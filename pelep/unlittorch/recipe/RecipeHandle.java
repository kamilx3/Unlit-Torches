package pelep.unlittorch.recipe;

import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeHandle implements IRecipe
{
    private ItemStack handle;
    
    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        if (ConfigCommon.recipeDisableLanternHandle)
        {
            return false;
        }
        
        boolean match = false;
        int iron = 0;
        
        for (int i = 0; i < ic.getSizeInventory(); i++)
        {
            ItemStack ist = ic.getStackInSlot(i);
            
            if (ist != null)
            {
                if (ist.itemID != Item.ingotIron.itemID)
                {
                    return false;
                }
                else if (i == 1 || i == 3 || i == 5)
                {
                    match = ++iron == 3;
                    continue;
                }
            }
            else if (i == 1 || i == 3 || i == 5)
            {
                break;
            }
        }
        
        if (!match)
        {
            iron = 0;
            
            for (int i = 0; i < ic.getSizeInventory(); i++)
            {
                ItemStack ist = ic.getStackInSlot(i);
                
                if (ist != null)
                {
                    if (ist.itemID != Item.ingotIron.itemID)
                    {
                        return false;
                    }
                    else if (i == 4 || i == 6 || i == 8)
                    {
                        match = ++iron == 3;
                        continue;
                    }
                }
                else if (i == 4 || i == 6 || i == 8)
                {
                    return false;
                }
            }
        }
        
        if (match)
        {
            this.handle = new ItemStack(ConfigCommon.itemIdLanternFuel, 1, 3);
            return true;
        }
        
        return false;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting ic)
    {
        return this.handle.copy();
    }
    
    @Override
    public int getRecipeSize()
    {
        return 6;
    }
    
    @Override
    public ItemStack getRecipeOutput()
    {
        return this.handle;
    }
}