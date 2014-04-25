package pelep.unlittorch.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import pelep.pcl.ProtocolException;
import pelep.unlittorch.config.ConfigCommon;

/**
 * @author pelep
 */
public class Packet06UpdateInv extends PacketCustom
{
    private int age;

    Packet06UpdateInv() {}

    public Packet06UpdateInv(int age)
    {
        this.age = age;
    }

    @Override
    void encode(ByteArrayDataOutput data)
    {
        data.writeInt(age);
    }

    @Override
    void decode(ByteArrayDataInput data) throws ProtocolException
    {
        age = data.readInt();
    }

    @Override
    void handleClient(EntityPlayer p, boolean client) throws ProtocolException
    {
        if (!client) throw new ProtocolException("Packet was received on wrong side!");
        ItemStack ist = p.getHeldItem();
        if (ist != null && ist.itemID == ConfigCommon.blockIdTorchLit)
            ist.setItemDamage(age);
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
