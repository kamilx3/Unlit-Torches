package pelep.unlittorch.render;

import static pelep.unlittorch.render.RenderBlockLantern.TEXTURE_LANTERN_ON;
import static pelep.unlittorch.render.RenderBlockLantern.TEXTURE_LANTERN_OFF;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderItemLantern implements IItemRenderer
{
    private ModelLantern entity = new ModelLantern().entity();
    private ModelLantern equipped = new ModelLantern();
    
    @Override
    public boolean handleRenderType(ItemStack ist, ItemRenderType type)
    {
        return (ist.itemID == ConfigCommon.blockIdLanternLit ||
                ist.itemID == ConfigCommon.blockIdLanternUnlit) &&
                (type == ItemRenderType.ENTITY ||
                type == ItemRenderType.EQUIPPED ||
                type == ItemRenderType.EQUIPPED_FIRST_PERSON ||
                type == ItemRenderType.INVENTORY);
    }
    
    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack ist, ItemRendererHelper helper)
    {
        return type != ItemRenderType.INVENTORY;
    }
    
    @Override
    public void renderItem(ItemRenderType type, ItemStack ist, Object... data)
    {
        ResourceLocation rl = ist.itemID == ConfigCommon.blockIdLanternLit ? TEXTURE_LANTERN_ON : TEXTURE_LANTERN_OFF;
        RenderBlocks rb = (RenderBlocks) data[0];
        boolean handle = ist.stackTagCompound != null && ist.stackTagCompound.getBoolean("handle");
        
        rb.minecraftRB.renderEngine.func_110577_a(rl);
        
        if (type == ItemRenderType.ENTITY)
        {
            float y = RenderItem.renderInFrame ? 0.05F : 0.15F;
            
            GL11.glScalef(1.5F, 1.5F, 1.5F);
            GL11.glTranslatef(0F, y, 0F);
            
            this.entity.renderAsEntity(handle);
        }
        else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            EntityClientPlayerMP p = (EntityClientPlayerMP) data[1];
            RenderPlayer rp = (RenderPlayer) RenderManager.instance.getEntityRenderObject(p);
            
            if (handle)
            {
                GL11.glScalef(1.5F, 1.5F, 1.5F);
                GL11.glRotatef(45F, 0F, 1F, 0F);
                GL11.glTranslatef(-0.1F, 0.75F, 0.5F);
                
                this.equipped.renderAsEquipped(true);
                
                GL11.glTranslatef(-0.5F, -0.1F, -0.1F);
                GL11.glRotatef(60F, 0F, 0F, -1F);
                GL11.glScalef(1F, 1F, 1F);
                
                rb.minecraftRB.renderEngine.func_110577_a(p.func_110306_p());
                rp.renderFirstPersonArm(p);
            }
            else
            {
                GL11.glScalef(1.5F, 1.5F, 1.5F);
                GL11.glRotatef(45F, 0F, 1F, 0F);
                GL11.glTranslatef(0F, 0.8F, 0.5F);
                
                this.equipped.renderAsEquipped(false);
                
                GL11.glTranslatef(-0.75F, -0.3F, 0F);
                GL11.glRotatef(60F, 0F, 0F, -1F);
                GL11.glRotatef(20F, 0F, 0F, -1F);
                GL11.glScalef(1F, 1F, 1F);
                
                rb.minecraftRB.renderEngine.func_110577_a(p.func_110306_p());
                rp.renderFirstPersonArm(p);
            }
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            EntityLivingBase el = (EntityLivingBase) data[1];
            
            if (el instanceof EntityPlayer ||
               (el.getClass().getSimpleName().equals("EntityDoppelganger") &&
                el.getClass().getPackage().toString().equals("package pelep.witchcraft.entity")))
            {
                if (handle)
                {
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    GL11.glRotatef(45F, 0F, -1F, 0F);
                    GL11.glTranslatef(0.9F, -0.3F, 0F);
                    this.equipped.renderAsEquipped(true);
                }
                else
                {
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    GL11.glRotatef(45F, 0F, -1F, 0F);
                    GL11.glTranslatef(0.75F, 0.15F, 0F);
                    this.equipped.renderAsEquipped(false);
                }
            }
            else if (el.getClass() == EntityVillager.class)
            {
                GL11.glScalef(1.5F, 1.5F, 1.5F);
                GL11.glRotatef(45F, 0F, 1F, 0F);
                GL11.glRotatef(20F, 1F, 0F, 0F);
                GL11.glTranslatef(-0.1F, 0.5F, 0.1F);
                this.equipped.renderAsEquipped(true);
            }
            else if (el.getClass() == EntityZombie.class && !el.isChild())
            {
                if (handle)
                {
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    GL11.glRotatef(45F, 0F, -1F, 0F);
                    GL11.glRotatef(70F, 0F, 0F, 1F);
                    GL11.glTranslatef(0.6F, -1.4F, 0F);
                    this.equipped.renderAsEquipped(true);
                }
                else
                {
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    GL11.glRotatef(45F, 0F, -1F, 0F);
                    GL11.glRotatef(70F, 0F, 0F, 1F);
                    GL11.glTranslatef(0.45F, -1.05F, 0F);
                    this.equipped.renderAsEquipped(false);
                }
            }
            else
            {
                Icon icon = ist.getItem().getIconFromDamage(handle ? 1 : 0);
                this.renderFlat(rb, icon);
            }
        }
        else if (type == ItemRenderType.INVENTORY)
        {
            rb.minecraftRB.renderEngine.func_110577_a(TextureMap.field_110576_c);
            Icon icon = ist.getItem().getIconFromDamage(handle ? 1 : 0);
            
            double minU = icon.getMinU();
            double minV = icon.getMinV();
            double maxU = icon.getMaxU();
            double maxV = icon.getMaxV();
            
            Tessellator t = Tessellator.instance;
            
            t.startDrawingQuads();
            t.addVertexWithUV(0, 16, 0, minU, maxV);
            t.addVertexWithUV(16, 16, 0, maxU, maxV);
            t.addVertexWithUV(16, 0, 0, maxU, minV);
            t.addVertexWithUV(0, 0, 0, minU, minV);
            t.draw();
        }
    }
    
    private void renderFlat(RenderBlocks rb, Icon icon)
    {
        Tessellator t = Tessellator.instance;
        
        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();
        
        rb.minecraftRB.renderEngine.func_110577_a(TextureMap.field_110576_c);
        
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        
        float scale = 1F / 0.375F;
        GL11.glScalef(-scale, -scale, scale);
        GL11.glRotatef(45F, 0F, -1F, 0F);
        GL11.glRotatef(20F, -1F, 0F, 0F);
        GL11.glTranslatef(0.25F, 0F, 0.125F);
        
        GL11.glScalef(0.375F, 0.375F, 0.375F);
        GL11.glRotatef(60F, 0F, 0F, 1F);
        GL11.glRotatef(90F, -1F, 0F, 0F);
        GL11.glRotatef(20F, 0F, 0F, 1F);
        
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef(0F, -0.3F, 0F);
        GL11.glScalef(1.5F, 1.5F, 1.5F);
        GL11.glRotatef(50F, 0F, 1F, 0F);
        GL11.glRotatef(335F, 0F, 0F, 1F);
        GL11.glTranslatef(-0.9375F, -0.0625F, 0F);
        
        ItemRenderer.renderItemIn2D(t, maxU, minV, minU, maxV, icon.getOriginX(), icon.getOriginY(), 0.0625F);
        
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }
}
