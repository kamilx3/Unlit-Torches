package pelep.unlittorch.multipart;

import static codechicken.multipart.minecraft.TorchPart.metaSideMap;
import static codechicken.multipart.minecraft.TorchPart.sideMetaMap;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.minecraft.McSidedMetaPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.render.RenderBlockTorch;

import java.util.Arrays;

/**
 * @author pelep
 */
abstract class TorchPart extends McSidedMetaPart
{
    protected int age;
    protected boolean eternal;

    protected TorchPart() {}

    protected TorchPart(int md, int age, boolean eternal)
    {
        super(md);
        this.age = age;
        this.eternal = eternal;
    }

    public static TorchPart getPart(World world, BlockCoord pos, int side, boolean lit, int age, boolean eternal)
    {
        if (side == 0) return null;

        pos = pos.copy().offset(side^1);

        if (!world.isBlockSolidOnSide(pos.x, pos.y, pos.z, ForgeDirection.getOrientation(side)))
        {
            if (side != 1) return null;
            Block block = Block.blocksList[world.getBlockId(pos.x, pos.y, pos.z)];
            if (block == null || !block.canPlaceTorchOnTop(world, pos.x, pos.y, pos.z)) return null;
        }

        return lit ? new TorchPartLit(sideMetaMap[side^1], age, eternal) : new TorchPartUnlit(sideMetaMap[side^1], age, eternal);
    }

    @Override
    public int sideForMeta(int md)
    {
        return metaSideMap[md];
    }

    @Override
    public Cuboid6 getBounds()
    {
        float adj = 0.15F;

        switch (meta)
        {
            case 1:
                return new Cuboid6(0F, 0.2F, 0.5F - adj, adj * 2F, 0.8F, 0.5F + adj);
            case 2:
                return new Cuboid6(1F - adj * 2F, 0.2F, 0.5F - adj, 1F, 0.8F, 0.5F + adj);
            case 3:
                return new Cuboid6(0.5F - adj, 0.2F, 0F, 0.5F + adj, 0.8F, adj * 2F);
            case 4:
                return new Cuboid6(0.5F - adj, 0.2F, 1F - adj * 2F, 0.5F + adj, 0.8F, 1F);
            default:
                adj = 0.1F;
                return new Cuboid6(0.5F - adj, 0F, 0.5F - adj, 0.5F + adj, 0.6F, 0.5F + adj);
        }
    }

    @Override
    public boolean canStay()
    {
        if (sideForMeta(meta) == 0)
        {
            Block block = Block.blocksList[world().getBlockId(x(), y() - 1, z())];
            if (block != null && block.canPlaceTorchOnTop(world(), x(), y() - 1, z())) return true;
        }

        return super.canStay();
    }

    @Override
    public void drop()
    {
        int id = ConfigCommon.torchDropsUnlit ? ConfigCommon.blockIdTorchUnlit : getBlockId();
        ItemStack ist = new ItemStack(id, 1, age);
        ist.setTagCompound(eternal ? new NBTTagCompound() : null);
        TileMultipart.dropItem(ist, world(), Vector3.fromTileEntityCenter(tile()));
        tile().remPart(this);
    }

    @Override
    public Iterable<ItemStack> getDrops()
    {
        int id = ConfigCommon.torchDropsUnlit ? ConfigCommon.blockIdTorchUnlit : getBlockId();
        ItemStack ist = new ItemStack(id, 1, age);
        ist.setTagCompound(eternal ? new NBTTagCompound() : null);
        return Arrays.asList(ist);
    }

    @Override
    public void save(NBTTagCompound tag)
    {
        super.save(tag);
        tag.setInteger("age", age);
        tag.setBoolean("eternal", eternal);
    }

    @Override
    public void load(NBTTagCompound tag)
    {
        super.load(tag);
        age = tag.getInteger("age");
        eternal = tag.getBoolean("eternal");
    }

    @Override
    public void writeDesc(MCDataOutput pkt)
    {
        super.writeDesc(pkt);
        pkt.writeInt(age);
        pkt.writeBoolean(eternal);
    }

    @Override
    public void readDesc(MCDataInput pkt)
    {
        super.readDesc(pkt);
        age = pkt.readInt();
        eternal = pkt.readBoolean();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderDynamic(Vector3 pos, float frame, int pass)
    {
        if (pass != 0 || !Minecraft.isGuiEnabled() || !Minecraft.getMinecraft().gameSettings.showDebugInfo)
            return;

        if (Minecraft.getMinecraft().thePlayer.getDistance(x() + 0.5, y() + 0.5, z() + 0.5) <= 80D)
        {
            String str = ConfigCommon.torchLifespanMax - age + "";

            float x = (float)pos.x + 0.5F;
            float y = (float)pos.y + 0.8F;
            float z = (float)pos.z + 0.5F;

            switch (meta)
            {
                case 1: x -= 0.25F; break;
                case 2: x += 0.25F; break;
                case 3: z -= 0.25F; break;
                case 4: z += 0.25F; break;
                default: y += 0.25F;
            }

            RenderBlockTorch.renderAge(str, x, y, z, frame, 1F/120F);
        }
    }

    protected void grabPart(EntityPlayer ep)
    {
        if (world().isRemote) return;
        ItemStack torch = new ItemStack(getBlockId(), 1, age);
        torch.setTagCompound(eternal ? new NBTTagCompound() : null);
        ep.inventory.setInventorySlotContents(ep.inventory.currentItem, torch);
        tile().remPart(this);
    }
}
