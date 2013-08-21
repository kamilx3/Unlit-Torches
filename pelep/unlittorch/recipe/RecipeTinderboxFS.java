package pelep.unlittorch.recipe;

import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeTinderboxFS implements IRecipe
{
    private ItemStack tb;
    
    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        int n = 0;
        int t = -1;
        int f = -1;
        
        for (int i = 0; i < ic.getSizeInventory(); i++)
        {
            ItemStack ist = ic.getStackInSlot(i);
            
            if (ist != null)
            {
                if (++n > 2)
                {
                    return false;
                }
                
                int id = ist.itemID;
                
                if (t == -1 && id == ConfigCommon.itemIdTinderbox && ist.getItemDamage() == 0)
                {
                    t = i;
                    continue;
                }
                else if (f == -1 && id == Item.flintAndSteel.itemID)
                {
                    f = i;
                    continue;
                }
                
                return false;
            }
        }
        
        if (n != 2 || t == -1 || f == -1) return false;
        
        this.tb = new ItemStack(ConfigCommon.itemIdTinderboxFS, 1, ic.getStackInSlot(f).getItemDamage());
        return true;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting ic)
    {
        return this.tb.copy();
    }
    
    @Override
    public int getRecipeSize()
    {
        return 2;
    }
    
    @Override
    public ItemStack getRecipeOutput()
    {
        return this.tb;
    }
}