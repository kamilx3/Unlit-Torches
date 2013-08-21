package pelep.unlittorch.recipe;

import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeLanternA implements IRecipe
{
    private ItemStack lantern;
    
    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        int n = 0;
        
        for (int i = 0; i < ic.getSizeInventory(); i++)
        {
            ItemStack ist = ic.getStackInSlot(i);
            n++;
            
            if (ist != null)
            {
                if ((i < 3 || 5 < i) && ist.itemID == Item.ingotIron.itemID)
                {
                    continue;
                }
                else if ((i == 3 || i == 5) && ist.itemID == Block.thinGlass.blockID)
                {
                    continue;
                }
            }
            else if (i == 4)
            {
                continue;
            }
            
            return false;
        }
        
        if (n == 9)
        {
            this.lantern = new ItemStack(ConfigCommon.blockIdLanternUnlit, 1, ConfigCommon.lanternLifespanMax);
            return true;
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
