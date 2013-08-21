package pelep.unlittorch.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.IgnitersHandler;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLanternUnlit extends ItemLantern
{
    public ItemLanternUnlit(int id)
    {
        super(id);
        this.setUnlocalizedName("ut_lanternUnlit");
        this.func_111206_d("unlittorch:lantern_completeoff");
    }
    
    
    @Override
    public String getUnlocalizedName(ItemStack ist)
    {
        return (ist.stackTagCompound == null || !ist.stackTagCompound.getBoolean("handle")) ? "item.ut_lanternUnlit" : "item.ut_lanternUnlitHandle";
    }
    
    @Override
    public String getUnlocalizedName()
    {
        return "ut_lanternUnlit";
    }
    
    
    //--------------------------------rendering--------------------------------//
    
    
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister ir)
    {
        this.itemIcon = ir.registerIcon("unlittorch:lantern_bodyoff");
        this.icon_complete = ir.registerIcon("unlittorch:lantern_completeoff");
    }
    
    
    //---------------------------------itemuse---------------------------------//
    
    
    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer p)
    {
        if (p.capabilities.isCreativeMode || (ist.getItemDamage() < this.getMaxDamage() && this.checkPlayer(p)))
        {
            ist.itemID = ConfigCommon.blockIdLanternLit;
            world.playSoundAtEntity(p, "fire.fire",  1F, world.rand.nextFloat() * 0.4F + 0.8F);
        }
        
        return ist;
    }
    
    private boolean checkPlayer(EntityPlayer p)
    {
        int tinderbox = -1;
        int igniter = -1;
        
        for (int i = 0; i < p.inventory.mainInventory.length; i++)
        {
            ItemStack ist = p.inventory.mainInventory[i];
            
            if (ist != null)
            {
                int id = ist.itemID;
                int d = ist.getItemDamage();
                
                if (id == ConfigCommon.itemIdTinderboxFS && IgnitersHandler.isLanternIgniter(id, d))
                {
                    if (ist.attemptDamageItem(1, p.getRNG()))
                    {
                        p.inventory.setInventorySlotContents(i, new ItemStack(ConfigCommon.itemIdTinderbox, 1, 0));
                    }
                    
                    return true;
                }
                else if (tinderbox == -1 && id == ConfigCommon.itemIdTinderbox && d == 0)
                {
                    tinderbox = i;
                }
                else if (igniter == -1 && IgnitersHandler.isLanternIgniter(id, d) && id != Item.flint.itemID && id != Item.gunpowder.itemID)
                {
                    igniter = i;
                }
            }
        }
        
        boolean light = false;
        
        if (igniter != -1 && tinderbox != -1)
        {
            ItemStack ign = p.inventory.mainInventory[igniter];
            light = true;
            
            if (ign.itemID == 50)
            {
                ign.setItemDamage(ign.getItemDamage() + (ign.getMaxDamage() / 10));
            }
            else if (ign.isItemStackDamageable())
            {
                ign.damageItem(1, p);
            }
            else if (ign.itemID != Item.bucketLava.itemID)
            {
                p.inventory.decrStackSize(igniter, 1);
            }
        }
        
        return light;
    }
}
