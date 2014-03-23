package pelep.unlittorch.nei;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pelep.unlittorch.config.ConfigCommon;

/**
 * @author pelep
 */
public class ShapedRecipeHandler extends codechicken.nei.recipe.ShapedRecipeHandler
{
    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        if (result.itemID == ConfigCommon.blockIdTorchUnlit && ConfigCommon.torchRecipeYieldsUnlit)
        {
            this.addTorch(false);
        }
        else if (result.itemID == ConfigCommon.blockIdTorchLit && !ConfigCommon.torchRecipeYieldsUnlit)
        {
            this.addTorch(true);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingr)
    {
        if (ingr.itemID == Item.stick.itemID || ingr.itemID == Item.coal.itemID)
            this.addTorch(!ConfigCommon.torchRecipeYieldsUnlit);
    }

    public void addTorch(boolean lit)
    {
        ItemStack[] ingra = {new ItemStack(Item.coal, 1, 0), new ItemStack(Item.stick)};
        ItemStack[] ingrb = {new ItemStack(Item.coal, 1, 1), new ItemStack(Item.stick)};
        ItemStack torch = new ItemStack(lit ? ConfigCommon.blockIdTorchLit : ConfigCommon.blockIdTorchUnlit, ConfigCommon.torchRecipeYieldCount, 0);
        this.arecipes.add(new CachedShapedRecipe(1, 2, ingra, torch));
        this.arecipes.add(new CachedShapedRecipe(1, 2, ingrb, torch));
    }
}
