package pelep.unlittorch;

import static pelep.unlittorch.UnlitTorch.MOD_NAME;
import static pelep.unlittorch.UnlitTorch.MOD_ID;
import static pelep.unlittorch.UnlitTorch.MOD_VERSION;
import static pelep.unlittorch.UnlitTorch.MOD_MCVERSION;
import static pelep.unlittorch.UnlitTorch.MOD_DEPENDENCIES;

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
import pelep.unlittorch.packet.PacketHandler;
import pelep.unlittorch.proxy.ProxyCommon;

/**
 * @author pelep
 */
@Mod(name = MOD_NAME, modid = MOD_ID, version = MOD_VERSION, acceptedMinecraftVersions = MOD_MCVERSION, dependencies = MOD_DEPENDENCIES)
@NetworkMod(clientSideRequired = true, serverSideRequired = true, clientPacketHandlerSpec = @SidedPacketHandler(channels = MOD_ID, packetHandler = PacketHandler.class), connectionHandler = ConnectionHandler.class)
public class UnlitTorch
{
    public static final String MOD_NAME = "Unlit Torches";
    public static final String MOD_ID = "unlittorch";
    public static final String MOD_VERSION = "2.0.0";
    public static final String MOD_MCVERSION = "1.6.4";
    public static final String MOD_DEPENDENCIES = "required-after:Forge@[9.11.1.965,);required-after:PCL@[1.3.4,);before:Bushwhacker";
    public static final String MOD_CHANNEL = MOD_ID;

    @Instance(MOD_ID)
    public static UnlitTorch instance;

    @SidedProxy(clientSide = "pelep.unlittorch.proxy.ProxyClient", serverSide = "pelep.unlittorch.proxy.ProxyCommon")
    public static ProxyCommon proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        proxy.setUpConfig(e.getSuggestedConfigurationFile());
        proxy.registerTorches();
        proxy.registerItems();
        proxy.registerTileEntity();
        proxy.registerRenderers();
    }

    @EventHandler
    public void init(FMLInitializationEvent e)
    {
        proxy.registerListeners();
        proxy.registerLightSources();
        proxy.registerRecipes();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
        proxy.setUpIgniters();
        proxy.checkTorch();
    }
}
