package pelep.unlittorch.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import pelep.pcl.ProtocolException;
import pelep.pcl.util.vec.Coordinate;
import pelep.unlittorch.multipart.TorchPartLit;

/**
 * @author pelep
 */
public class Packet05UpdatePart extends PacketCustom
{
    private Coordinate pos;
    private int idx;
    private int dim;
    private int age;

    Packet05UpdatePart() {}

    public Packet05UpdatePart(int idx, Coordinate pos, int dim, int age)
    {
        this.pos = pos;
        this.idx = idx;
        this.dim = dim;
        this.age = age;
    }

    @Override
    public void encode(ByteArrayDataOutput data)
    {
        data.writeInt(idx);
        data.writeInt(pos.x);
        data.writeInt(pos.y);
        data.writeInt(pos.z);
        data.writeInt(dim);
        data.writeInt(age);
    }

    @Override
    public void decode(ByteArrayDataInput data) throws ProtocolException
    {
        idx = data.readInt();
        pos = new Coordinate(data.readInt(), data.readInt(), data.readInt());
        dim = data.readInt();
        age = data.readInt();
    }

    @Override
    public void handleClient(EntityPlayer p, boolean client) throws ProtocolException
    {
        if (!client) throw new ProtocolException("Packet was received on wrong side!");
        if (p.worldObj.provider.dimensionId != dim || !p.worldObj.blockExists(pos.x, pos.y, pos.z)) return;
        TorchPartLit.updatePart(p.worldObj, pos, idx, age);
    }

    @Override
    public void handleServer(EntityPlayer p) throws ProtocolException
    {
        throw new ProtocolException("Packet was received on wrong side!");
    }

    @Override
    protected boolean isChunkPacket()
    {
        return true;
    }
}
