package pelep.unlittorch.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import pelep.unlittorch.block.BlockLanternLit;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.entity.TileEntityLantern;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderBlockLantern extends TileEntitySpecialRenderer
{
    public static final ResourceLocation TEXTURE_LANTERN_ON = new ResourceLocation("unlittorch:textures/blocks/lantern_on.png");
    public static final ResourceLocation TEXTURE_LANTERN_OFF = new ResourceLocation("unlittorch:textures/blocks/lantern_off.png");
    
    private ModelLantern ground = new ModelLantern().ground();
    private ModelLantern wall = new ModelLantern().wall();
    private ModelLantern ceiling = new ModelLantern().ceiling();
    
    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float ptick)
    {
        ResourceLocation rl = te.getBlockType() instanceof BlockLanternLit ? TEXTURE_LANTERN_ON : TEXTURE_LANTERN_OFF;
        this.func_110628_a(rl);
        
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
        GL11.glScalef(1F, 1F, 1F);
        
        int md = te.getBlockMetadata();
        
        switch (md)
        {
        case 1: case 5: case 9:
            GL11.glRotatef(180F, 0F, 1F, 0F);
            break;
        case 2: case 6: case 10:
            GL11.glRotatef(90F, 0F, -1F, 0F);
            break;
        case 3: case 7: case 11:
            GL11.glRotatef(90F, 0F, 1F, 0F);
        }
        
        switch (md)
        {
        case 0: case 1: case 2: case 3:
            this.wall.renderOnWall();
            break;
        case 4: case 5: case 6: case 7:
            this.ceiling.renderOnCeiling();
            break;
        case 8: case 9: case 10: case 11:
            this.ground.renderOnGround(((TileEntityLantern)te).hasHandle());
        }
        
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        
        if (Minecraft.isGuiEnabled() && Minecraft.getMinecraft().gameSettings.showDebugInfo)
        {
            EntityLivingBase ep = this.tileEntityRenderer.entityLivingPlayer;
            
            if (te.getDistanceFrom(ep.posX, ep.posY, ep.posZ) <= 80D)
            {
                FontRenderer fr = this.getFontRenderer();
                Tessellator t = Tessellator.instance;
                String age = (ConfigCommon.lanternLifespanMax - ((TileEntityLantern)te).getAge()) + "";
                
                int w = fr.getStringWidth(age) / 2;
                
                float scale = 0.016666668F;
                float viewX = ep.prevRotationPitch + (ep.rotationPitch - ep.prevRotationPitch) * ptick;
                float viewY = ep.prevRotationYaw + (ep.rotationYaw - ep.prevRotationYaw) * ptick;
                float ay = (md < 8 && md > 3) ? 1.05F : 0.9F;
                
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
