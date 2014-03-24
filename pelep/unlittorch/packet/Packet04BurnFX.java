package pelep.unlittorch.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import pelep.pcl.ProtocolException;

/**
 * @author pelep
 */
public class Packet04BurnFX extends PacketCustom
{
    private int x;
    private int y;
    private int z;
    private int md;

    public Packet04BurnFX() {}

    public Packet04BurnFX(int x, int y, int z, int md)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.md = md;
    }

    @Override
    public void encode(ByteArrayDataOutput data)
    {
        data.writeInt(this.x);
        data.writeInt(this.y);
        data.writeInt(this.z);
        data.writeByte(this.md);
    }

    @Override
    public void decode(ByteArrayDataInput data) throws ProtocolException
    {
        this.x = data.readInt();
        this.y = data.readInt();
        this.z = data.readInt();
        this.md = data.readByte();
    }

    @Override
    public void handleClient(EntityPlayer p, boolean client) throws ProtocolException
    {
        if (!client) throw new ProtocolException("Packet was received on wrong side!");

        double dx = this.x + 0.5D;
        double dy = this.y + 0.7D;
        double dz = this.z + 0.5D;

        switch (this.md)
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
