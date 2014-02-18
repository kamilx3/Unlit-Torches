package pelep.unlittorch;

import static pelep.unlittorch.UnlitTorchPlugin.MOD_NAME;
import static pelep.unlittorch.UnlitTorchPlugin.MOD_ID;
import static pelep.unlittorch.UnlitTorchPlugin.MOD_VERSION;
import static pelep.unlittorch.UnlitTorchPlugin.MOD_MCVERSION;
import static pelep.unlittorch.UnlitTorchPlugin.MOD_DEPENDENCIES;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import pelep.unlittorch.handler.ConnectionHandler;
import pelep.unlittorch.handler.LogHandler;
import pelep.unlittorch.packet.PacketHandler;
import pelep.unlittorch.proxy.ProxyCommon;

/**
 * @author pelep
 */
@Mod(name = MOD_NAME, modid = MOD_ID, version = MOD_VERSION, acceptedMinecraftVersions = MOD_MCVERSION, dependencies = MOD_DEPENDENCIES)
@NetworkMod(clientSideRequired = true, serverSideRequired = true, clientPacketHandlerSpec = @SidedPacketHandler(channels = MOD_ID, packetHandler = PacketHandler.class), connectionHandler = ConnectionHandler.class)
public class UnlitTorch
{
    @Instance(MOD_ID)
    public static UnlitTorch instance;

    @SidedProxy(clientSide = "pelep.unlittorch.proxy.ProxyClient", serverSide = "pelep.unlittorch.proxy.ProxyCommon")
    public static ProxyCommon proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        LogHandler.init();

        proxy.setUpConfig(e.getSuggestedConfigurationFile());
        proxy.registerTorches();
        proxy.registerTileEntity();
        proxy.registerRenderers();
    }

    @EventHandler
    public void init(FMLInitializationEvent e)
    {
        proxy.registerTrackers();
        proxy.registerLightSources();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
        proxy.setUpIgniters();
        proxy.registerRecipes();
        proxy.checkTorch();
    }
}
