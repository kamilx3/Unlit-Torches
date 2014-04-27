package pelep.unlittorch.nei;

import static codechicken.nei.NEIClientUtils.translate;
import static pelep.unlittorch.config.ConfigCommon.*;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.oredict.OreDictionary;
import pelep.unlittorch.recipe.RecipeTorchUnlitA;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author pelep
 */
public class ShapelessRecipeHandler extends codechicken.nei.recipe.ShapelessRecipeHandler
{
    private static Random rand = new Random();

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        if (result.itemID == itemIdCloth && result.getItemDamage() == 0)
        {
            addCloth();
        }
        else if (result.itemID == Item.stick.itemID)
        {
            addStick();
        }
        else if (result.itemID == blockIdTorchLit)
        {
            addTorchLit();
            addTorchLit(new ItemStack(Block.torchWood));
            addTorchLit(new ItemStack(blockIdTorchLit, 1, 0));
            addTorchLit(new ItemStack(Item.flint));
            addTorchLit(new ItemStack(Item.flintAndSteel));
            addTorchLit(new ItemStack(Item.bucketLava));
        }
        else if (result.itemID == blockIdTorchUnlit)
        {
            addTorchUnlit();
            addTorchUnlit(new ItemStack(itemIdCloth, 1, 0));
            addTorchUnlit(new ItemStack(itemIdCloth, 1, 1));
            addTorchUnlit(new ItemStack(Block.cloth.blockID, 1, OreDictionary.WILDCARD_VALUE));
            addTorchUnlit(new ItemStack(Block.carpet.blockID, 1, OreDictionary.WILDCARD_VALUE));
            addTorchUnlit(new ItemStack(Item.bucketWater));
            addTorchUnlit(new ItemStack(Item.bucketMilk));
            addTorchRepair(0);
            addTorchRepair(1);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingr)
    {
        if (ingr.itemID == blockIdTorchLit)
        {
            addTorchLit();
            addTorchLit(new ItemStack(blockIdTorchLit, 1, 0));
            addTorchUnlit(new ItemStack(itemIdCloth, 1, 0));
            addTorchUnlit(new ItemStack(itemIdCloth, 1, 1));
            addTorchUnlit(new ItemStack(Block.cloth.blockID, 1, OreDictionary.WILDCARD_VALUE));
            addTorchUnlit(new ItemStack(Block.carpet.blockID, 1, OreDictionary.WILDCARD_VALUE));
            addTorchUnlit(new ItemStack(Item.bucketWater));
            addTorchUnlit(new ItemStack(Item.bucketMilk));
        }
        else if (ingr.itemID == blockIdTorchUnlit)
        {
            addStick();
            addTorchLit(new ItemStack(Block.torchWood));
            addTorchLit(new ItemStack(blockIdTorchLit, 1, 0));
            addTorchLit(new ItemStack(Item.flint));
            addTorchLit(new ItemStack(Item.flintAndSteel));
            addTorchLit(new ItemStack(Item.bucketLava));
            addTorchUnlit();
            addTorchRepair(0);
            addTorchRepair(1);
        }
        else if (ingr.itemID == Block.torchWood.blockID || ingr.itemID == Item.flint.itemID ||
                ingr.itemID == Item.flintAndSteel.itemID || ingr.itemID == Item.bucketLava.itemID)
        {
            addTorchLit(ingr.copy());
        }
        else if (ingr.itemID == Item.shears.itemID)
        {
            addCloth();
        }
        else if (ingr.itemID == Block.cloth.blockID)
        {
            addCloth();
            addTorchUnlit(new ItemStack(ingr.itemID, 1, OreDictionary.WILDCARD_VALUE));
        }
        else if (ingr.itemID == Block.carpet.blockID ||
                ingr.itemID == Item.bucketWater.itemID ||
                ingr.itemID == Item.bucketMilk.itemID)
        {
            addTorchUnlit(new ItemStack(ingr.itemID, 1, OreDictionary.WILDCARD_VALUE));
        }
        else if (ingr.itemID == itemIdCloth)
        {
            addTorchUnlit(new ItemStack(ingr.itemID, 1, 0));
            addTorchUnlit(new ItemStack(ingr.itemID, 1, 1));
        }
        else if (ingr.itemID == Item.coal.itemID)
        {
            addTorchRepair(ingr.getItemDamage());
        }
    }

    private void addCloth()
    {
        List<ItemStack> ingr = new ArrayList<ItemStack>();
        ingr.add(new ItemStack(Block.cloth, 1, OreDictionary.WILDCARD_VALUE));
        ingr.add(new ItemStack(Item.shears, 1, 0));
        arecipes.add(new CachedShapelessRecipe(ingr, new ItemStack(itemIdCloth, 3, 0)));
    }

    private void addStick()
    {
        List<ItemStack> ingr = new ArrayList<ItemStack>();

        for (int i = 0; i < torchRecipeYieldCount; i++)
            ingr.add(new ItemStack(blockIdTorchUnlit, 1, 0));

        arecipes.add(new CachedShapelessRecipe(ingr, new ItemStack(Item.stick)));
    }

    private void addTorchLit()
    {
        List<ItemStack> ingr = new ArrayList<ItemStack>();
        ingr.add(new ItemStack(blockIdTorchLit, 1, 200));
        ingr.add(new ItemStack(blockIdTorchLit, 1, torchLifespanMax - 200));
        ItemStack ist = new ItemStack(blockIdTorchLit, 1, torchLifespanMax / 2);
        arecipes.add(new CachedShapelessRecipe(ingr, ist));
    }

    private void addTorchLit(ItemStack ign)
    {
        List<ItemStack> ingr = new ArrayList<ItemStack>();
        ingr.add(new ItemStack(blockIdTorchUnlit, 1, 0));
        ingr.add(ign);
        arecipes.add(new CachedShapelessRecipe(ingr, new ItemStack(blockIdTorchLit, 1, 0)));
    }

    private void addTorchUnlit()
    {
        List<ItemStack> ingr = new ArrayList<ItemStack>();
        int d1 = torchLifespanMax / 2;
        int d2 = torchLifespanMax * 3 / 4;
        ingr.add(new ItemStack(blockIdTorchUnlit, 1, d1));
        ingr.add(new ItemStack(blockIdTorchUnlit, 1, d2));
        int d = RecipeTorchUnlitA.getRepairedValue(d1, d2);
        ItemStack ist = new ItemStack(blockIdTorchUnlit, 1, d);
        arecipes.add(new CachedShapelessRecipe(ingr, ist));
    }

    private void addTorchUnlit(ItemStack ext)
    {
        List<ItemStack> ingr = new ArrayList<ItemStack>();
        ingr.add(new ItemStack(blockIdTorchLit, 1, 0));
        ingr.add(ext);
        arecipes.add(new CachedShapelessRecipe(ingr, new ItemStack(blockIdTorchUnlit, 1, 0)));
    }

    private void addTorchRepair(int md)
    {
        for (int i = 0; i < torchRecipeYieldCount; i++)
        {
            List<ItemStack> ingr = new ArrayList<ItemStack>();
            ingr.add(new ItemStack(Item.coal.itemID, 1, md));
            ItemStack ist = new ItemStack(blockIdTorchUnlit, i + 1, 0);

            for (int j = 0; j <= i; j++)
                ingr.add(new ItemStack(blockIdTorchUnlit, 1, rand.nextInt(torchLifespanMax)));

            arecipes.add(new CachedShapelessRecipe(ingr, ist));
        }
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe gui, ItemStack ist, List<String> tip, int index)
    {
        if (ist == null) return super.handleItemTooltip(gui, null, tip, index);

        CachedShapelessRecipe recipe = (CachedShapelessRecipe) arecipes.get(index);
        List<PositionedStack> ingr = recipe.getIngredients();

        if (ingr.size() != 2)
        {
            return super.handleItemTooltip(gui, ist, tip, index);
        }
        else if (gui.isMouseOver(ingr.get(0), index) && ist.itemID == blockIdTorchLit && ist.getItemDamage() == 200)
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
            else if (ist.itemID == Block.torchWood.blockID ||
                    (ist.itemID == blockIdTorchLit && ist.getItemDamage() == 0) ||
                    (ist.itemID == itemIdCloth && ist.getItemDamage() == 1))
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
