package pelep.unlittorch.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelLantern extends ModelBase
{
    private ModelRenderer handle = new ModelRenderer(this, "handle");
    private ModelRenderer lantern = new ModelRenderer(this, "lantern");
    
    public ModelLantern()
    {
        this.textureWidth = 40;
        this.textureHeight = 40;
        
        this.handle.setTextureSize(this.textureWidth, this.textureHeight);
        this.lantern.setTextureSize(this.textureWidth, this.textureHeight);
        
        this.handle.setRotationPoint(0F, 0F, 0F);
        this.lantern.setRotationPoint(0F, 0F, 0F);
        this.setAngles(this.handle, 0F, 0F, 0F);
        this.setAngles(this.lantern, 0F, 0F, 0F);
        
        this.setTextureOffset("handle.top", 1, 27);
        this.setTextureOffset("handle.right", 35, 30);
        this.setTextureOffset("handle.left", 29, 30);
        this.handle.addBox("top", -0.5F, 8.5F, -6F, 1, 1, 12);
        this.handle.addBox("right", -0.5F, -0.5F, 5F, 1, 9, 1);
        this.handle.addBox("left", -0.5F, -0.5F, -6F, 1, 9, 1);
        
        this.setTextureOffset("lantern.lid", 16, 13);
        this.setTextureOffset("lantern.top", 0, 0);
        this.setTextureOffset("lantern.frameh", 23, 29);
        this.setTextureOffset("lantern.framew", 1, 26);
        this.setTextureOffset("lantern.framel", 1, 32);
        this.setTextureOffset("lantern.glass", 16, 14);
        this.setTextureOffset("lantern.bottom", 0, 0);
        this.setTextureOffset("lantern.flamex", 32, 3);
        this.setTextureOffset("lantern.flamez", 32, 0);
        this.lantern.addBox("lid", -3F, 0.5F, -3F, 6, 1, 6);
        this.lantern.addBox("top", -5F, -1.5F, -5F, 10, 2, 10);
        this.lantern.addBox("frameh", -3F, -9.5F, -3F, 1, 8, 1);
        this.lantern.addBox("frameh", -3F, -9.5F, 2F, 1, 8, 1);
        this.lantern.addBox("frameh", 2F, -9.5F, -3F, 1, 8, 1);
        this.lantern.addBox("frameh", 2F, -9.5F, 2F, 1, 8, 1);
        this.lantern.addBox("framew", -3F, -9.5F, -2F, 1, 1, 4);
        this.lantern.addBox("framew", 2F, -9.5F, -2F, 1, 1, 4);
        this.lantern.addBox("framel", -2F, -9.5F, -3F, 4, 1, 1);
        this.lantern.addBox("framel", -2F, -9.5F, 2F, 4, 1, 1);
        this.lantern.addBox("glass", -3F, -9.5F, -3F, 6, 8, 6);
        this.lantern.addBox("bottom", -5F, -11.5F, -5F, 10, 2, 10);
        this.lantern.addBox("flamex", -1.5F, -9.5F, 0F, 3, 6, 0);
        this.lantern.addBox("flamez", 0F, -9.5F, -1.5F, 0, 6, 3);
    }
    
    public ModelLantern ground()
    {
        this.handle.setRotationPoint(0F, -4.5F, 0F);
        this.lantern.setRotationPoint(0F, -4.5F, 0F);
        this.setAngles(this.handle, 0F, 0F, -2.5F);
        this.setAngles(this.lantern, 0F, 0F, 0F);
        return this;
    }
    
    public ModelLantern wall()
    {
        this.handle.setRotationPoint(-11F, 0F, 0F);
        this.lantern.setRotationPoint(-11F, 0F, 0F);
        this.setAngles(this.handle, 0F, 0F, 0.2F);
        this.setAngles(this.lantern, 0F, 0F, 0F);
        return this;
    }
    
    public ModelLantern ceiling()
    {
        this.handle.setRotationPoint(0F, 3.5F, 0F);
        this.lantern.setRotationPoint(0F, 3.5F, 0F);
        return this;
    }
    
    public ModelLantern entity()
    {
        this.handle.setRotationPoint(0F, 4.5F, 0F);
        this.lantern.setRotationPoint(0F, 4.5F, 0F);
        this.setAngles(this.handle, 0F, 0F, -2.5F);
        this.setAngles(this.lantern, 0F, 0F, 0F);
        return this;
    }
    
    public void renderOnGround(boolean handle)
    {
        float scale = 0.03125F;
        this.lantern.render(scale);
        if (handle) this.handle.render(scale);
    }
    
    public void renderOnWall()
    {
        float scale = 0.03125F;
        this.handle.render(scale);
        this.lantern.render(scale);
    }
    
    public void renderOnCeiling()
    {
        float scale = 0.03125F;
        this.handle.render(scale);
        this.lantern.render(scale);
    }
    
    public void renderAsEntity(boolean handle)
    {
        float scale = 0.07F;
        this.lantern.render(scale);
        if (handle) this.handle.render(scale);
    }
    
    public void renderAsEquipped(boolean handle)
    {
        float scale = 0.0625F;
        this.lantern.render(scale);
        if (handle) this.handle.render(scale);
    }
    
    private void setAngles(ModelRenderer mr, float ax, float ay, float az)
    {
        mr.rotateAngleX = ax;
        mr.rotateAngleY = ay;
        mr.rotateAngleZ = az;
    }
}
