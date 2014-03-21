package pelep.unlittorch.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import pelep.pcl.ProtocolException;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.tileentity.TileEntityTorch;

/**
 * @author pelep
 */
public class Packet03UpdateTile extends PacketCustom
{
    private int x;
    private int y;
    private int z;
    private int dim;
    private int age;

    public Packet03UpdateTile() {}

    public Packet03UpdateTile(int x, int y, int z, int dim, int age)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
        this.age = age;
    }

    @Override
    public void write(ByteArrayDataOutput data)
    {
        data.writeInt(this.x);
        data.writeInt(this.y);
        data.writeInt(this.z);
        data.writeInt(this.dim);
        data.writeInt(this.age);
    }

    @Override
    public void read(ByteArrayDataInput data) throws ProtocolException
    {
        this.x = data.readInt();
        this.y = data.readInt();
        this.z = data.readInt();
        this.dim = data.readInt();
        this.age = data.readInt();
    }

    @Override
    public void execute(EntityPlayer p, boolean remote) throws ProtocolException
    {
        if (remote)
        {
            if (p.worldObj.provider.dimensionId == this.dim &&
                p.worldObj.blockExists(this.x, this.y, this.z) &&
                p.worldObj.getBlockId(this.x, this.y, this.z) == ConfigCommon.blockIdTorchLit)
            {
                TileEntity te = p.worldObj.getBlockTileEntity(this.x, this.y, this.z);
                ((TileEntityTorch)te).setAge(this.age);
            }
        }
        else
        {
            throw new ProtocolException("Packet was received on wrong side!");
        }
    }

    @Override
    protected boolean isChunkPacket()
    {
        return true;
    }
}
