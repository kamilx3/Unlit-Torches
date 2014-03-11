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
        {
            this.igniters = "";
        }
    }

    @Override
    public void write(ByteArrayDataOutput data)
    {
        data.writeByte(this.type);
        data.writeShort(this.igniters.length());
        data.writeChars(this.igniters);
    }

    @Override
    public void read(ByteArrayDataInput data) throws ProtocolException
    {
        this.igniters = "";
        this.type = data.readByte();
        int size = data.readShort();

        for (int i = 0; i < size; i++)
        {
            this.igniters += data.readChar();
        }
    }

    @Override
    public void execute(EntityPlayer p, boolean remote) throws ProtocolException
    {
        if (remote)
        {
            IgnitersHandler.syncWithServer(this.type, this.igniters);
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
