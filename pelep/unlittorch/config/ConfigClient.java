package pelep.unlittorch.config;

import static pelep.unlittorch.config.ConfigCommon.getInt;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.Configuration;

/**
 * @author pelep
 */
@SideOnly(Side.CLIENT)
public class ConfigClient
{
    private static boolean torchRecipeYieldsUnlit;
    private static int torchRecipeYieldCount;
    private static boolean torchUpdates;
    private static boolean torchSingleUse;
    private static int torchLifespanMax;

    public static boolean enableDynamicLighting;
    public static int torchLightValue;

    public static void loadConfig(Configuration config)
    {
        ConfigCommon.loadConfig(config);

        torchRecipeYieldsUnlit = ConfigCommon.torchRecipeYieldsUnlit;
        torchRecipeYieldCount = ConfigCommon.torchRecipeYieldCount;
        torchUpdates = ConfigCommon.torchUpdates;
        torchSingleUse = ConfigCommon.torchSingleUse;
        torchLifespanMax = ConfigCommon.torchLifespanMax;

        enableDynamicLighting = config.get("LIGHTING", "EnableDynamicLighting", true, "True if dynamic lighting for lit torches should be enabled").getBoolean(true);
        torchLightValue = getInt(0, 15, config.get("LIGHTING", "TorchLightValue", 13, "The light value of HELD lit torches and DROPPED lit torches only"));
    }

    public static void desyncFromServer()
    {
        ConfigCommon.torchRecipeYieldsUnlit = torchRecipeYieldsUnlit;
        ConfigCommon.torchRecipeYieldCount = torchRecipeYieldCount;
        ConfigCommon.torchUpdates = torchUpdates;
        ConfigCommon.torchSingleUse = torchSingleUse;
        ConfigCommon.torchLifespanMax = torchLifespanMax;
    }
}
