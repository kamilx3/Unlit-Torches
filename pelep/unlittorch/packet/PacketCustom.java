package pelep.unlittorch.packet;

import static pelep.unlittorch.UnlitTorchPlugin.MOD_CHANNEL;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import pelep.pcl.ProtocolException;

/**
 * @author pelep
 */
abstract class PacketCustom
{
    private static final BiMap<Integer, Class<? extends PacketCustom>> packets;

    static
    {
        ImmutableBiMap.Builder<Integer, Class<? extends PacketCustom>> builder = ImmutableBiMap.builder();

        builder.put(0, Packet00Config.class);
        builder.put(1, Packet01Igniters.class);
        builder.put(2, Packet02UpdateEntity.class);
        builder.put(3, Packet03UpdateTile.class);
        builder.put(4, Packet04BurnFX.class);

        packets = builder.build();
    }

    public static PacketCustom construct(int id) throws ProtocolException, ReflectiveOperationException
    {
        Class<? extends PacketCustom> clazz = packets.get(id);

        if (clazz == null)
        {
            throw new ProtocolException("Unknown Packet Id!");
        }
        else
        {
            return clazz.newInstance();
        }
    }

    public final Packet250CustomPayload create()
    {
        ByteArrayDataOutput data = ByteStreams.newDataOutput();

        data.write(getId());
        this.write(data);

        Packet250CustomPayload pkt = new Packet250CustomPayload(MOD_CHANNEL, data.toByteArray());
        pkt.isChunkDataPacket = this.isChunkPacket();

        return pkt;
    }

    private int getId()
    {
        if (packets.inverse().containsKey(this.getClass()))
        {
            return packets.inverse().get(this.getClass());
        }

        throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " is missing a mapping!");
    }

    public abstract void write(ByteArrayDataOutput data);

    public abstract void read(ByteArrayDataInput data) throws ProtocolException;

    public abstract void execute(EntityPlayer p, boolean remote) throws ProtocolException;

    protected abstract boolean isChunkPacket();
}
