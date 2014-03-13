package pelep.unlittorch.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
import pelep.pcl.lights.LightsManager;
import pelep.unlittorch.config.ConfigClient;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.LogHandler;
import pelep.unlittorch.render.RenderBlockTorch;
import pelep.unlittorch.render.RenderItemTorch;
import pelep.unlittorch.tileentity.TileEntityTorch;

import java.io.File;

/**
 * @author pelep
 */
@SideOnly(Side.CLIENT)
public class ProxyClient extends ProxyCommon
{
    @Override
    public void setUpConfig(File f)
    {
        LogHandler.info("Reading config file");

        Configuration config = new Configuration(f);
        config.load();
        ConfigClient.loadConfig(config);
        config.save();

        LogHandler.fine("Read!");
    }

    @Override
    public void registerRenderers()
    {
        LogHandler.info("Registering renderers");
        RenderItemTorch renderItemTorch = new RenderItemTorch();
        MinecraftForgeClient.registerItemRenderer(50, renderItemTorch);
        MinecraftForgeClient.registerItemRenderer(ConfigCommon.blockIdTorchUnlit, renderItemTorch);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTorch.class, new RenderBlockTorch());
    }

    @Override
    public void registerLightSources()
    {
        LogHandler.info("Registering light sources");
        if (ConfigClient.enableDynamicLighting) LightsManager.registerBasicLightSource(50, 13);
    }
}
