package pelep.unlittorch.config;

import net.minecraft.item.Item;
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
    public static String igniterIdsSet = (Item.flint.itemID + "," + Item.flintAndSteel.itemID + "," + Item.bucketLava.itemID);

    public static int mobZombieTorch;
    public static int mobSkeletonTorch;
    public static boolean mobVillagerTorch;

    @Override
    public void load()
    {
        blockIdTorchLit = this.getBlock("IdBlockTorchLit", 551, "Block ID for the LIT torch");
        blockIdTorchUnlit = this.getBlock("IdBlockTorchUnlit", 550, "Block ID for the UNLIT torch");
        itemIdCloth = this.getItem("IdCloth", 4201, "Item ID for cloth");

        this.ctg = "RECIPE";
        torchRecipeYieldsUnlit = this.getBoolean("OverrideTorchRecipe", true, "True if the torch recipe should yield unlit torches");
        torchRecipeYieldCount = this.getInt("TorchRecipeYieldCount", 4, 1, 9, "Number of torches the torch recipe should yield. Lower it for a challenge. Min:1 Max:9");

        this.ctg = "TORCH";
        torchUpdates = this.getBoolean("TorchUpdates", true, "Set to false to disable torches aging, dying out, etc.");
        torchDropsUnlit = this.getBoolean("DropsUnlit", false, "True if lit torches should drop as UNLIT torches");
        torchSingleUse = this.getBoolean("SingleUse", true, "True if torches should break when their lifespan is over");

        torchLifespanMin = this.getInt("MinimumAge", 8000, 1, 32000, "MINIMUM lifespan of a torch (8000 = 1 MC day). Maximum of 32000 (4 MC days)");
        torchLifespanMax = this.getInt("MaximumAge", 12000, 1, 32000, "MAXIMUM lifespan of a torch (12000 = 1 1/2 MC day). Maximum of 32000 (4 MC days)");
        torchRandomKillChance = this.getInt("ChanceToRandomlyBurnOut", 25, 0, 100, "x/100 chance for a torch to burn out. Set to 0 to disable");
        torchDestroyChance = this.getInt("ChanceToDestroy", 30, 0, 100, "x/100 chance that a torch is destroyed instead when it randomly burns out. Set to 0 to disable");

        this.ctg = "IGNITERS";
        String comment = "IDs and metadata of blocks that can ignite HELD torches. If no metadata is specified, all metadata will be valid for the specified ID\n" +
                "Syntax: id,id:metadata\n" +
                "Example: 2,33:2,33:4,5";
        igniterIdsHeld = this.getString("TorchHeld", igniterIdsHeld, comment);
        comment = "IDs and metadata of blocks/items that can ignite SET torches. If no metadata is specified, all metadata will be valid for the specified ID\n" +
                "Syntax: id,id:metadata\n" +
                "Example: 2,33:2,33:4,5";
        igniterIdsSet = this.getString("TorchSet", igniterIdsSet, comment);

        this.ctg = "MOBS";
        mobVillagerTorch = this.getBoolean("Villager", true, "True if villagers should extinguish torches during day and ignite them at night");
        mobZombieTorch = this.getInt("Zombie", 5, "1 in x zombies will extinguish torches when in range. Set to 0 to disable");
        mobSkeletonTorch = this.getInt("Skeleton", 8, "1 in x skeletons will attempt to extinguish torches when in range. Set to 0 to disable");

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
