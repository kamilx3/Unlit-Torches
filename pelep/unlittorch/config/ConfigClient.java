package pelep.unlittorch.config;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.Configuration;

/**
 * @author pelep
 */
@SideOnly(Side.CLIENT)
public class ConfigClient
{
    private static boolean overrideTorchRecipe;
    private static int torchRecipeYieldCount;
    private static boolean torchUpdates;
    private static boolean torchSingleUse;
    private static int torchLifespanMax;

    public static void loadConfig(Configuration config)
    {
        ConfigCommon.loadConfig(config);

        overrideTorchRecipe = ConfigCommon.overrideTorchRecipe;
        torchRecipeYieldCount = ConfigCommon.torchRecipeYieldCount;
        torchUpdates = ConfigCommon.torchUpdates;
        torchSingleUse = ConfigCommon.torchSingleUse;
        torchLifespanMax = ConfigCommon.torchLifespanMax;
    }

    public static void desyncFromServer()
    {
        ConfigCommon.overrideTorchRecipe = overrideTorchRecipe;
        ConfigCommon.torchRecipeYieldCount = torchRecipeYieldCount;
        ConfigCommon.torchUpdates = torchUpdates;
        ConfigCommon.torchSingleUse = torchSingleUse;
        ConfigCommon.torchLifespanMax = torchLifespanMax;
    }
}
