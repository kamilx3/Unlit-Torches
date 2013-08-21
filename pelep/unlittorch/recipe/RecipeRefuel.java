package pelep.unlittorch.recipe;

import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeRefuel implements IRecipe
{
    private ItemStack lantern;
    
    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        ItemStack l = ic.getStackInSlot(0);
        
        if (l != null && (l.itemID == ConfigCommon.blockIdLanternLit || l.itemID == ConfigCommon.blockIdLanternUnlit))
        {
            int lvl = l.getItemDamage();
            boolean full = lvl < 1;
            
            for (int i = 1; i < ic.getSizeInventory(); i++)
            {
                ItemStack ist = ic.getStackInSlot(i);
                
                if (ist == null)
                {
                    continue;
                }
                else if (ist.itemID == ConfigCommon.itemIdLanternFuel)
                {
                    if (full)
                    {
                        return false;
                    }
                    
                    int d = ist.getItemDamage();
                    
                    if (d == 0)
                    {
                        lvl -= ConfigCommon.lanternFuelFat;
                    }
                    else if (d == 1)
                    {
                        lvl -= (ConfigCommon.lanternFuelFat * 3);
                    }
                    else if (d == 2)
                    {
                        lvl -= ConfigCommon.lanternFuelOil;
                    }
                    else
                    {
                        return false;
                    }
                    
                    full = lvl < 1;
                    continue;
                }
                
                return false;
            }
            
            if (lvl != l.getItemDamage())
            {
                this.lantern = l.copy();
                this.lantern.setItemDamage(Math.max(lvl, 0));
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting ic)
    {
        return this.lantern.copy();
    }
    
    @Override
    public int getRecipeSize()
    {
        return 9;
    }
    
    @Override
    public ItemStack getRecipeOutput()
    {
        return this.lantern;
    }
}
