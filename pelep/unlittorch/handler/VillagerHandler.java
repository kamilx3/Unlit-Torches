package pelep.unlittorch.handler;

import java.util.Random;

import pelep.unlittorch.config.ConfigCommon;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import net.minecraft.entity.passive.EntityVillager;
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
            int j = rand.nextInt(3);
            
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
            ItemStack price1 = getLowPrice(false, rand);
            ItemStack price2 = getLowPrice(true, rand);
            mrl.add(new MerchantRecipe(price1, price2, merch));
        }
    }
    
    private static void addCrapHigh(int i, MerchantRecipeList mrl, Random rand)
    {
        for (int j = 0; j < i; j++)
        {
            ItemStack merch = getCrapQualityMerch(rand);
            ItemStack price1 = getHighPrice(false, rand);
            ItemStack price2 = getHighPrice(true, rand);
            mrl.add(new MerchantRecipe(price1, price2, merch));
        }
    }
    
    private static void addHighHigh(int i, MerchantRecipeList mrl, Random rand)
    {
        for (int j = 0; j < i; j++)
        {
            ItemStack merch = getHighQualityMerch(rand);
            ItemStack price1 = getHighPrice(false, rand);
            ItemStack price2 = getHighPrice(true, rand);
            mrl.add(new MerchantRecipe(price1, price2, merch));
        }
    }
    
    private static void addCrapLow(int i, MerchantRecipeList mrl, Random rand)
    {
        for (int j = 0; j < i; j++)
        {
            ItemStack merch = getCrapQualityMerch(rand);
            ItemStack price1 = getLowPrice(false, rand);
            ItemStack price2 = getLowPrice(true, rand);
            mrl.add(new MerchantRecipe(price1, price2, merch));
        }
    }
    
    private static void addRandom(int i, MerchantRecipeList mrl, Random rand)
    {
        for (int j = 0; j < i; j++)
        {
            ItemStack merch = rand.nextBoolean() ? getHighQualityMerch(rand) : getCrapQualityMerch(rand);
            ItemStack price1 = rand.nextBoolean() ? getHighPrice(false, rand) : getLowPrice(false, rand);
            ItemStack price2 = rand.nextBoolean() ? getHighPrice(true, rand) : getLowPrice(true, rand);
            mrl.add(new MerchantRecipe(price1, price2, merch));
        }
    }

    private static ItemStack getHighQualityMerch(Random rand)
    {
        ItemStack ist;
        int i = rand.nextInt(5);
        
        if (i == 0)
        {
            int d = Math.max(rand.nextInt(ConfigCommon.lanternLifespanMax / 3), 1);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("handle", rand.nextBoolean());
            ist = new ItemStack(ConfigCommon.blockIdLanternUnlit, 1, d);
            ist.setTagCompound(tag);
        }
        else if (i == 1)
        {
            ist = new ItemStack(ConfigCommon.itemIdTinderboxFS, 1, 0);
        }
        else if (i == 2)
        {
            ist = new ItemStack(ConfigCommon.itemIdLanternFuel, 12 + rand.nextInt(4), 0);
        }
        else if (i == 3)
        {
            ist = new ItemStack(ConfigCommon.itemIdLanternFuel, 4 + rand.nextInt(4), 1);
        }
        else
        {
            ist = new ItemStack(ConfigCommon.itemIdLanternFuel, 1 + rand.nextInt(3), 2);
        }
        
        return ist;
    }

    private static ItemStack getCrapQualityMerch(Random rand)
    {
        ItemStack ist;
        int i = rand.nextInt(5);
        
        if (i == 0)
        {
            int l = ConfigCommon.lanternLifespanMax / 2;
            int d = Math.max(l + rand.nextInt(l / 2), 1);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("handle", false);
            ist = new ItemStack(ConfigCommon.blockIdLanternUnlit, 1, d);
            ist.setTagCompound(tag);
        }
        else if (i == 1)
        {
            ist = new ItemStack(ConfigCommon.itemIdTinderbox, 1, rand.nextInt(2));
        }
        else if (i == 2)
        {
            ist = new ItemStack(ConfigCommon.itemIdLanternFuel, 4 + rand.nextInt(4), 0);
        }
        else if (i == 3)
        {
            ist = new ItemStack(ConfigCommon.itemIdLanternFuel, 1 + rand.nextInt(3), 1);
        }
        else
        {
            ist = new ItemStack(ConfigCommon.itemIdLanternFuel, 1, 2);
        }
        
        return ist;
    }

    private static ItemStack getLowPrice(boolean nullable, Random rand)
    {
        //TODO gravel, wheat, 
        return new ItemStack(1, 1, 0);
    }

    private static ItemStack getHighPrice(boolean nullable, Random rand)
    {
        //TODO emerald
        return new ItemStack(1, 1, 0);
    }
}
