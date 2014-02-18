package pelep.unlittorch.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import pelep.pcl.ProtocolException;
import pelep.unlittorch.handler.LogHandler;

import static pelep.unlittorch.UnlitTorchPlugin.MOD_CHANNEL;

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
                PacketCustom packet = PacketCustom.construct(id);
                packet.read(data);
                packet.execute(p, p.worldObj.isRemote);
            }
            catch (ProtocolException e)
            {
                if (player instanceof EntityPlayerMP)
                {
                    ((EntityPlayerMP)player).playerNetServerHandler.kickPlayerFromServer("Protocol Exception!");
                    LogHandler.warning("Kicking player " + ((EntityPlayer)player).username + " for causing a Protocol Exception!");
                }
            }
            catch (ReflectiveOperationException e)
            {
                throw new RuntimeException("Unexpected Reflection exception during Packet construction!", e);
            }
        }
    }
}
