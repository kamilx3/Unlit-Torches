package pelep.unlittorch.proxy;

import static pelep.unlittorch.UnlitTorch.LOGGER;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.MinecraftForgeClient;
import pelep.pcl.lights.LightsManager;
import pelep.unlittorch.config.ConfigClient;
import pelep.unlittorch.config.ConfigCommon;
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
        LOGGER.info("Reading config file");
        new ConfigClient().load(f);
        LOGGER.fine("Read!");
    }

    @Override
    public void registerRenderers()
    {
        LOGGER.info("Registering renderers");
        RenderItemTorch renderItemTorch = new RenderItemTorch();
        MinecraftForgeClient.registerItemRenderer(ConfigCommon.blockIdTorchLit, renderItemTorch);
        MinecraftForgeClient.registerItemRenderer(ConfigCommon.blockIdTorchUnlit, renderItemTorch);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTorch.class, new RenderBlockTorch());
    }

    @Override
    public void registerLightSources()
    {
        if (ConfigClient.enableDynamicLighting && ConfigClient.torchLightValue > 0)
        {
            LOGGER.info("Registering light source");
            LightsManager.registerBasicLightSource(ConfigCommon.blockIdTorchLit, ConfigClient.torchLightValue);
        }
    }
}
