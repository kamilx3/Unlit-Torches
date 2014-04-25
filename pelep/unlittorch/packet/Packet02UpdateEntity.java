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

    Packet02UpdateEntity() {}

    public Packet02UpdateEntity(Entity e)
    {
        id = e.entityId;
        dim = e.worldObj.provider.dimensionId;
    }

    @Override
    void encode(ByteArrayDataOutput data)
    {
        data.writeInt(id);
        data.writeInt(dim);
    }

    @Override
    void decode(ByteArrayDataInput data) throws ProtocolException
    {
        id = data.readInt();
        dim = data.readInt();
    }

    @Override
    void handleClient(EntityPlayer p, boolean client) throws ProtocolException
    {
        if (!client) throw new ProtocolException("Packet was received on wrong side!");

        if (p.worldObj.provider.dimensionId == dim)
        {
            Entity e = p.worldObj.getEntityByID(id);

            if (e instanceof EntityItem)
            {
                EntityItem ei = (EntityItem) e;
                ei.getEntityItem().itemID = ConfigCommon.blockIdTorchUnlit;
            }
        }
    }

    @Override
    void handleServer(EntityPlayer p) throws ProtocolException
    {
        throw new ProtocolException("Packet was received on wrong side!");
    }

    @Override
    protected boolean isChunkPacket()
    {
        return false;
    }
}
