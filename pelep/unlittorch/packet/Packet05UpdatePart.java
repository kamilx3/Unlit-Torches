package pelep.unlittorch.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import pelep.pcl.ProtocolException;
import pelep.pcl.util.vec.Coordinates;
import pelep.unlittorch.multipart.TorchPartLit;

/**
 * @author pelep
 */
public class Packet05UpdatePart extends PacketCustom
{
    private Coordinates pos;
    private int idx;
    private int dim;
    private int age;

    Packet05UpdatePart() {}

    public Packet05UpdatePart(int idx, Coordinates pos, int dim, int age)
    {
        this.pos = pos;
        this.idx = idx;
        this.dim = dim;
        this.age = age;
    }

    @Override
    void encode(ByteArrayDataOutput data)
    {
        data.writeInt(idx);
        data.writeInt(pos.x);
        data.writeInt(pos.y);
        data.writeInt(pos.z);
        data.writeInt(dim);
        data.writeInt(age);
    }

    @Override
    void decode(ByteArrayDataInput data) throws ProtocolException
    {
        idx = data.readInt();
        pos = new Coordinates(data.readInt(), data.readInt(), data.readInt());
        dim = data.readInt();
        age = data.readInt();
    }

    @Override
    void handleClient(EntityPlayer p, boolean client) throws ProtocolException
    {
        if (!client) throw new ProtocolException("Packet was received on wrong side!");
        if (p.worldObj.provider.dimensionId != dim || !p.worldObj.blockExists(pos.x, pos.y, pos.z)) return;
        TorchPartLit.updatePart(p.worldObj, pos, idx, age);
    }

    @Override
    void handleServer(EntityPlayer p) throws ProtocolException
    {
        throw new ProtocolException("Packet was received on wrong side!");
    }

    @Override
    protected boolean isChunkPacket()
    {
        return true;
    }
}
