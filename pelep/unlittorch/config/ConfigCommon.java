package pelep.unlittorch.config;

import java.util.HashMap;

import pelep.unlittorch.handler.IgnitersHandler;
import pelep.unlittorch.handler.LogHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ConfigCommon
{
    public static int blockIdLanternLit;
    public static int blockIdLanternUnlit;
    public static int blockIdLanternHook;

    public static int itemIdLanternFuel;
    public static int itemIdTinderbox;
    public static int itemIdTinderboxFS;
    
    public static boolean recipeOverrideTorches;
    public static boolean recipeDisableEmptyTinderbox;
    public static boolean recipeDisableLanternHandle;
    
    public static boolean torchIsSimple;
    public static boolean torchDropsUnlitLit;
    public static boolean torchDropsUnlitUnlit;
    public static boolean torchSingleUse;
    public static int torchLifespanMin;
    public static int torchLifespanMax;
    public static int torchKillChance;
    public static String torchIgniterIdsHeld = (Block.torchWood.blockID + "");
    public static String torchIgniterIdsSet = (Block.torchWood.blockID + "," + Item.flint.itemID + "," + Item.flintAndSteel.itemID + "," + Item.bucketLava.itemID + "," + Item.gunpowder.itemID);
    
    public static boolean lanternIsSimple;
    public static int lanternLifespanMax;
    public static int lanternFuelFat;
    public static int lanternFuelOil;
    public static String lanternIgniterIds = (Block.torchWood.blockID + "," + Item.flintAndSteel.itemID + "," + Item.bucketLava.itemID);
    public static String lanternTinderIds = (Block.leaves.blockID + "," + Block.cloth.blockID + "," + Block.field_111038_cB.blockID + "," + Item.wheat.itemID + "," + Item.paper.itemID);
    public static long lanternBurnDamageInterval;

    public static int mobZombieTorch;
    public static int mobSkeletonTorch;
    public static boolean mobVillagerTorch;
    public static boolean mobVillagerLantern;
    public static HashMap<String, Integer> mobDropsFat;

    public static void loadConfig(Configuration config)
    {
        blockIdLanternHook = config.getBlock("LANTERN", "IdBlockLanternHook", 500, "Block and item IDs for the lantern-related stuff").getInt();
        blockIdLanternLit = config.getBlock("LANTERN", "IdBlockLanternLit", 501).getInt();
        blockIdLanternUnlit = config.getBlock("LANTERN", "IdBlockLanternUnlit", 502).getInt();

        itemIdLanternFuel = config.get("LANTERN", "IdItemLanternFuel", 4100).getInt();
        itemIdTinderbox = config.get("LANTERN", "IdItemTinderbox", 4101).getInt();
        itemIdTinderboxFS = config.get("LANTERN", "IdItemTinderboxFS", 4102).getInt();
        
        recipeOverrideTorches = config.get("RECIPE", "OverrideTorchRecipe", true, "True if the torch recipe should yield unlit torches").getBoolean(true);
        recipeDisableEmptyTinderbox = config.get("RECIPE", "DisableEmptyTinderboxRecipe", false, "If the recipe for the empty tinderbox is overriding another mod's recipe, set this to true").getBoolean(false);
        recipeDisableLanternHandle = config.get("RECIPE", "DisableLanternHandleRecipe", false, "If the recipe for the lantern handle is overriding another mod's recipe, set this to true").getBoolean(false);
        
        torchIsSimple = config.get(Configuration.CATEGORY_GENERAL, "SimpleTorch", false, "Set to true to disable TORCHES aging, dying out, etc.").getBoolean(false);
        torchLifespanMin = getInt(0, 32000, config.get("TORCH LIT", "MinimumAge", 8000, "MINIMUM lifespan of a torch (8000 = 1 Minecraft day). Maximum of 32000"));
        torchLifespanMax = getInt(0, 32000, config.get("TORCH LIT", "MaximumAge", 12000, "MAXIMUM lifespan of a torch (12000 = 1 1/2 Minecraft day). Maximum of 32000"));
        torchDropsUnlitLit = config.get("TORCH LIT", "DropsUnlit", true, "True if lit torches should drop as UNLIT torches").getBoolean(true);
        torchDropsUnlitUnlit = config.get("TORCH UNLIT", "DropsUnlit", true, "True if unlit torches should drop as UNLIT torches").getBoolean(true);
        torchKillChance = getInt(0, 100, config.get("TORCH LIT", "ChanceToBurnOut", 25, "x/100 chance for a torch to burn out. Set to 0 to disable random dying out of torches"));
        torchSingleUse = config.get("TORCH LIT", "SingleUse", true, "True if torches should break when their lifespan is over").getBoolean(true);
        torchIgniterIdsHeld = config.get("IGNITERS/TINDER", "Igniters-TorchHeld", torchIgniterIdsHeld, "Block/Item IDs of HELD torch igniters. Items are separated by commas. IDs and metadata by colons. Note that block and itemstack metadata are DIFFERENT. Example: 2,33:2,33:4,5").getString();
        torchIgniterIdsSet = config.get("IGNITERS/TINDER", "Igniters-TorchSet", torchIgniterIdsSet + "," + itemIdTinderboxFS, "Block/Item IDs of SET torch igniters. Items are separated by commas. IDs and metadata by colons. Example: 2,33:2,33:4,5").getString();

        lanternIsSimple = config.get(Configuration.CATEGORY_GENERAL, "SimpleLantern", false, "Set to true to disable LANTERNS aging, dying out, etc.").getBoolean(false);
        lanternLifespanMax = getInt(0, 32000, config.get("LANTERN", "MaximumAge", 16000, "MAXIMUM lifespan of a lantern (16000 = 2 Minecraft days). Maximum of 32000"));
        lanternFuelFat = getInt(0, 32000, config.get("LANTERN", "FuelFat", 1060, "Amount of fuel FAT gives to a lantern (Bottled fat will give 3x this). 1060 = 1/15 Minecraft day"));
        lanternFuelOil = getInt(0, 32000, config.get("LANTERN", "FuelOil", 8000, "Amount of fuel OIL gives to a lantern. 8000 = 1 Minecraft day"));
        lanternBurnDamageInterval = config.get("LANTERN", "BurnInterval", 60, "Interval between burn damage in ticks while holding a LIT LANTERN WITHOUT A HANDLE").getInt();
        lanternIgniterIds = config.get("IGNITERS/TINDER", "Igniters-Lantern", lanternIgniterIds + "," + itemIdTinderboxFS, "Block/Item IDs of lantern igniters. Items are separated by commas. IDs and metadata by colons. Example: 2,33:2,33:4,5").getString();
        lanternTinderIds = config.get("IGNITERS/TINDER", "Tinder", lanternTinderIds, "Block/Item IDs of tinder. Items are separated by commas. IDs and metadata by colons. Example: 2,33:2,33:4,5").getString();

        mobVillagerTorch = config.get("MODIFY MOBS", "VillagerTorch", true, "True if villagers should kill torches during day and light them up at night").getBoolean(true);
        mobVillagerLantern = config.get("MODIFY MOBS", "VillagerLantern", true, "True if villager priests and librarians should spawn with lanterns").getBoolean(true);
        mobZombieTorch = config.get("MODIFY MOBS", "Zombie", 5, "1 in x zombies will kill torches when close. Set to 0 to disable").getInt();
        mobSkeletonTorch = config.get("MODIFY MOBS", "Skeleton", 8, "1 in x skeletons will kill torches when in range. Set to 0 to disable").getInt();

        setFatMobs(config.get("MODIFY MOBS", "FatMobs", "Pig:35,Cow:20", "List of mobs that can drop animal fat and x/100 CHANCE the mob will drop animal fat on death. CAPITALIZATION is important. Leave empty to disable mobs dropping fat").getString());
        setChance();
    }
    
    private static int getInt(int min, int max, Property prop)
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
        if (torchKillChance > 0)
        {
            double dif = torchLifespanMax - torchLifespanMin;
            double prcnt = torchKillChance / 100D;
            torchKillChance = (int)(dif - (dif * prcnt));
        }
    }
    
    public static void postInit()
    {
        IgnitersHandler.setSetTorchIgniters();
        IgnitersHandler.setHeldTorchIgniters();
        IgnitersHandler.setLanternIgniters();
        IgnitersHandler.setLanternTinder();
    }

    private static void setFatMobs(String maps)
    {
        mobDropsFat = new HashMap();
        
        if (maps != null && maps.length() > 0)
        {
            for (String map : maps.split(","))
            {
                if (map != null && map.length() > 0)
                {
                    String[] kv = map.split(":");
                    
                    if (kv.length == 2)
                    {
                        try
                        {
                            String mob = kv[0];
                            
                            if (mobDropsFat.containsKey(mob))
                            {
                                LogHandler.info("Ignoring duplicate mapping for %s", mob);
                            }
                            else
                            {
                                int chance = Integer.parseInt(kv[1]);
                                if (chance < 1) chance = 1;
                                if (chance > 100) chance = 100;
                                mobDropsFat.put(mob, chance);
                            }
                        }
                        catch (NumberFormatException e)
                        {
                            LogHandler.warning("Mapping '%s' is invalid. %s is not an acceptable value", map, kv[1]);
                        }
                    }
                    else
                    {
                        LogHandler.warning("Mapping '%s' is invalid", map);
                    }
                }
            }
        }
    }
}
