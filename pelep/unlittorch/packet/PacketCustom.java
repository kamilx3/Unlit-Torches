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
    private static final BiMap<Integer, Class<? extends PacketCustom>> packets = ImmutableBiMap.<Integer, Class<? extends PacketCustom>>builder()
            .put(0, Packet00Config.class)
            .put(1, Packet01Igniters.class)
            .put(2, Packet02UpdateEntity.class)
            .put(3, Packet03BurnFX.class)
            .put(4, Packet04PlacePart.class)
            .put(5, Packet05UpdatePart.class)
            .put(6, Packet06UpdateInv.class)
            .build();

    static PacketCustom create(int id) throws ProtocolException, ReflectiveOperationException
    {
        Class<? extends PacketCustom> clazz = packets.get(id);
        if (clazz == null) throw new ProtocolException("Unknown packet Id!");
        return clazz.newInstance();
    }

    public final Packet250CustomPayload create()
    {
        ByteArrayDataOutput data = ByteStreams.newDataOutput();

        data.write(getId());
        encode(data);

        Packet250CustomPayload pkt = new Packet250CustomPayload(MOD_CHANNEL, data.toByteArray());
        pkt.isChunkDataPacket = isChunkPacket();

        return pkt;
    }

    private int getId()
    {
        if (!packets.inverse().containsKey(getClass()))
            throw new RuntimeException("Packet " + getClass().getSimpleName() + " is missing a mapping!");
        return packets.inverse().get(getClass());
    }

    abstract void encode(ByteArrayDataOutput data);

    abstract void decode(ByteArrayDataInput data) throws ProtocolException;

    abstract void handleClient(EntityPlayer p, boolean client) throws ProtocolException;

    abstract void handleServer(EntityPlayer p) throws ProtocolException;

    protected abstract boolean isChunkPacket();
}
