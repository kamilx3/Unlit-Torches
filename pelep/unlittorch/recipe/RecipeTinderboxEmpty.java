package pelep.unlittorch.recipe;

import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeTinderboxEmpty implements IRecipe
{
    private ItemStack tb;
    
    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        if (ConfigCommon.recipeDisableEmptyTinderbox)
        {
            return false;
        }
        
        int size = ic.getSizeInventory();
        int n = 0;
        int i1 = -1;
        int i2 = -1;
        
        for (int i = 0; i < size; i++)
        {
            ItemStack ist = ic.getStackInSlot(i);
            
            if (ist != null)
            {
                if (++n > 2)
                {
                    return false;
                }
                
                if (ist.itemID == Item.ingotIron.itemID)
                {
                    if (i1 == -1)
                    {
                        i1 = i;
						continue;
                    }
                    else if (i2 == -1)
                    {
                        i2 = i;
                        continue;
                    }
                }
                
                return false;
            }
        }
        
        if (n != 2 || i1 == -1 || i2 == -1) return false;
        
        int a = size == 9 ? 3 : 2;
        
        if (i1 == (i2 - a))
        {
            this.tb = new ItemStack(ConfigCommon.itemIdTinderbox, 1, 1);
            return true;
        }
        
        return false;
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