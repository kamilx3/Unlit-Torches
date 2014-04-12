package pelep.unlittorch.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import pelep.pcl.ProtocolException;
import pelep.pcl.util.vec.Coordinates;

/**
 * @author pelep
 */
public class Packet03BurnFX extends PacketCustom
{
    private Coordinates pos;
    private int md;

    public Packet03BurnFX() {}

    public Packet03BurnFX(int x, int y, int z, int md)
    {
        this.pos = new Coordinates(x, y, z);
        this.md = md;
    }

    @Override
    public void encode(ByteArrayDataOutput data)
    {
        data.writeInt(pos.x);
        data.writeInt(pos.y);
        data.writeInt(pos.z);
        data.writeByte(md);
    }

    @Override
    public void decode(ByteArrayDataInput data) throws ProtocolException
    {
        pos = new Coordinates(data.readInt(), data.readInt(), data.readInt());
        md = data.readByte();
    }

    @Override
    public void handleClient(EntityPlayer p, boolean client) throws ProtocolException
    {
        if (!client) throw new ProtocolException("Packet was received on wrong side!");

        double dx = pos.x + 0.5D;
        double dy = pos.y + 0.7D;
        double dz = pos.z + 0.5D;

        switch (md)
        {
            case 1:
                p.worldObj.spawnParticle("flame", dx - 0.275D, dy + 0.225D, dz, 0D, 0D, 0D);
                p.worldObj.spawnParticle("flame", dx - 0.325D, dy + 0.000D, dz, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx - 0.275D, dy + 0.225D, dz, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx - 0.325D, dy + 0.000D, dz, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx - 0.375D, dy - 0.225D, dz, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx - 0.425D, dy - 0.450D, dz, 0D, 0D, 0D);
                break;
            case 2:
                p.worldObj.spawnParticle("flame", dx + 0.275D, dy + 0.225D, dz, 0D, 0D, 0D);
                p.worldObj.spawnParticle("flame", dx + 0.325D, dy + 0.000D, dz, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx + 0.275D, dy + 0.225D, dz, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx + 0.325D, dy + 0.000D, dz, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx + 0.375D, dy - 0.225D, dz, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx + 0.425D, dy - 0.450D, dz, 0D, 0D, 0D);
                break;
            case 3:
                p.worldObj.spawnParticle("flame", dx, dy + 0.225D, dz - 0.275D, 0D, 0D, 0D);
                p.worldObj.spawnParticle("flame", dx, dy + 0.000D, dz - 0.325D, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx, dy + 0.225D, dz - 0.275D, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx, dy + 0.000D, dz - 0.325D, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx, dy - 0.225D, dz - 0.375D, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx, dy - 0.450D, dz - 0.425D, 0D, 0D, 0D);
                break;
            case 4:
                p.worldObj.spawnParticle("flame", dx, dy + 0.225D, dz + 0.275D, 0D, 0D, 0D);
                p.worldObj.spawnParticle("flame", dx, dy + 0.000D, dz + 0.325D, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx, dy + 0.225D, dz + 0.275D, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx, dy + 0.000D, dz + 0.325D, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx, dy - 0.225D, dz + 0.375D, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx, dy - 0.450D, dz + 0.425D, 0D, 0D, 0D);
                break;
            default:
                p.worldObj.spawnParticle("flame", dx, dy - 0.000D, dz, 0D, 0D, 0D);
                p.worldObj.spawnParticle("flame", dx, dy - 0.250D, dz, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx, dy - 0.000D, dz, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx, dy - 0.225D, dz, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx, dy - 0.450D, dz, 0D, 0D, 0D);
                p.worldObj.spawnParticle("smoke", dx, dy - 0.675D, dz, 0D, 0D, 0D);
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
        return false;
    }
}
