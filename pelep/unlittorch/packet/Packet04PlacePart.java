package pelep.unlittorch.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import pelep.pcl.ProtocolException;
import pelep.unlittorch.multipart.TorchPartFactory;

/**
 * @author pelep
 */
public class Packet04PlacePart extends PacketCustom
{
    @Override
    void encode(ByteArrayDataOutput data)
    {
    }

    @Override
    void decode(ByteArrayDataInput data) throws ProtocolException
    {
    }

    @Override
    void handleClient(EntityPlayer p, boolean client) throws ProtocolException
    {
        if (client) throw new ProtocolException("Packet was received on wrong side!");
        TorchPartFactory.place(p);
    }

    @Override
    void handleServer(EntityPlayer p)
    {
        TorchPartFactory.place(p);
    }

    @Override
    protected boolean isChunkPacket()
    {
        return true;
    }
}
