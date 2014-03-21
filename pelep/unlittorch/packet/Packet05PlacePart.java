package pelep.unlittorch.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import pelep.pcl.ProtocolException;
import pelep.unlittorch.multipart.TorchPartFactory;

/**
 * @author pelep
 */
public class Packet05PlacePart extends PacketCustom
{
    @Override
    public void write(ByteArrayDataOutput data)
    {
    }

    @Override
    public void read(ByteArrayDataInput data) throws ProtocolException
    {
    }

    @Override
    public void execute(EntityPlayer p, boolean remote) throws ProtocolException
    {
        if (remote) throw new ProtocolException("Packet was received on wrong side!");
        TorchPartFactory.place(p, p.worldObj);
    }

    @Override
    protected boolean isChunkPacket()
    {
        return true;
    }
}
