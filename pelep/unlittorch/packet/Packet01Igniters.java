package pelep.unlittorch.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import pelep.pcl.ProtocolException;
import pelep.unlittorch.handler.IgnitersHandler;

/**
 * @author pelep
 */
public class Packet01Igniters extends PacketCustom
{
    private byte type;
    private String igniters = "";

    public Packet01Igniters() {}

    public Packet01Igniters(byte type, String igniters)
    {
        this.type = type;
        this.igniters = igniters;

        if (this.igniters == null || this.igniters.length() < 1)
            this.igniters = "";
    }

    @Override
    public void encode(ByteArrayDataOutput data)
    {
        data.writeByte(type);
        data.writeShort(igniters.length());
        data.writeChars(igniters);
    }

    @Override
    public void decode(ByteArrayDataInput data) throws ProtocolException
    {
        igniters = "";
        type = data.readByte();
        int size = data.readShort();

        for (int i = 0; i < size; i++)
            igniters += data.readChar();
    }

    @Override
    public void handleClient(EntityPlayer p, boolean client) throws ProtocolException
    {
        if (!client) throw new ProtocolException("Config packet received on server side!");
        IgnitersHandler.syncWithServer(type, igniters);
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
