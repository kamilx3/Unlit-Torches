package pelep.unlittorch.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import pelep.unlittorch.config.ConfigCommon;

/**
 * @author pelep
 */
public class RecipeStick implements IRecipe
{
    private final ItemStack stick = new ItemStack(Item.stick.itemID, 1, 0);

    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        int n = 0;

        for (int i = 0; i < ic.getSizeInventory(); i++)
        {
            ItemStack ist = ic.getStackInSlot(i);

            if (ist == null)
            {
                continue;
            }
            else if (ist.itemID == ConfigCommon.blockIdTorchUnlit && ist.getItemDamage() == 0 && ++n <= ConfigCommon.torchRecipeYieldCount)
            {
                continue;
            }

            return false;
        }

        return n == ConfigCommon.torchRecipeYieldCount;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting ic)
    {
        return this.stick.copy();
    }

    @Override
    public int getRecipeSize()
    {
        return ConfigCommon.torchRecipeYieldCount;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return this.stick;
    }
}
