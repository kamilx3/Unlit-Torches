package pelep.unlittorch.config;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

/**
 * @author pelep
 */
public class ConfigCommon
{
    public static int blockIdTorchLit;
    public static int blockIdTorchUnlit;
    public static int itemIdCloth;

    public static boolean torchRecipeYieldsUnlit;
    public static int torchRecipeYieldCount;

    public static boolean torchUpdates;
    public static boolean torchDropsUnlit;
    public static boolean torchSingleUse;

    public static int torchLifespanMin;
    public static int torchLifespanMax;
    public static int torchRandomKillChance;
    public static int torchDestroyChance;

    public static String igniterIdsHeld = (Block.torchWood.blockID + "");
    public static String igniterIdsSet = (Block.torchWood.blockID + "," + Item.flint.itemID + "," + Item.flintAndSteel.itemID + "," + Item.bucketLava.itemID);

    public static int mobZombieTorch;
    public static int mobSkeletonTorch;
    public static boolean mobVillagerTorch;

    public static void loadConfig(Configuration config)
    {
        blockIdTorchLit = config.getBlock(Configuration.CATEGORY_BLOCK, "IdBlockTorchLit", 551, "Block ID for the LIT torch").getInt();
        blockIdTorchUnlit = config.getBlock(Configuration.CATEGORY_BLOCK, "IdBlockTorchUnlit", 550, "Block ID for the UNLIT torch").getInt();
        itemIdCloth = config.get(Configuration.CATEGORY_ITEM, "IdCloth", 4201, "Item ID for cloth").getInt();

        torchRecipeYieldsUnlit = config.get("RECIPE", "OverrideTorchRecipe", true, "True if the torch recipe should yield unlit torches").getBoolean(true);
        torchRecipeYieldCount = getInt(1, 9, config.get("RECIPE", "TorchRecipeYieldCount", 4, "Number of torches the torch recipe should yield. Lower it for a challenge. Min:1 Max:9"));

        torchUpdates = config.get("TORCH", "TorchUpdates", true, "Set to false to disable torches aging, dying out, etc.").getBoolean(false);
        torchDropsUnlit = config.get("TORCH", "DropsUnlit", false, "True if lit torches should drop as UNLIT torches").getBoolean(false);
        torchSingleUse = config.get("TORCH", "SingleUse", true, "True if torches should break when their lifespan is over").getBoolean(true);

        torchLifespanMin = getInt(1, 32000, config.get("TORCH", "MinimumAge", 8000, "MINIMUM lifespan of a torch (8000 = 1 MC day). Maximum of 32000 (4 MC days)"));
        torchLifespanMax = getInt(1, 32000, config.get("TORCH", "MaximumAge", 12000, "MAXIMUM lifespan of a torch (12000 = 1 1/2 MC day). Maximum of 32000 (4 MC days)"));
        torchRandomKillChance = getInt(0, 100, config.get("TORCH", "ChanceToRandomlyBurnOut", 25, "x/100 chance for a torch to burn out. Set to 0 to disable random dying out of torches"));
        torchDestroyChance = getInt(0, 100, config.get("TORCH", "ChanceToDestroy", 30, "x/100 chance that a torch is destroyed instead when it randomly burns out. Set to 0 to disable."));

        igniterIdsHeld = config.get("IGNITERS", "TorchHeld", igniterIdsHeld, "Ids of blocks/items that can ignite HELD torches. Items are separated by commas. IDs and metadata by colons. Note that block and itemstack metadata are DIFFERENT. Example: 2,33:2,33:4,5").getString();
        igniterIdsSet = config.get("IGNITERS", "TorchSet", igniterIdsSet, "Ids of blocks/items that can ignite SET torches. Items are separated by commas. IDs and metadata by colons. Example: 2,33:2,33:4,5").getString();

        mobVillagerTorch = config.get("MOBS", "Villager", true, "True if villagers should kill torches during day and light them up at night").getBoolean(true);
        mobZombieTorch = config.get("MOBS", "Zombie", 5, "1 in x zombies will kill torches when close. Set to 0 to disable").getInt();
        mobSkeletonTorch = config.get("MOBS", "Skeleton", 8, "1 in x skeletons will kill torches when in range. Set to 0 to disable").getInt();

        setChance();
    }

    static int getInt(int min, int max, Property prop)
    {
        int i = prop.getInt();

        if (i < min)
        {
            i = min;
            prop.set(i);
        }

        if (i > max)
        {
            i = max;
            prop.set(i);
        }

        return i;
    }

    private static void setChance()
    {
        if (torchRandomKillChance > 0)
        {
            double dif = torchLifespanMax - torchLifespanMin;
            double prcnt = torchRandomKillChance / 100D;
            torchRandomKillChance = (int)(dif - (dif * prcnt));
        }
    }
}
