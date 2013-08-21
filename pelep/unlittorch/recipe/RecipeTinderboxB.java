package pelep.unlittorch.recipe;

import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.IgnitersHandler;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeTinderboxB implements IRecipe
{
    private ItemStack tb;
    
    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        int n = 0;
        int a = -1;
        int b = -1;
        int c = -1;
        
        for (int i = 0; i < ic.getSizeInventory(); i++)
        {
            ItemStack ist = ic.getStackInSlot(i);
            
            if (ist != null)
            {
                if (++n > 3)
                {
                    return false;
                }
                
                int id = ist.itemID;
                int d = ist.getItemDamage();
                
                if (i < 3 && a == -1 && IgnitersHandler.isLanternTinder(id, d))
                {
                    a = i;
                    continue;
                }
                else if (i < 6 && b == -1 && id == Item.ingotIron.itemID)
                {
                    if ((i - 3) == a)
                    {
                        b = i;
                        continue;
                    }
                }
                else if (i < 9 && c == -1 && id == Item.ingotIron.itemID)
                {
                    if ((i - 3) == b)
                    {
                        c = i;
                        continue;
                    }
                }
                
                return false;
            }
        }
        
        if (n == 3 && a != -1 && b != -1 && c != -1)
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
        return 3;
    }
    
    @Override
    public ItemStack getRecipeOutput()
    {
        return this.tb;
    }
}