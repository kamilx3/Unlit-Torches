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
    public void write(ByteArrayDataOutput data)
    {
        byte b = 0;
        b |= ConfigCommon.torchRecipeYieldsUnlit ? 1 << 0 : 0;
        b |= ConfigCommon.torchUpdates ? 1 << 1 : 0;
        b |= ConfigCommon.torchSingleUse ? 1 << 2 : 0;

        data.writeByte(b);
        data.writeShort(ConfigCommon.torchLifespanMax);
        data.writeByte(ConfigCommon.torchRecipeYieldCount);
    }

    @Override
    public void read(ByteArrayDataInput data) throws ProtocolException
    {
        this.b = data.readByte();
        this.torchLifespanMax = data.readShort();
        this.torchRecipeYieldCount = data.readByte();
    }

    @Override
    public void execute(EntityPlayer p, boolean remote) throws ProtocolException
    {
        if (remote)
        {
            ConfigCommon.torchRecipeYieldsUnlit = (this.b & (1 << 0)) == (1 << 0);
            ConfigCommon.torchUpdates = (this.b & (1 << 1)) == (1 << 1);
            ConfigCommon.torchSingleUse = (this.b & (1 << 2)) == (1 << 2);
            ConfigCommon.torchLifespanMax = this.torchLifespanMax;
            ConfigCommon.torchRecipeYieldCount = this.torchRecipeYieldCount;
        }
        else
        {
            throw new ProtocolException("Config packet received on server side!");
        }
    }

    @Override
    protected boolean isChunkPacket()
    {
        return true;
    }
}
