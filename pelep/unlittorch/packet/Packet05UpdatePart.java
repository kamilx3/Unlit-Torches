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
        data.writeInt(this.idx);
        data.writeInt(this.pos.x);
        data.writeInt(this.pos.y);
        data.writeInt(this.pos.z);
        data.writeInt(this.dim);
        data.writeInt(this.age);
    }

    @Override
    public void decode(ByteArrayDataInput data) throws ProtocolException
    {
        this.idx = data.readInt();
        this.pos = new Coordinate(data.readInt(), data.readInt(), data.readInt());
        this.dim = data.readInt();
        this.age = data.readInt();
    }

    @Override
    public void handleClient(EntityPlayer p, boolean client) throws ProtocolException
    {
        if (!client) throw new ProtocolException("Packet was received on wrong side!");
        if (p.worldObj.provider.dimensionId != this.dim || !p.worldObj.blockExists(this.pos.x, this.pos.y, this.pos.z)) return;
        TorchPartLit.updatePart(p.worldObj, this.pos, this.idx, this.age);
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
