package pelep.unlittorch.packet;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import pelep.pcl.ProtocolException;
import pelep.unlittorch.multipart.TorchPartLit;

/**
 * @author pelep
 */
public class Packet05UpdatePart extends PacketCustom
{
    private int index;
    private int x;
    private int y;
    private int z;
    private int dim;
    private int age;

    Packet05UpdatePart() {}

    public Packet05UpdatePart(int index, int x, int y, int z, int dim, int age)
    {
        this.index = index;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
        this.age = age;
    }

    @Override
    public void encode(ByteArrayDataOutput data)
    {
        data.writeInt(this.index);
        data.writeInt(this.x);
        data.writeInt(this.y);
        data.writeInt(this.z);
        data.writeInt(this.dim);
        data.writeInt(this.age);
    }

    @Override
    public void decode(ByteArrayDataInput data) throws ProtocolException
    {
        this.index = data.readInt();
        this.x = data.readInt();
        this.y = data.readInt();
        this.z = data.readInt();
        this.dim = data.readInt();
        this.age = data.readInt();
    }

    @Override
    public void handleClient(EntityPlayer p, boolean client) throws ProtocolException
    {
        if (!client) throw new ProtocolException("Packet was received on wrong side!");
        if (p.worldObj.provider.dimensionId != this.dim || !p.worldObj.blockExists(this.x, this.y, this.z)) return;

        TileEntity te = p.worldObj.getBlockTileEntity(this.x, this.y, this.z);

        if (te instanceof TileMultipart)
        {
            try
            {
                TileMultipart tm = (TileMultipart) te;
                TMultiPart part = tm.jPartList().get(this.index);

                if (part != null && "unlittorch:torch_lit".equals(part.getType()))
                    ((TorchPartLit)part).setAge(this.age);
            }
            catch (IndexOutOfBoundsException e)
            {
                //
            }
        }
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