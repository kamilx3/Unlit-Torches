package pelep.unlittorch.packet;

import static pelep.unlittorch.UnlitTorch.LOGGER;
import static pelep.unlittorch.UnlitTorch.MOD_CHANNEL;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import pelep.pcl.ProtocolException;

/**
 * @author pelep
 */
public class PacketHandler implements IPacketHandler
{
    @Override
    public void onPacketData(INetworkManager nm, Packet250CustomPayload pkt, Player player)
    {
        if (pkt.channel.equals(MOD_CHANNEL))
        {
            try
            {
                ByteArrayDataInput data = ByteStreams.newDataInput(pkt.data);
                int id = data.readUnsignedByte();
                EntityPlayer p = (EntityPlayer) player;
                PacketCustom packet = PacketCustom.create(id);
                packet.decode(data);
                if (FMLCommonHandler.instance().getSide().isClient())
                {
                    packet.handleClient(p, p.worldObj.isRemote);
                }
                else
                {
                    packet.handleServer(p);
                }
            }
            catch (ProtocolException e)
            {
                if (player instanceof EntityPlayerMP)
                {
                    ((EntityPlayerMP)player).playerNetServerHandler.kickPlayerFromServer("Protocol Exception!");
                    LOGGER.warning("Kicking player %s for causing a Protocol Exception!", ((EntityPlayer)player).username);
                }
            }
            catch (ReflectiveOperationException e)
            {
                throw new RuntimeException("Unexpected Reflection exception during Packet construction!", e);
            }
        }
    }
}
