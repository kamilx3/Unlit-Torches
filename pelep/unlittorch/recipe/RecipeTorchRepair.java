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
public class RecipeTorchRepair implements IRecipe
{
    private ItemStack torch;

    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        int t = 0;
        int c = 0;

        for (int i = 0; i < ic.getSizeInventory(); i++)
        {
            ItemStack ist = ic.getStackInSlot(i);

            if (ist != null)
            {
                if (ist.itemID == Item.coal.itemID && c++ == 0)
                {
                    continue;
                }
                else if (ist.itemID == ConfigCommon.blockIdTorchUnlit &&
                        ist.getItemDamage() != 0 &&
                        ++t <= ConfigCommon.torchRecipeYieldCount)
                {
                    continue;
                }

                return false;
            }
        }

        if (c != 1 || t == 0) return false;

        torch = new ItemStack(ConfigCommon.blockIdTorchUnlit, t, 0);
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting ic)
    {
        return torch.copy();
    }

    @Override
    public int getRecipeSize()
    {
        return ConfigCommon.torchRecipeYieldCount + 1;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return torch;
    }
}
