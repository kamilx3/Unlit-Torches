package pelep.unlittorch.config;

import pelep.pcl.util.UtilConfig;

/**
 * @author pelep
 */
public class ConfigCommon extends UtilConfig
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
    public static String igniterIdsSet = "";

    public static int mobZombieTorch;
    public static int mobSkeletonTorch;
    public static boolean mobVillagerTorch;

    @Override
    protected void load()
    {
        blockIdTorchLit = getBlock("IdBlockTorchLit", 551, "Block ID for the LIT torch");
        blockIdTorchUnlit = getBlock("IdBlockTorchUnlit", 550, "Block ID for the UNLIT torch");
        itemIdCloth = getItem("IdCloth", 4201, "Item ID for cloth");

        setCategory("RECIPE");
        torchRecipeYieldsUnlit = getBoolean("OverrideTorchRecipe", true, "True if the torch recipe should yield unlit torches");
        torchRecipeYieldCount = getInt("TorchRecipeYieldCount", 4, 1, 9, "Number of torches the torch recipe should yield. Lower it for a challenge. Min:1 Max:9");

        setCategory("TORCH");
        torchUpdates = getBoolean("TorchUpdates", true, "Set to false to make lit torches act pretty much like vanilla torches. Disables aging, randomly dying out, etc.");
        torchDropsUnlit = getBoolean("DropsUnlit", false, "True if lit torches should drop as UNLIT torches");
        torchSingleUse = getBoolean("SingleUse", true, "True if torches should break when their lifespan is over");

        torchLifespanMin = getInt("MinimumAge", 1000, 1, 32000, "MINIMUM lifespan of a torch (1000 = 1 MC day). Maximum of 32000 (32 MC days)");
        torchLifespanMax = getInt("MaximumAge", 1500, 1, 32000, "MAXIMUM lifespan of a torch (1500 = 1 1/2 MC day). Maximum of 32000 (32 MC days)");
        torchRandomKillChance = getInt("ChanceToRandomlyBurnOut", 25, 0, 100, "x/100 chance for a torch to burn out. Set to 0 to disable");
        torchDestroyChance = getInt("ChanceToDestroy", 30, 0, 100, "x/100 chance that a torch is destroyed instead when it randomly burns out. Set to 0 to disable");

        String comment = "Block/Item IDs of igniters. If no metadata is specified, all metadata will be valid for the specified ID\n" +
                "The provided example will turn wool (id:35) colored magneta, yellow, purple, blue, and brown to igniters\n" +
                "It will also turn all dyes (id:351) into igniters\n\n" +
                "Syntax: id,id:metadata\n" +
                "Example: 35:2,35:4,35:10-12,351";
        setCategory("IGNITERS", comment);
        igniterIdsHeld = getString("TorchHeld", igniterIdsHeld, "IDs and metadata of blocks that can ignite HELD torches");
        igniterIdsSet = getString("TorchSet", igniterIdsSet, "IDs and metadata of blocks/items that can ignite SET torches");

        setCategory("MOBS");
        mobVillagerTorch = getBoolean("Villager", true, "True if villagers should extinguish torches during day and ignite them at night");
        mobZombieTorch = getInt("Zombie", 5, "1 in x zombies will extinguish torches when in range. Set to 0 to disable");
        mobSkeletonTorch = getInt("Skeleton", 8, "1 in x skeletons will attempt to extinguish torches when in range. Set to 0 to disable");

        setChance();
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
