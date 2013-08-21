package pelep.unlittorch.ai;

import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityAIHandleLanterns extends EntityAIBase
{
    private EntityLivingBase el;
    private World world;
    
    public EntityAIHandleLanterns(EntityLivingBase el)
    {
        this.el = el;
        this.world = el.worldObj;
    }

    @Override
    public boolean shouldExecute()
    {
        ItemStack ist = this.el.getCurrentItemOrArmor(0);
        
        if (ist == null)
        {
            return false;
        }
        else if (this.world.isDaytime())
        {
            if (this.world.isRaining())
            {
                return ist.itemID == ConfigCommon.blockIdLanternUnlit;
            }
            else
            {
                return ist.itemID == ConfigCommon.blockIdLanternLit;
            }
        }
        else
        {
            return ist.itemID == ConfigCommon.blockIdLanternUnlit;
        }
    }
    
    @Override
    public void startExecuting()
    {
        ItemStack ist = this.el.getCurrentItemOrArmor(0);
        
        if (ist != null)
        {
            if (ist.itemID == ConfigCommon.blockIdLanternLit)
            {
                this.world.playSoundAtEntity(this.el, "fire.fire", 1F, this.world.rand.nextFloat() * 0.4F + 1.5F);
                this.el.setCurrentItemOrArmor(0, new ItemStack(ConfigCommon.blockIdLanternUnlit, 1, 0));
            }
            else if (ist.itemID == ConfigCommon.blockIdLanternUnlit)
            {
                this.world.playSoundAtEntity(this.el, "fire.fire", 1F, this.world.rand.nextFloat() * 0.4F + 0.8F);
                this.el.setCurrentItemOrArmor(0, new ItemStack(ConfigCommon.blockIdLanternLit, 1, 0));
            }
        }
    }
}
