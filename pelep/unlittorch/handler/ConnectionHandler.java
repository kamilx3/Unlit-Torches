package pelep.unlittorch.handler;

import pelep.unlittorch.config.ConfigClient;
import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class ConnectionHandler implements IConnectionHandler
{
    @Override
    public void playerLoggedIn(Player p, NetHandler nh, INetworkManager nm)
    {
        PacketSender.sendConfigPacket(p);
        PacketSender.sendIgniterOrTinderPacket((byte)1, p, ConfigCommon.torchIgniterIdsSet);
        PacketSender.sendIgniterOrTinderPacket((byte)2, p, ConfigCommon.torchIgniterIdsHeld);
        PacketSender.sendIgniterOrTinderPacket((byte)3, p, ConfigCommon.lanternIgniterIds);
        PacketSender.sendIgniterOrTinderPacket((byte)4, p, ConfigCommon.lanternTinderIds);
    }
    
    @Override
    public void connectionClosed(INetworkManager nm)
    {
        if (FMLCommonHandler.instance().getSide().isClient() && Minecraft.getMinecraft().getIntegratedServer() == null)
        {
            LogHandler.info("Unsyncing with server");
            ConfigClient.unsync();
        }
    }

    @Override
    public String connectionReceived(NetLoginHandler nh, INetworkManager nm)
    {
        return null;
    }

    @Override
    public void connectionOpened(NetHandler nh, String server, int port, INetworkManager nm)
    {
    }

    @Override
    public void connectionOpened(NetHandler nh, MinecraftServer server, INetworkManager nm)
    {
    }
    
    @Override
    public void clientLoggedIn(NetHandler nh, INetworkManager nm, Packet1Login login)
    {
    }
}
