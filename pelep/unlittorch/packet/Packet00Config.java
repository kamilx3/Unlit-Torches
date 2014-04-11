package pelep.unlittorch.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import pelep.pcl.ProtocolException;
import pelep.unlittorch.config.ConfigCommon;

/**
 * @author pelep
 */
public class Packet00Config extends PacketCustom
{
    private byte b;
    private short torchLifespanMax;
    private int torchRecipeYieldCount;

    public Packet00Config() {}

    @Override
    public void encode(ByteArrayDataOutput data)
    {
        byte b = 0;
        b |= ConfigCommon.torchRecipeYieldsUnlit ? 1 : 0;
        b |= ConfigCommon.torchUpdates ? 1 << 1 : 0;
        b |= ConfigCommon.torchSingleUse ? 1 << 2 : 0;

        data.writeByte(b);
        data.writeShort(ConfigCommon.torchLifespanMax);
        data.writeByte(ConfigCommon.torchRecipeYieldCount);
    }

    @Override
    public void decode(ByteArrayDataInput data) throws ProtocolException
    {
        b = data.readByte();
        torchLifespanMax = data.readShort();
        torchRecipeYieldCount = data.readByte();
    }

    @Override
    public void handleClient(EntityPlayer p, boolean client) throws ProtocolException
    {
        if (!client) throw new ProtocolException("Config packet received on server side!");

        ConfigCommon.torchRecipeYieldsUnlit = (b & 1) == 1;
        ConfigCommon.torchUpdates = (b & (1 << 1)) == (1 << 1);
        ConfigCommon.torchSingleUse = (b & (1 << 2)) == (1 << 2);
        ConfigCommon.torchLifespanMax = torchLifespanMax;
        ConfigCommon.torchRecipeYieldCount = torchRecipeYieldCount;
    }

    @Override
    public void handleServer(EntityPlayer p) throws ProtocolException
    {
        throw new ProtocolException("Config packet received on server side!");
    }

    @Override
    protected boolean isChunkPacket()
    {
        return true;
    }
}
