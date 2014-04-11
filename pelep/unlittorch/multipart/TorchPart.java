package pelep.unlittorch.multipart;

import static codechicken.multipart.minecraft.TorchPart.metaSideMap;
import static codechicken.multipart.minecraft.TorchPart.sideMetaMap;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.MultipartRenderer;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.minecraft.McSidedMetaPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import org.lwjgl.opengl.GL11;
import pelep.unlittorch.config.ConfigCommon;

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

        EntityLivingBase ep = Minecraft.getMinecraft().thePlayer;

        if (ep.getDistance(x() + 0.5, y() + 0.5, z() + 0.5) <= 80D)
        {
            FontRenderer fr = MultipartRenderer.getFontRenderer();
            Tessellator t = Tessellator.instance;
            String str = ConfigCommon.torchLifespanMax - age + "";

            int w = fr.getStringWidth(str) / 2;

            float scale = 1F / 120F;
            float viewX = ep.prevRotationPitch + (ep.rotationPitch - ep.prevRotationPitch) * frame;
            float viewY = ep.prevRotationYaw + (ep.rotationYaw - ep.prevRotationYaw) * frame;

            float ax = 0F;
            float ay = 0.8F;
            float az = 0F;

            switch (meta)
            {
                case 1: ax = -0.25F; break;
                case 2: ax = 0.25F; break;
                case 3: az = -0.25F; break;
                case 4: az = 0.25F; break;
                default: ay = 1.05F;
            }

            GL11.glPushMatrix();
            GL11.glTranslatef((float)pos.x + 0.5F + ax, (float)pos.y + ay, (float)pos.z + 0.5F + az);
            GL11.glNormal3f(0F, 1F, 0F);
            GL11.glRotatef(-viewY, 0F, 1F, 0F);
            GL11.glRotatef(viewX, 1F, 0F, 0F);
            GL11.glScalef(-scale, -scale, scale);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glTranslatef(0F, 0.25F / scale, 0F);
            GL11.glDepthMask(false);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_TEXTURE_2D);

            t.startDrawingQuads();
            t.setColorRGBA_F(0F, 0F, 0F, 0.25F);
            t.addVertex((-w - 1), -1D, 0D);
            t.addVertex((-w - 1), 8D, 0D);
            t.addVertex((w + 1), 8D, 0D);
            t.addVertex((w + 1), -1D, 0D);
            t.draw();

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(true);

            fr.drawString(str, -fr.getStringWidth(str) / 2, 0, -1);

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glPopMatrix();
        }
    }
}
