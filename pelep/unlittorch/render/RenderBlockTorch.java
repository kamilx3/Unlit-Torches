package pelep.unlittorch.render;

import static pelep.unlittorch.block.BlockTorchUnlit.RENDER_ID;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.tileentity.TileEntityTorch;

/**
 * @author pelep
 */
@SideOnly(Side.CLIENT)
public class RenderBlockTorch extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler
{
    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float ptick)
    {
        if (Minecraft.isGuiEnabled() && Minecraft.getMinecraft().gameSettings.showDebugInfo)
        {
            EntityLivingBase ep = tileEntityRenderer.entityLivingPlayer;

            if (te.getDistanceFrom(ep.posX, ep.posY, ep.posZ) <= 80D)
            {
                String age = (ConfigCommon.torchLifespanMax - ((TileEntityTorch)te).age) + "";
                x += 0.5F;
                y += te.getBlockMetadata() == 5 ? 1.05F : 0.9F;
                z += 0.5F;
                renderAge(age, (float)x, (float)y, (float)z, ptick, 1F/60F);
            }
        }
    }

    public static void renderAge(String age, float x, float y, float z, float ptick, float scale)
    {
        Tessellator t = Tessellator.instance;
        EntityLivingBase ep = Minecraft.getMinecraft().thePlayer;
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        int w = fr.getStringWidth(age) / 2;

        float viewX = ep.prevRotationPitch + (ep.rotationPitch - ep.prevRotationPitch) * ptick;
        float viewY = ep.prevRotationYaw + (ep.rotationYaw - ep.prevRotationYaw) * ptick;

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
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

        fr.drawString(age, -fr.getStringWidth(age) / 2, 0, -1);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopMatrix();
    }

    @Override
    public void renderInventoryBlock(Block block, int md, int rId, RenderBlocks rb)
    {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int rId, RenderBlocks rb)
    {
        TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
        Icon icon = te.age >= ConfigCommon.torchLifespanMax ? block.getIcon(1, 1) : block.getIcon(0, 0);
        icon = rb.getIconSafe(icon);

        rb.setOverrideBlockTexture(icon);
        rb.renderBlockTorch(block, x, y, z);
        rb.clearOverrideBlockTexture();

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory()
    {
        return false;
    }

    @Override
    public int getRenderId()
    {
        return RENDER_ID;
    }
}
