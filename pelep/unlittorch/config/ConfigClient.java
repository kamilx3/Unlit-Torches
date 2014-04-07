package pelep.unlittorch.config;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pelep.pcl.util.UtilConfig;

/**
 * @author pelep
 */
@SideOnly(Side.CLIENT)
public class ConfigClient extends UtilConfig
{
    private static boolean torchRecipeYieldsUnlit;
    private static int torchRecipeYieldCount;
    private static boolean torchUpdates;
    private static boolean torchSingleUse;
    private static int torchLifespanMax;

    public static boolean enableDynamicLighting;
    public static int torchLightValue;

    @Override
    public void load()
    {
        new ConfigCommon().load(this.cfg);

        torchRecipeYieldsUnlit = ConfigCommon.torchRecipeYieldsUnlit;
        torchRecipeYieldCount = ConfigCommon.torchRecipeYieldCount;
        torchUpdates = ConfigCommon.torchUpdates;
        torchSingleUse = ConfigCommon.torchSingleUse;
        torchLifespanMax = ConfigCommon.torchLifespanMax;

        this.ctg = "LIGHTING";
        enableDynamicLighting = this.getBoolean("EnableDynamicLighting", true, "True if dynamic lighting for lit torches should be enabled");
        torchLightValue = this.getInt("TorchLightValue", 13, 0, 15, "The light value of HELD lit torches and DROPPED lit torches only. Max: 15");
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
