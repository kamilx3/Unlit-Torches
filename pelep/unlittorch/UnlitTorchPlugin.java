package pelep.unlittorch;

import static pelep.unlittorch.UnlitTorchPlugin.MOD_ID;
import static pelep.unlittorch.UnlitTorchPlugin.MOD_MCVERSION;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import pelep.pcl.asm.PCLAccessTransformer;
import pelep.unlittorch.handler.LogHandler;

import java.util.Map;

/**
 * @author pelep
 */
@IFMLLoadingPlugin.Name(MOD_ID)
@IFMLLoadingPlugin.TransformerExclusions({"pelep.unlittorch.asm"})
@IFMLLoadingPlugin.MCVersion(MOD_MCVERSION)
public class UnlitTorchPlugin implements IFMLLoadingPlugin
{
    public static final String MOD_NAME = "Unlit Torches";
    public static final String MOD_ID = "UnlitTorch";
    public static final String MOD_VERSION = "2.0.30";
    public static final String MOD_MCVERSION = "1.6.4";
    public static final String MOD_DEPENDENCIES = "required-after:Forge@[9.11.1.965,);required-after:PCL@[1.3.4,);before:Bushwhacker";
    public static final String MOD_CHANNEL = MOD_ID;

    public UnlitTorchPlugin()
    {
        LogHandler.init();
    }

    @Override
    public String[] getLibraryRequestClass()
    {
        return null;
    }

    @Override
    public String[] getASMTransformerClass()
    {
        return null;
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        try
        {
            PCLAccessTransformer.addMapFile("ut_at.cfg");
        }
        catch (NoClassDefFoundError e)
        {
            throw new RuntimeException("UnlitTorch requires PCL!");
        }
    }
}
