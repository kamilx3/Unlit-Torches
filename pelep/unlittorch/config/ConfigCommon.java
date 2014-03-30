package pelep.unlittorch.config;

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

    public static String igniterIdsHeld = "";
    public static String igniterIdsSet = (Item.flint.itemID + "," + Item.flintAndSteel.itemID + "," + Item.bucketLava.itemID);

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

        torchUpdates = config.get("TORCH", "TorchUpdates", true, "Set to false to disable torches aging, dying out, etc.").getBoolean(true);
        torchDropsUnlit = config.get("TORCH", "DropsUnlit", false, "True if lit torches should drop as UNLIT torches").getBoolean(false);
        torchSingleUse = config.get("TORCH", "SingleUse", true, "True if torches should break when their lifespan is over").getBoolean(true);

        torchLifespanMin = getInt(1, 32000, config.get("TORCH", "MinimumAge", 8000, "MINIMUM lifespan of a torch (8000 = 1 MC day). Maximum of 32000 (4 MC days)"));
        torchLifespanMax = getInt(1, 32000, config.get("TORCH", "MaximumAge", 12000, "MAXIMUM lifespan of a torch (12000 = 1 1/2 MC day). Maximum of 32000 (4 MC days)"));
        torchRandomKillChance = getInt(0, 100, config.get("TORCH", "ChanceToRandomlyBurnOut", 25, "x/100 chance for a torch to burn out. Set to 0 to disable random dying out of torches"));
        torchDestroyChance = getInt(0, 100, config.get("TORCH", "ChanceToDestroy", 30, "x/100 chance that a torch is destroyed instead when it randomly burns out. Set to 0 to disable."));

        String comment = "IDs and metadata of blocks that can ignite HELD torches. If no metadata is specified, all metadata will be valid for the specified ID\n" +
                "Syntax: id,id:metadata\n" +
                "Example: 2,33:2,33:4,5";
        igniterIdsHeld = config.get("IGNITERS", "TorchHeld", igniterIdsHeld, comment).getString();
        comment = "IDs and metadata of blocks/items that can ignite SET torches. If no metadata is specified, all metadata will be valid for the specified ID\n" +
                "Syntax: id,id:metadata\n" +
                "Example: 2,33:2,33:4,5";
        igniterIdsSet = config.get("IGNITERS", "TorchSet", igniterIdsSet, comment).getString();

        mobVillagerTorch = config.get("MOBS", "Villager", true, "True if villagers should extinguish torches during day and ignite them at night").getBoolean(true);
        mobZombieTorch = config.get("MOBS", "Zombie", 5, "1 in x zombies will extinguish torches when in range. Set to 0 to disable").getInt();
        mobSkeletonTorch = config.get("MOBS", "Skeleton", 8, "1 in x skeletons will attempt to extinguish torches when in range. Set to 0 to disable").getInt();

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
