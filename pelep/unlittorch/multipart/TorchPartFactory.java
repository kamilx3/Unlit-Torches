package pelep.unlittorch.multipart;

import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultiPartRegistry.IPartConverter;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.minecraft.McBlockPart;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet15Place;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.packet.Packet05PlacePart;
import pelep.unlittorch.tileentity.TileEntityTorch;

/**
 * @author pelep
 */
public class TorchPartFactory implements IPartFactory, IPartConverter
{
    public TorchPartFactory()
    {
        MultiPartRegistry.registerConverter(this);
        MultiPartRegistry.registerParts(this, new String[]{"unlittorch:torch_lit", "unlittorch:torch_unlit",});
        MinecraftForge.EVENT_BUS.register(this);
    }

    //for loading
    @Override
    public TMultiPart createPart(String name, boolean client)
    {
        if ("unlittorch:torch_lit".equals(name))
        {
            return new TorchPartLit();
        }
        else if ("unlittorch:torch_unlit".equals(name))
        {
            return new TorchPartUnlit();
        }

        return null;
    }

    @Override
    public boolean canConvert(int id)
    {
        return id == ConfigCommon.blockIdTorchLit || id == ConfigCommon.blockIdTorchUnlit;
    }

    @Override
    public TMultiPart convert(World world, BlockCoord pos)
    {
        int id = world.getBlockId(pos.x, pos.y, pos.z);

        if (id == ConfigCommon.blockIdTorchLit)
        {
            TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(pos.x, pos.y, pos.z);
            return new TorchPartLit(world.getBlockMetadata(pos.x, pos.y, pos.z), te.getAge(), te.isEternal());
        }
        else if (id == ConfigCommon.blockIdTorchUnlit)
        {
            TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(pos.x, pos.y, pos.z);
            return new TorchPartUnlit(world.getBlockMetadata(pos.x, pos.y, pos.z), te.getAge(), te.isEternal());
        }

        return null;
    }

    @ForgeSubscribe
    public void playerInteract(PlayerInteractEvent e)
    {
        if (e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && e.entityPlayer.worldObj.isRemote)
        {
            if (place(e.entityPlayer, e.entityPlayer.worldObj)) e.setCanceled(true);
        }
    }

    public static boolean place(EntityPlayer ep, World world)
    {
        MovingObjectPosition hit = RayTracer.reTrace(world, ep);
        if (hit == null) return false;

        ItemStack held = ep.getHeldItem();
        if (held == null) return false;

        BlockCoord pos = new BlockCoord(hit.blockX, hit.blockY, hit.blockZ).offset(hit.sideHit);
        McBlockPart part = null;

        if (held.itemID == ConfigCommon.blockIdTorchLit)
        {
            part = TorchPart.getPart(world, pos, hit.sideHit, true, held.getItemDamage(), held.stackTagCompound != null);
        }
        else if (held.itemID == ConfigCommon.blockIdTorchUnlit)
        {
            part = TorchPart.getPart(world, pos, hit.sideHit, false, held.getItemDamage(), held.stackTagCompound != null);
        }

        if (part == null) return false;

        if (world.isRemote && !ep.isSneaking())
        {
            Vector3 vec = new Vector3(hit.hitVec).add(-hit.blockX, -hit.blockY, -hit.blockZ);
            Block block = Block.blocksList[world.getBlockId(hit.blockX, hit.blockY, hit.blockZ)];
            float x = (float) vec.x;
            float y = (float) vec.y;
            float z = (float) vec.z;

            if(block != null && !(block instanceof BlockFence) && block.onBlockActivated(world, hit.blockX, hit.blockY, hit.blockZ, ep, hit.sideHit, x, y, z))
            {
                ep.swingItem();
                PacketDispatcher.sendPacketToServer(new Packet15Place(hit.blockX, hit.blockY, hit.blockZ, hit.sideHit, ep.inventory.getCurrentItem(), x, y, z));
                return false;
            }
        }

        TileMultipart tile = TileMultipart.getOrConvertTile(world, pos);
        if (tile == null || !tile.canAddPart(part)) return false;

        if (!world.isRemote)
        {
            TileMultipart.addPart(world, pos, part);
            String sound = part.getBlock().stepSound.getPlaceSound();
            float volume = part.getBlock().stepSound.getVolume();
            float pitch = part.getBlock().stepSound.getPitch();
            world.playSoundEffect(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, sound, (volume + 1F) / 2F, pitch * 0.8F);

            if(!ep.capabilities.isCreativeMode && --held.stackSize <= 0)
            {
                ep.inventory.mainInventory[ep.inventory.currentItem] = null;
                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(ep, held));
            }
        }
        else
        {
            ep.swingItem();
            PacketDispatcher.sendPacketToServer(new Packet05PlacePart().create());
        }

        return true;
    }
}
