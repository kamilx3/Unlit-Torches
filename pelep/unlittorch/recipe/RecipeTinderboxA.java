package pelep.unlittorch.recipe;

import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.IgnitersHandler;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeTinderboxA implements IRecipe
{
    private ItemStack tb;
    
    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        int size = ic.getSizeInventory();
        int n = 0;
        int t = -1;
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
                int d = ist.getItemDamage();
                
                if (c == -1 && id == ConfigCommon.itemIdTinderbox && d == 1)
                {
                    c = i;
                    continue;
                }
                else if (t == -1 && IgnitersHandler.isLanternTinder(id, d))
                {
                    t = i;
                    continue;
                }
                
                return false;
            }
        }
        
        if (n != 2 || t == -1 || c == -1) return false;
        
        int a = size == 9 ? 3 : 2;
        
        if (t == (c - a))
        {
            this.tb = new ItemStack(ConfigCommon.itemIdTinderbox, 1, 0);
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
