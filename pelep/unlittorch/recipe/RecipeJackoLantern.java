package pelep.unlittorch.recipe;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeJackoLantern implements IRecipe
{
    private ItemStack lantern = new ItemStack(Block.pumpkinLantern, 1);
    
    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        int size = ic.getSizeInventory();
        int n = 0;
        int t = -1;
        int p = -1;
        
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
                
                if (t == -1 && id == 50 && ist.getItemDamage() != 0)
                {
                    t = i;
                    continue;
                }
                else if (p == -1 && id == Block.pumpkin.blockID)
                {
                    p = i;
                    continue;
                }
                
                return false;
            }
        }
        
        if (n != 2 || t == -1 || p == -1) return false;
        
        int a = size == 9 ? 3 : 2;
        return p == (t - a);
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting ic)
    {
        return this.lantern.copy();
    }
    
    @Override
    public int getRecipeSize()
    {
        return 2;
    }
    
    @Override
    public ItemStack getRecipeOutput()
    {
        return this.lantern;
    }
}
