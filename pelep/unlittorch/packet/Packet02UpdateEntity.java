package pelep.unlittorch.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import pelep.pcl.ProtocolException;
import pelep.unlittorch.config.ConfigCommon;

/**
 * @author pelep
 */
public class Packet02UpdateEntity extends PacketCustom
{
    private int id;
    private int dim;

    public Packet02UpdateEntity() {}

    public Packet02UpdateEntity(Entity e)
    {
        this.id = e.entityId;
        this.dim = e.worldObj.provider.dimensionId;
    }

    @Override
    public void encode(ByteArrayDataOutput data)
    {
        data.writeInt(this.id);
        data.writeInt(this.dim);
    }

    @Override
    public void decode(ByteArrayDataInput data) throws ProtocolException
    {
        this.id = data.readInt();
        this.dim = data.readInt();
    }

    @Override
    public void handleClient(EntityPlayer p, boolean client) throws ProtocolException
    {
        if (!client) throw new ProtocolException("Packet was received on wrong side!");

        if (p.worldObj.provider.dimensionId == this.dim)
        {
            Entity e = p.worldObj.getEntityByID(this.id);

            if (e != null && e instanceof EntityItem)
            {
                EntityItem ei = (EntityItem) e;
                ei.getEntityItem().itemID = ConfigCommon.blockIdTorchUnlit;
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
        return false;
    }
}
