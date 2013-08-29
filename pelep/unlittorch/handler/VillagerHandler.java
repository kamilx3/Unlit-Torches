package pelep.unlittorch.handler;

import java.util.Random;

import pelep.unlittorch.config.ConfigCommon;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class VillagerHandler implements IVillageTradeHandler
{
    public static final int VILLAGER_ID = 15;

    @Override
    public void manipulateTradesForVillager(EntityVillager ev, MerchantRecipeList mrl, Random rand)
    {
        int p = ev.getProfession();

        if (p == VILLAGER_ID)
        {
            int i = 1 + rand.nextInt(4);
            int j = rand.nextInt(10);

            if (j == 0)
            {
                addHighLow(i, mrl, rand);
            }
            else if (j == 1)
            {
                addCrapHigh(i, mrl, rand);
            }
            else if (j < 5)
            {
                addHighHigh(i, mrl, rand);
            }
            else if (j < 8)
            {
                addCrapLow(i, mrl, rand);
            }
            else
            {
                addRandom(i, mrl, rand);
            }
        }
        else if (p == 0)
        {
            int i = 1 + rand.nextInt(3);
            int j = rand.nextInt(4);
            
            if (j == 0)
            {
                addCrapLow(i, mrl, rand);
            }
            else if (j == 1)
            {
                addHighHigh(i, mrl, rand);
            }
            else
            {
                addRandom(i, mrl, rand);
            }
        }
        else if (p == 3)
        {
            if (rand.nextBoolean())
            {
                addHighLow(1 + rand.nextInt(2), mrl, rand);
            }
            else
            {
                addHighHigh(1 + rand.nextInt(2), mrl, rand);
            }
        }
    }

    private static void addHighLow(int i, MerchantRecipeList mrl, Random rand)
    {
        for (int j = 0; j < i; j++)
        {
            ItemStack merch = getHighQualityMerch(rand);
            ItemStack price = getLowPrice(rand);
            mrl.add(new MerchantRecipe(price, merch));
        }
    }
    
    private static void addCrapHigh(int i, MerchantRecipeList mrl, Random rand)
    {
        for (int j = 0; j < i; j++)
        {
            ItemStack merch = getCrapQualityMerch(rand);
            ItemStack price = getHighPrice(rand);
            mrl.add(new MerchantRecipe(price, merch));
        }
    }
    
    private static void addHighHigh(int i, MerchantRecipeList mrl, Random rand)
    {
        for (int j = 0; j < i; j++)
        {
            ItemStack merch = getHighQualityMerch(rand);
            ItemStack price = getHighPrice(rand);
            mrl.add(new MerchantRecipe(price, merch));
        }
    }
    
    private static void addCrapLow(int i, MerchantRecipeList mrl, Random rand)
    {
        for (int j = 0; j < i; j++)
        {
            ItemStack merch = getCrapQualityMerch(rand);
            ItemStack price = getLowPrice(rand);
            mrl.add(new MerchantRecipe(price, merch));
        }
    }
    
    private static void addRandom(int i, MerchantRecipeList mrl, Random rand)
    {
        for (int j = 0; j < i; j++)
        {
            ItemStack merch = rand.nextBoolean() ? getHighQualityMerch(rand) : getCrapQualityMerch(rand);
            ItemStack price = rand.nextBoolean() ? getHighPrice(rand) : getLowPrice(rand);
            mrl.add(new MerchantRecipe(price, merch));
        }
    }

    private static ItemStack getHighQualityMerch(Random rand)
    {
        switch (rand.nextInt(4))
        {
        case 0:
            int d = Math.max(rand.nextInt(ConfigCommon.lanternLifespanMax / 3), 1);

            NBTTagCompound tag = new NBTTagCompound();
            ItemStack ist = new ItemStack(ConfigCommon.blockIdLanternUnlit, 1, d);

            tag.setBoolean("handle", rand.nextBoolean());
            ist.setTagCompound(tag);

            return ist;
        case 1:
            return new ItemStack(ConfigCommon.itemIdTinderboxFS, 1, 0);
        case 2:
            return new ItemStack(ConfigCommon.itemIdLanternFuel, 4 + rand.nextInt(5), 1);
        default:
            return new ItemStack(ConfigCommon.itemIdLanternFuel, 2 + rand.nextInt(4), 2);
        }
    }

    private static ItemStack getCrapQualityMerch(Random rand)
    {
        switch (rand.nextInt(4))
        {
        case 0:
            return new ItemStack(ConfigCommon.itemIdTinderbox, 1, rand.nextInt(2));
        case 1:
            return new ItemStack(ConfigCommon.itemIdLanternFuel, 8 + rand.nextInt(8), 0);
        case 2:
            return new ItemStack(ConfigCommon.itemIdLanternFuel, 2 + rand.nextInt(4), 1);
        default:
            return new ItemStack(ConfigCommon.itemIdLanternFuel, 1 + rand.nextInt(3), 3);
        }
    }

    private static ItemStack getLowPrice(Random rand)
    {
        switch (rand.nextInt(10))
        {
        case 0:
            return new ItemStack(Item.wheat, 18 + rand.nextInt(15), 0);
        case 1:
            return new ItemStack(Block.gravel, 14 + rand.nextInt(11), 0);
        case 2:
            return new ItemStack(Block.cloth, 14 + rand.nextInt(11), 0);
        case 3:
            return new ItemStack(Item.coal, 2 + rand.nextInt(3), 0);
        case 4:
            return new ItemStack(Item.coal, 2 + rand.nextInt(3), 1);
        case 5:
            return new ItemStack(Item.paper, 15 + rand.nextInt(11), 0);
        case 6:
            return new ItemStack(Item.porkRaw, 6 + rand.nextInt(6), 0);
        case 7:
            return new ItemStack(Item.beefRaw, 6 + rand.nextInt(6), 0);
        case 8:
            return new ItemStack(Item.chickenRaw, 6 + rand.nextInt(6), 0);
        default:
            return new ItemStack(Item.fishCooked, 4 + rand.nextInt(5), 0);
        }
    }

    private static ItemStack getHighPrice(Random rand)
    {
        switch (rand.nextInt(8))
        {
        case 0:
            return new ItemStack(Item.emerald, 1 + rand.nextInt(2), 0);
        case 1:
            return new ItemStack(Item.ingotIron, 2 + rand.nextInt(6), 0);
        case 2:
            return new ItemStack(Item.ingotGold, 1 + rand.nextInt(3), 0);
        case 3:
            return new ItemStack(Item.swordGold, 1, 0);
        case 4:
            return new ItemStack(Item.pickaxeGold, 1, 0);
        case 5:
            return new ItemStack(Item.axeGold, 1, 0);
        case 6:
            return new ItemStack(Item.writtenBook, 2 + rand.nextInt(3), 0);
        default:
            return new ItemStack(Item.book, 8 + rand.nextInt(11), 0);
        }
    }
}
