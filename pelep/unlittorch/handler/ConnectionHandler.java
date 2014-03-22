package pelep.unlittorch.handler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import pelep.unlittorch.config.ConfigClient;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.packet.Packet00Config;
import pelep.unlittorch.packet.Packet01Igniters;

/**
 * @author pelep
 */
public class ConnectionHandler implements IConnectionHandler
{
    @Override
    public void playerLoggedIn(Player p, NetHandler nh, INetworkManager nm)
    {
        LogHandler.fine("Sending config packets to player %s", ((EntityPlayer)p).getEntityName());
        PacketDispatcher.sendPacketToPlayer(new Packet00Config().create(), p);
        PacketDispatcher.sendPacketToPlayer(new Packet01Igniters((byte)0, ConfigCommon.igniterIdsSet).create(), p);
        PacketDispatcher.sendPacketToPlayer(new Packet01Igniters((byte)1, ConfigCommon.igniterIdsHeld).create(), p);
    }

    @Override
    public void connectionClosed(INetworkManager nm)
    {
        if (FMLCommonHandler.instance().getSide().isClient() && Minecraft.getMinecraft().getIntegratedServer() == null)
        {
            LogHandler.info("Unsyncing with server");
            ConfigClient.desyncFromServer();
            IgnitersHandler.desyncFromServer();
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
    public void clientLoggedIn(NetHandler nh, INetworkManager nm, Packet1Login pkt)
    {
    }
}
