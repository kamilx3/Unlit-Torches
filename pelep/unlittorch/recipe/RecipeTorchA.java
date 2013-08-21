package pelep.unlittorch.recipe;

import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeTorchA implements IRecipe
{
    private ItemStack torch;
    
    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        int size = ic.getSizeInventory();
        int n = 0;
        int s = -1;
        int c = -1;
        
        for (int i = 0; i < size; i++)
        {
            ItemStack ist = ic.getStackInSlot(i);
            
            if (ist != null)
            {
                if (++n > 2)
                {
                    return false;
                }
                
                int id = ist.itemID;
                
                if (s == -1 && id == Item.stick.itemID)
                {
                    s = i;
                    continue;
                }
                else if (c == -1 && id == Item.coal.itemID)
                {
                    c = i;
                    continue;
                }
                
                return false;
            }
        }
        
        if (n != 2 || s == -1 || c == -1) return false;
        
        int a = size == 9 ? 3 : 2;
        
        if (c == (s - a))
        {
            if (ConfigCommon.recipeOverrideTorches)
            {
                this.torch = new ItemStack(50, 4, 0);
            }
            else
            {
                this.torch = new ItemStack(50, 4, 1);
            }
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting ic)
    {
        return this.torch.copy();
    }
    
    @Override
    public int getRecipeSize()
    {
        return 2;
    }
    
    @Override
    public ItemStack getRecipeOutput()
    {
        return this.torch;
    }
}