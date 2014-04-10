package pelep.unlittorch.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.tileentity.TileEntityTorch;

/**
 * @author pelep
 */
@SideOnly(Side.CLIENT)
public class RenderBlockTorch extends TileEntitySpecialRenderer
{
    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float ptick)
    {
        if (Minecraft.isGuiEnabled() && Minecraft.getMinecraft().gameSettings.showDebugInfo)
        {
            EntityLivingBase ep = this.tileEntityRenderer.entityLivingPlayer;

            if (te.getDistanceFrom(ep.posX, ep.posY, ep.posZ) <= 80D)
            {
                FontRenderer fr = this.getFontRenderer();
                Tessellator t = Tessellator.instance;
                String age = (ConfigCommon.torchLifespanMax - ((TileEntityTorch)te).getAge()) + "";

                int w = fr.getStringWidth(age) / 2;

                float scale = 1F / 60F;
                float viewX = ep.prevRotationPitch + (ep.rotationPitch - ep.prevRotationPitch) * ptick;
                float viewY = ep.prevRotationYaw + (ep.rotationYaw - ep.prevRotationYaw) * ptick;
                float ay = te.getBlockMetadata() == 5 ? 1.05F : 0.9F;

                GL11.glPushMatrix();
                GL11.glTranslatef((float)x + 0.5F, (float)y + ay, (float)z + 0.5F);
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
        }
    }
}
