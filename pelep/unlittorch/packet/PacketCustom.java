package pelep.unlittorch.packet;

import static pelep.unlittorch.UnlitTorch.MOD_CHANNEL;

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
        builder.put(3, Packet03BurnFX.class);
        builder.put(4, Packet04PlacePart.class);
        builder.put(5, Packet05UpdatePart.class);

        packets = builder.build();
    }

    static PacketCustom create(int id) throws ProtocolException, ReflectiveOperationException
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
        this.encode(data);

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

    abstract void encode(ByteArrayDataOutput data);

    abstract void decode(ByteArrayDataInput data) throws ProtocolException;

    abstract void handleClient(EntityPlayer p, boolean client) throws ProtocolException;

    abstract void handleServer(EntityPlayer p) throws ProtocolException;

    protected abstract boolean isChunkPacket();
}
