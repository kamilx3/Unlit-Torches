package pelep.unlittorch;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import pelep.unlittorch.asm.UTClassTransformer;
import pelep.unlittorch.handler.LogHandler;

@IFMLLoadingPlugin.Name("UnlitTorch")
@IFMLLoadingPlugin.TransformerExclusions({"pelep.unlittorch.asm"})
@IFMLLoadingPlugin.MCVersion("1.6.2")
public class UnlitTorchPlugin implements IFMLLoadingPlugin
{
    public static final String MOD_NAME = "Unlit Torches and Lanterns";
    public static final String MOD_ID = "UnlitTorch";
    public static final String MOD_VERSION = "1.29.29";
    public static final String MOD_MCVERSION = "1.6.2";
    public static final String MOD_DEPENDENCIES = "required-after:Forge@[9.10.0.804,);before:Bushwhacker";
    
    public UnlitTorchPlugin()
    {
        LogHandler.init();
    }
    
    @Override
    @Deprecated
    public String[] getLibraryRequestClass()
    {
        return null;
    }
    
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {"pelep.unlittorch.asm.UTAccessTransformer", "pelep.unlittorch.asm.UTClassTransformer"};
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
        if (data.containsKey("runtimeDeobfuscationEnabled") && !((Boolean)data.get("runtimeDeobfuscationEnabled")))
        {
            UTClassTransformer.setDeobf();
        }
    }
}
