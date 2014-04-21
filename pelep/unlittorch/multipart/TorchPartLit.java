package pelep.unlittorch.multipart;

import static pelep.unlittorch.config.ConfigCommon.*;
import static pelep.unlittorch.UnlitTorch.LOGGER;

import codechicken.lib.vec.BlockCoord;
import codechicken.multipart.IRandomDisplayTick;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import pelep.pcl.util.vec.Coordinates;
import pelep.unlittorch.block.BlockTorchLit;
import pelep.unlittorch.item.ItemTorchLit;
import pelep.unlittorch.packet.Packet03BurnFX;
import pelep.unlittorch.packet.Packet05UpdatePart;

import java.util.Random;

/**
 * @author pelep
 */
public class TorchPartLit extends TorchPart implements IRandomDisplayTick
{
    private Chunk chunk;

    public TorchPartLit() {}

    public TorchPartLit(int md, int age, boolean eternal)
    {
        super(md, age, eternal);
    }

    @Override
    public Block getBlock()
    {
        return BlockTorchLit.instance;
    }

    @Override
    public String getType()
    {
        return "unlittorch:torch_lit";
    }

    @Override
    public void randomDisplayTick(Random rand)
    {
        BlockTorchLit.displayFlame(world(), x(), y(), z(), meta);
    }

    @Override
    public void onEntityCollision(Entity e)
    {
        if (world().isRemote || e.onGround || !(e instanceof EntityArrow)) return;
        if (MathHelper.sqrt_double(e.motionX * e.motionX + e.motionY * e.motionY + e.motionZ * e.motionZ) > 1.2D)
            extinguishPart("fire.fire", 1F);
    }

    @Override
    public boolean activate(EntityPlayer ep, MovingObjectPosition mop, ItemStack ist)
    {
        if (ep.isSneaking())
        {
            if (ist != null) return false;

            if (!world().isRemote)
            {
                ItemStack torch = new ItemStack(getBlockId(), 1, age);
                torch.setTagCompound(eternal ? new NBTTagCompound() : null);
                ep.inventory.setInventorySlotContents(ep.inventory.currentItem, torch);
                tile().remPart(this);
            }

            return true;
        }
        else if (ist != null)
        {
            int id = ist.itemID;

            if (id == getBlockId())
            {
                renewTorches(ep, ist);
                return true;
            }
            else if (id == blockIdTorchUnlit)
            {
                BlockTorchLit.igniteHeldTorch(world(), ist, ep);
                return true;
            }
            else if (id == itemIdCloth)
            {
                if (ist.getItemDamage() == 0)
                {
                    extinguishPart("fire.fire", 1F);
                    if (!ep.capabilities.isCreativeMode) ep.inventory.decrStackSize(ep.inventory.currentItem, 1);
                }
                else
                {
                    extinguishPart("random.fizz", 0.3F);
                }

                return true;
            }
            else if (id == Item.bucketMilk.itemID || id == Item.bucketWater.itemID)
            {
                extinguishPart("random.fizz", 0.3F);
                return true;
            }
            else if (id == Block.cloth.blockID || id == Block.carpet.blockID)
            {
                extinguishPart("fire.fire", 1F);
                if (!ep.capabilities.isCreativeMode) ep.inventory.decrStackSize(ep.inventory.currentItem, 1);
                return true;
            }
            else if (id == Item.gunpowder.itemID)
            {
                if (!ep.capabilities.isCreativeMode) ep.inventory.decrStackSize(ep.inventory.currentItem, 5);
                int size = ist.stackSize;
                float strength = size < 5 ? (size * 0.2F) : 1F;
                world().newExplosion(ep, x(), y(), z(), strength, size > 5, true);
                return true;
            }
        }

        return false;
    }

    @Override
    public void onConverted()
    {
        //because multipart is weird and isn't adding this automagically
        if (!world().isRemote && torchUpdates) world().addTileEntity(tile());
    }

    @Override
    public boolean doesTick()
    {
        return torchUpdates && !eternal;
    }

    @Override
    public void update()
    {
        if (eternal || !torchUpdates || world().getTotalWorldTime() % ItemTorchLit.UPDATE_INTERVAL != 0) return;

        if (age >= torchLifespanMax)
        {
            if (torchSingleUse)
            {
                destroyPart();
            }
            else
            {
                extinguishPart("fire.fire", 1F);
            }

            return;
        }

        if (!world().isRemote)
        {
            if (isWet() && world().rand.nextInt(3) == 0)
            {
                extinguishPart("random.fizz", 0.3F);
                return;
            }

            if (age > torchLifespanMin && torchRandomKillChance > 0 && world().rand.nextInt(torchRandomKillChance) == 0)
            {
                if (world().rand.nextInt(100) < torchDestroyChance)
                {
                    destroyPart();
                }
                else
                {
                    extinguishPart("fire.fire", 1F);
                }

                return;
            }

            if (chunk == null) chunk = world().getChunkFromBlockCoords(x(), z());
            chunk.setChunkModified();
        }

        age++;
    }


    //----------------------------------util----------------------------------//


    private void extinguishPart(String sound, float volume)
    {
        if (world().isRemote) return;
        World world = world();
        BlockCoord pos = new BlockCoord(tile());
        tile().remPart(this);
        TileMultipart.addPart(world, pos, new TorchPartUnlit(meta, age, eternal));
        if (!"".equals(sound))
            world.playSoundEffect(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, sound, volume, world.rand.nextFloat() * 0.4F + 0.8F);
    }

    private void destroyPart()
    {
        if (world().isRemote) return;
        Packet03BurnFX pkt = new Packet03BurnFX(x(), y(), z(), meta);
        PacketDispatcher.sendPacketToAllAround(x(), y(), z(), 64D, world().provider.dimensionId, pkt.create());
        world().playSoundEffect(x() + 0.5, y() + 0.5, z() + 0.5, "fire.fire", 1F, world().rand.nextFloat() * 0.4F + 0.8F);
        tile().remPart(this);
    }

    private void renewTorches(EntityPlayer p, ItemStack ist)
    {
        int ia = ist.getItemDamage();
        if (age == ia) return;

        double d = (age + ia) / 2;
        int na = MathHelper.ceiling_double_int(d);
        ist.setItemDamage(na);
        p.swingItem();

        if (world().isRemote) return;

        age = na;
        world().playSoundEffect(x() + 0.5, y() + 0.5, z() + 0.5, "fire.fire", 1F, world().rand.nextFloat() * 0.4F + 0.8F);

        int dim = world().provider.dimensionId;
        int i = tile().jPartList().indexOf(this);
        Coordinates pos = new Coordinates(x(), y(), z());
        PacketDispatcher.sendPacketToAllInDimension(new Packet05UpdatePart(i, pos, dim, age).create(), dim);
    }

    //replacement for world#canLightningStrikeAt(x, y, z)
    private boolean isWet()
    {
        if (!world().isRaining())
        {
            return false;
        }
        else if (!world().canBlockSeeTheSky(x(), y(), z()))
        {
            return false;
        }
        else if (world().getPrecipitationHeight(x(), z()) > y() + 1) //multipart sorta fucks this part up
        {
            return false;
        }

        return true;
    }

    public static void updatePart(World world, Coordinates coord, int index, int age)
    {
        TileEntity te = world.getBlockTileEntity(coord.x, coord.y, coord.z);

        if (te instanceof TileMultipart)
        {
            try
            {
                TileMultipart tm = (TileMultipart) te;
                TMultiPart part = tm.jPartList().get(index);

                if (part != null && "unlittorch:torch_lit".equals(part.getType()))
                    ((TorchPartLit)part).age = age;
            }
            catch (IndexOutOfBoundsException e)
            {
                LOGGER.warning("Index %d for TileMultipart at (%d,%d,%d) is out of bounds", index, coord.x, coord.y, coord.z);
            }
        }
    }
}
