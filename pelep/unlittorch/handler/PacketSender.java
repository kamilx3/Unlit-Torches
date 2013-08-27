package pelep.unlittorch.handler;

import static pelep.unlittorch.UnlitTorchPlugin.MOD_ID;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;

public class PacketSender
{
    private static Packet250CustomPayload getPacket(byte type, boolean chunk, Object... objs)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        Packet250CustomPayload pkt = new Packet250CustomPayload();

        try
        {
            dos.writeByte(type);
            
            for (Object o : objs)
            {
                if (o instanceof Byte)
                {
                    dos.writeByte((Byte) o);
                }
                else if (o instanceof Short)
                {
                    dos.writeShort((Short) o);
                }
                else if (o instanceof Integer)
                {
                    dos.writeInt((Integer) o);
                }
                else if (o instanceof Boolean)
                {
                    dos.writeBoolean((Boolean) o);
                }
                else if (o instanceof String)
                {
                    dos.writeChars((String) o);
                }
            }
        }
        catch (Exception e)
        {
            LogHandler.severe("Failed to create packet (%d)", type);
            return null;
        }
        
        pkt.channel = MOD_ID;
        pkt.data = bos.toByteArray();
        pkt.length = bos.size();
        pkt.isChunkDataPacket = chunk;
        
        return pkt;
    }

    public static void sendConfigPacket(Player p)
    {
        byte b = 0;
        
        b |= ConfigCommon.torchIsSimple ? 1 : 0;
        b |= ConfigCommon.lanternIsSimple ? 1 << 1 : 0;
        b |= ConfigCommon.recipeDisableEmptyTinderbox ? 1 << 2 : 0;
        b |= ConfigCommon.recipeDisableLanternHandle ? 1 << 3 : 0;
        b |= ConfigCommon.recipeOverrideTorches ? 1 << 4 : 0;
        b |= ConfigCommon.torchSingleUse ? 1 << 5 : 0;
        
        Object[] o = new Object[]
                {
                (short) ConfigCommon.torchLifespanMax,
                (short) ConfigCommon.lanternLifespanMax,
                (short) ConfigCommon.lanternFuelFat,
                (short) ConfigCommon.lanternFuelOil,
                b
                };

        Packet250CustomPayload pkt = getPacket((byte)0, true, o);
        
        if (pkt != null)
        {
            LogHandler.fine("Sending packet (0) to player %s", ((EntityPlayer)p).getEntityName());
            PacketDispatcher.sendPacketToPlayer(pkt, p);
        }
    }

    public static void sendIgniterOrTinderPacket(byte type, Player p, String igniters)
    {
        if (!igniters.equals(""))
        {
            Packet250CustomPayload pkt = getPacket(type, true, (short)igniters.length(), igniters);
            
            if (pkt != null)
            {
                LogHandler.fine("Sending packet (%d) to player %s", type, ((EntityPlayer)p).getEntityName());
                PacketDispatcher.sendPacketToPlayer(pkt, p);
            }
        }
    }
    
    public static void sendAgePacket(int age, int x, int y, int z, int dim)
    {
        Packet250CustomPayload pkt = getPacket((byte) 5, true, age, x, y, z, dim);
        
        if (pkt != null)
        {
            PacketDispatcher.sendPacketToAllInDimension(pkt, dim);
        }
    }

    public static void sendEntityPacket(EntityItem ei, byte type)
    {
        Packet250CustomPayload pkt = getPacket(type, false, ei.entityId, ei.worldObj.provider.dimensionId);

        if (pkt != null)
        {
            PacketDispatcher.sendPacketToAllInDimension(pkt, ei.worldObj.provider.dimensionId);
        }
    }

    public static void sendInventoryPacket(EntityPlayer p, byte item, byte slot)
    {
        Packet250CustomPayload pkt = getPacket((byte) 8, false, item, slot);
        
        if (pkt != null)
        {
            PacketDispatcher.sendPacketToPlayer(pkt, (Player)p);
        }
    }
}
