package pelep.unlittorch.nei;

import static codechicken.nei.NEIClientUtils.translate;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.oredict.OreDictionary;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.recipe.RecipeTorchUnlitA;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pelep
 */
public class ShapelessRecipeHandler extends codechicken.nei.recipe.ShapelessRecipeHandler
{
    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        if (result.itemID == ConfigCommon.itemIdCloth && result.getItemDamage() == 0)
        {
            this.addCloth();
        }
        else if (result.itemID == Item.stick.itemID)
        {
            this.addStick();
        }
        else if (result.itemID == ConfigCommon.blockIdTorchLit)
        {
            this.addTorchLit();
            this.addTorchLit(new ItemStack(ConfigCommon.blockIdTorchLit, 1, 0));
            this.addTorchLit(new ItemStack(Item.flint));
            this.addTorchLit(new ItemStack(Item.flintAndSteel));
            this.addTorchLit(new ItemStack(Item.bucketLava));
        }
        else if (result.itemID == ConfigCommon.blockIdTorchUnlit)
        {
            this.addTorchUnlit();
            this.addTorchUnlit(new ItemStack(ConfigCommon.itemIdCloth, 1, 0));
            this.addTorchUnlit(new ItemStack(ConfigCommon.itemIdCloth, 1, 1));
            this.addTorchUnlit(new ItemStack(Block.cloth.blockID, 1, OreDictionary.WILDCARD_VALUE));
            this.addTorchUnlit(new ItemStack(Block.carpet.blockID, 1, OreDictionary.WILDCARD_VALUE));
            this.addTorchUnlit(new ItemStack(Item.bucketWater));
            this.addTorchUnlit(new ItemStack(Item.bucketMilk));
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingr)
    {
        if (ingr.itemID == ConfigCommon.blockIdTorchLit)
        {
            this.addTorchLit();
            this.addTorchLit(new ItemStack(ConfigCommon.blockIdTorchLit, 1, 0));
            this.addTorchUnlit(new ItemStack(ConfigCommon.itemIdCloth, 1, 0));
            this.addTorchUnlit(new ItemStack(ConfigCommon.itemIdCloth, 1, 1));
            this.addTorchUnlit(new ItemStack(Block.cloth.blockID, 1, OreDictionary.WILDCARD_VALUE));
            this.addTorchUnlit(new ItemStack(Block.carpet.blockID, 1, OreDictionary.WILDCARD_VALUE));
            this.addTorchUnlit(new ItemStack(Item.bucketWater));
            this.addTorchUnlit(new ItemStack(Item.bucketMilk));
        }
        else if (ingr.itemID == ConfigCommon.blockIdTorchUnlit)
        {
            this.addStick();
            this.addTorchLit(new ItemStack(ConfigCommon.blockIdTorchLit, 1, 0));
            this.addTorchLit(new ItemStack(Item.flint));
            this.addTorchLit(new ItemStack(Item.flintAndSteel));
            this.addTorchLit(new ItemStack(Item.bucketLava));
            this.addTorchUnlit();
        }
        else if (ingr.itemID == Item.flint.itemID || ingr.itemID == Item.flintAndSteel.itemID || ingr.itemID == Item.bucketLava.itemID)
        {
            this.addTorchLit(ingr.copy());
        }
        else if (ingr.itemID == Item.shears.itemID)
        {
            this.addCloth();
        }
        else if (ingr.itemID == Block.cloth.blockID)
        {
            this.addCloth();
            this.addTorchUnlit(new ItemStack(ingr.itemID, 1, OreDictionary.WILDCARD_VALUE));
        }
        else if (ingr.itemID == Block.carpet.blockID ||
                ingr.itemID == Item.bucketWater.itemID ||
                ingr.itemID == Item.bucketMilk.itemID)
        {
            this.addTorchUnlit(new ItemStack(ingr.itemID, 1, OreDictionary.WILDCARD_VALUE));
        }
        else if (ingr.itemID == ConfigCommon.itemIdCloth)
        {
            this.addTorchUnlit(new ItemStack(ingr.itemID, 1, 0));
            this.addTorchUnlit(new ItemStack(ingr.itemID, 1, 1));
        }
    }

    private void addCloth()
    {
        List<ItemStack> ingr = new ArrayList();
        ingr.add(new ItemStack(Block.cloth, 1, OreDictionary.WILDCARD_VALUE));
        ingr.add(new ItemStack(Item.shears, 1, 0));
        this.arecipes.add(new CachedShapelessRecipe(ingr, new ItemStack(ConfigCommon.itemIdCloth, 3, 0)));
    }

    private void addStick()
    {
        List<ItemStack> ingr = new ArrayList();
        for (int i = 0; i < ConfigCommon.torchRecipeYieldCount; i++)
        {
            ingr.add(new ItemStack(ConfigCommon.blockIdTorchUnlit, 1, 0));
        }
        this.arecipes.add(new CachedShapelessRecipe(ingr, new ItemStack(Item.stick)));
    }

    private void addTorchLit()
    {
        List<ItemStack> ingr = new ArrayList();
        ingr.add(new ItemStack(ConfigCommon.blockIdTorchLit, 1, 200));
        ingr.add(new ItemStack(ConfigCommon.blockIdTorchLit, 1, ConfigCommon.torchLifespanMax - 200));
        ItemStack ist = new ItemStack(ConfigCommon.blockIdTorchLit, 1, ConfigCommon.torchLifespanMax / 2);
        this.arecipes.add(new CachedShapelessRecipe(ingr, ist));
    }

    private void addTorchLit(ItemStack ign)
    {
        List<ItemStack> ingr = new ArrayList();
        ingr.add(new ItemStack(ConfigCommon.blockIdTorchUnlit, 1, 0));
        ingr.add(ign);
        this.arecipes.add(new CachedShapelessRecipe(ingr, new ItemStack(ConfigCommon.blockIdTorchLit, 1, 0)));
    }

    private void addTorchUnlit()
    {
        List<ItemStack> ingr = new ArrayList();
        int d1 = ConfigCommon.torchLifespanMax / 2;
        int d2 = ConfigCommon.torchLifespanMax * 3 / 4;
        ingr.add(new ItemStack(ConfigCommon.blockIdTorchUnlit, 1, d1));
        ingr.add(new ItemStack(ConfigCommon.blockIdTorchUnlit, 1, d2));
        int d = RecipeTorchUnlitA.getRepairedValue(d1, d2);
        ItemStack ist = new ItemStack(ConfigCommon.blockIdTorchUnlit, 1, d);
        this.arecipes.add(new CachedShapelessRecipe(ingr, ist));
    }

    private void addTorchUnlit(ItemStack ext)
    {
        List<ItemStack> ingr = new ArrayList();
        ingr.add(new ItemStack(ConfigCommon.blockIdTorchLit, 1, 0));
        ingr.add(ext);
        this.arecipes.add(new CachedShapelessRecipe(ingr, new ItemStack(ConfigCommon.blockIdTorchUnlit, 1, 0)));
    }


    @Override
    public List<String> handleItemTooltip(GuiRecipe gui, ItemStack ist, List<String> tip, int index)
    {
        if (ist == null) return super.handleItemTooltip(gui, null, tip, index);

        CachedShapelessRecipe recipe = (CachedShapelessRecipe) this.arecipes.get(index);
        List<PositionedStack> ingr = recipe.getIngredients();

        if (ingr.size() != 2)
        {
            return super.handleItemTooltip(gui, ist, tip, index);
        }
        else if (gui.isMouseOver(ingr.get(0), index) && ist.itemID == ConfigCommon.blockIdTorchLit && ist.getItemDamage() == 200)
        {
            tip.add(EnumChatFormatting.GRAY + translate("unlittorch.notConsumed"));
            tip.add(EnumChatFormatting.RED + translate("unlittorch.willBeDamaged"));
        }
        else if (gui.isMouseOver(ingr.get(1), index))
        {
            if (ist.itemID == Item.shears.itemID ||
                ist.itemID == Item.flintAndSteel.itemID)
            {
                tip.add(EnumChatFormatting.GRAY + translate("unlittorch.notConsumed"));
                tip.add(EnumChatFormatting.RED + translate("unlittorch.willBeDamaged"));
            }
            else if ((ist.itemID == ConfigCommon.itemIdCloth && ist.getItemDamage() == 1) ||
                    ist.itemID == ConfigCommon.blockIdTorchLit && ist.getItemDamage() == 0)
            {
                tip.add(EnumChatFormatting.GRAY + translate("unlittorch.notConsumed"));
            }
            else if (ist.itemID == Item.bucketLava.itemID ||
                    ist.itemID == Item.bucketMilk.itemID ||
                    ist.itemID == Item.bucketWater.itemID)
            {
                tip.add(EnumChatFormatting.RED + translate("unlittorch.returnedEmpty"));
            }
        }

        return super.handleItemTooltip(gui, ist, tip, index);
    }
}
