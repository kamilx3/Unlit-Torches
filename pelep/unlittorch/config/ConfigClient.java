package pelep.unlittorch.config;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pelep.unlittorch.handler.IgnitersHandler;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

@SideOnly(Side.CLIENT)
public class ConfigClient
{
    private static boolean recipeOverrideTorches;
    private static boolean recipeDisableEmptyTinderbox;
    private static boolean recipeDisableLanternHandle;
    
    private static boolean torchIsSimple;
    private static boolean torchSingleUse;
    private static int torchLifespanMax;

    private static boolean lanternIsSimple;
    private static int lanternLifespanMax;
    private static int lanternFuelFat;
    private static int lanternFuelOil;

    public static boolean lightsEnablePlayerMP;
    public static boolean lightsEnableMobs;
    public static boolean lightsEnableItems;
    public static int lightsIntervalPlayer;
    public static int lightsIntervalOthers;
    public static int lightsStrengthTorch;
    public static int lightsStrengthLantern;
    
    public static void loadConfig(Configuration config)
    {
        ConfigCommon.loadConfig(config);
        
        recipeOverrideTorches = ConfigCommon.recipeOverrideTorches;
        recipeDisableEmptyTinderbox = ConfigCommon.recipeDisableEmptyTinderbox;
        recipeDisableLanternHandle = ConfigCommon.recipeDisableLanternHandle;
        
        torchIsSimple = ConfigCommon.torchIsSimple;
        torchSingleUse = ConfigCommon.torchSingleUse;
        torchLifespanMax = ConfigCommon.torchLifespanMax;
        
        lanternIsSimple = ConfigCommon.lanternIsSimple;
        lanternFuelFat = ConfigCommon.lanternFuelFat;
        lanternFuelOil = ConfigCommon.lanternFuelOil;
        lanternLifespanMax = ConfigCommon.lanternLifespanMax;
        
        lightsIntervalPlayer = config.get("LIGHTING", "Player", 0, "Lighting update interval for torches/lanterns held by YOUR player. Set to -1 to disable").getInt();
        lightsIntervalOthers = config.get("LIGHTING", "Others", 20, "Lighting update interval for torches/lanterns held by OTHER PLAYERS, MOBS, and DROPPED ITEMS. Set to -1 to disable all three").getInt();
        lightsEnablePlayerMP = config.get("LIGHTING", "EnablePlayerMP", true, "Set to false to disable lights from OTHER PLAYERS").getBoolean(false);
        lightsEnableMobs = config.get("LIGHTING", "EnableMobs", true, "Set to false to disable lights from MOBS").getBoolean(false);
        lightsEnableItems = config.get("LIGHTING", "EnableItems", true, "Set to false to disable lights from DROPPED ITEMS").getBoolean(false);
        lightsStrengthTorch = getInt(15, config.get("LIGHTING", "StrengthTorch", 13, "Strength of a torch's light. MAXIMUM of 15. Set to 0 to disable"));
        lightsStrengthLantern = getInt(15, config.get("LIGHTING", "StrengthLantern", 15, "Strength of a lantern's light. MAXIMUM of 15. Set to 0 to disable"));
    }
    
    private static int getInt(int max, Property prop)
    {
        int i = prop.getInt();
        
        if (i > max)
        {
            i = max;
            prop.set(i);
        }
        
        return i;
    }

    public static void unsync()
    {
        IgnitersHandler.unsync();
        
        ConfigCommon.recipeOverrideTorches = recipeOverrideTorches;
        ConfigCommon.recipeDisableEmptyTinderbox = recipeDisableEmptyTinderbox;
        ConfigCommon.recipeDisableLanternHandle = recipeDisableLanternHandle;
        
        ConfigCommon.torchIsSimple = torchIsSimple;
        ConfigCommon.torchSingleUse = torchSingleUse;
        ConfigCommon.torchLifespanMax = torchLifespanMax;
        
        ConfigCommon.lanternIsSimple = lanternIsSimple;
        ConfigCommon.lanternFuelFat = lanternFuelFat;
        ConfigCommon.lanternFuelOil = lanternFuelOil;
        ConfigCommon.lanternLifespanMax = lanternLifespanMax;
    }
}
