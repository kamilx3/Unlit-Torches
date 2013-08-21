package pelep.unlittorch.render;

import org.lwjgl.opengl.GL11;

import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.entity.TileEntityTorch;
import pelep.unlittorch.proxy.ProxyCommon;
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
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockTorch extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler
{
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int rId, RenderBlocks rb)
    {
        Tessellator t = Tessellator.instance;
        t.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        t.setColorOpaque_F(1F, 1F, 1F);
        
        int md = world.getBlockMetadata(x, y, z);
        Icon icon = rb.hasOverrideBlockTexture() ? rb.overrideBlockTexture : rb.getBlockIconFromSideAndMetadata(block, 0, md);
        
        double os = 0.4000000059604645D;
        double dos = 0.5D - os;
        double hos = 0.20000000298023224D;
        
        switch (md)
        {
        case 1: case 6:
            this.renderTorch(t, icon, x - dos, y + hos, z, -os, 0D);
            break;
        case 2: case 7:
            this.renderTorch(t, icon, x + dos, y + hos, z, os, 0D);
            break;
        case 3: case 8:
            this.renderTorch(t, icon, x, y + hos, z - dos, 0D, -os);
            break;
        case 4: case 9:
            this.renderTorch(t, icon, x, y + hos, z + dos, 0D, os);
            break;
        default:
            this.renderTorch(t, icon, x, y, z, 0D, 0D);
        }
        
        return true;
    }
    
    @Override
    public void renderInventoryBlock(Block block, int md, int rId, RenderBlocks rb)
    {
    }
    
    @Override
    public boolean shouldRender3DInInventory()
    {
        return false;
    }
    
    @Override
    public int getRenderId()
    {
        return ProxyCommon.RID_TORCH;
    }
    
    private void renderTorch(Tessellator t, Icon icon, double x, double y, double z, double ax, double az)
    {
        double minU = icon.getMinU();
        double maxU = icon.getMaxU();
        double minV = icon.getMinV();
        double maxV = icon.getMaxV();
        
        double u7 = icon.getInterpolatedU(7D);
        double u9 = icon.getInterpolatedU(9D);
        
        double v6 = icon.getInterpolatedV(6D);
        double v8 = icon.getInterpolatedV(8D);
        
        double v13 = icon.getInterpolatedV(13D);
        double v15 = icon.getInterpolatedV(15D);
        
        x += 0.5D;
        z += 0.5D;
        
        double minX = x - 0.5D;
        double maxX = x + 0.5D;
        double minZ = z - 0.5D;
        double maxZ = z + 0.5D;
        
        double p = 0.0625D;
        double p10 = 0.625D;
        
        t.addVertexWithUV(x + ax * (1D - p10) - p, y + p10, z + az * (1D - p10) - p, u7, v6);
        t.addVertexWithUV(x + ax * (1D - p10) - p, y + p10, z + az * (1D - p10) + p, u7, v8);
        t.addVertexWithUV(x + ax * (1D - p10) + p, y + p10, z + az * (1D - p10) + p, u9, v8);
        t.addVertexWithUV(x + ax * (1D - p10) + p, y + p10, z + az * (1D - p10) - p, u9, v6);
        
        t.addVertexWithUV(x + p + ax, y, z - p + az, u9, v13);
        t.addVertexWithUV(x + p + ax, y, z + p + az, u9, v15);
        t.addVertexWithUV(x - p + ax, y, z + p + az, u7, v15);
        t.addVertexWithUV(x - p + ax, y, z - p + az, u7, v13);
        
        t.addVertexWithUV(x - p + 0D, y + 1D, minZ + 0D, minU, minV);
        t.addVertexWithUV(x - p + ax, y + 0D, minZ + az, minU, maxV);
        t.addVertexWithUV(x - p + ax, y + 0D, maxZ + az, maxU, maxV);
        t.addVertexWithUV(x - p + 0D, y + 1D, maxZ + 0D, maxU, minV);
        
        t.addVertexWithUV(x + p + 0D, y + 1D, maxZ + 0D, minU, minV);
        t.addVertexWithUV(x + p + ax, y + 0D, maxZ + az, minU, maxV);
        t.addVertexWithUV(x + p + ax, y + 0D, minZ + az, maxU, maxV);
        t.addVertexWithUV(x + p + 0D, y + 1D, minZ + 0D, maxU, minV);
        
        t.addVertexWithUV(minX + 0D, y + 1D, z + p + 0D, minU, minV);
        t.addVertexWithUV(minX + ax, y + 0D, z + p + az, minU, maxV);
        t.addVertexWithUV(maxX + ax, y + 0D, z + p + az, maxU, maxV);
        t.addVertexWithUV(maxX + 0D, y + 1D, z + p + 0D, maxU, minV);
        
        t.addVertexWithUV(maxX + 0D, y + 1D, z - p + 0D, minU, minV);
        t.addVertexWithUV(maxX + ax, y + 0D, z - p + az, minU, maxV);
        t.addVertexWithUV(minX + ax, y + 0D, z - p + az, maxU, maxV);
        t.addVertexWithUV(minX + 0D, y + 1D, z - p + 0D, maxU, minV);
    }
    
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
                
                float scale = 0.016666668F;
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
