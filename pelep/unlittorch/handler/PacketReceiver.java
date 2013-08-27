package pelep.unlittorch.handler;

import static pelep.unlittorch.UnlitTorchPlugin.MOD_ID;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import pelep.unlittorch.block.BlockTorch;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.entity.TileEntityTorch;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PacketReceiver implements IPacketHandler
{
    @Override
    public void onPacketData(INetworkManager nm, Packet250CustomPayload pkt, Player player)
    {
        if (pkt.channel.equals(MOD_ID))
        {
            DataInputStream data = new DataInputStream(new ByteArrayInputStream(pkt.data));
            
            byte type = -1;
            
            try
            {
                type = data.readByte();
                
                switch (type)
                {
                case 0:
                    LogHandler.fine("Handling packet (%d)", type);
                    this.handleConfigPacket(data);
                    break;
                case 1: case 2: case 3: case 4:
                    LogHandler.fine("Handling packet (%d)", type);
                    this.handleConfigPacket(type, data);
                    break;
                case 5:
                    this.handleAgePacket(data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt());
                    return;
                case 6: case 7:
                    this.handleEntityPacket(type, data.readInt(), data.readInt());
                    return;
                case 8:
                    this.handleInvUpdatePacket(data.readByte(), data.readByte());
                    return;
                default:
                    LogHandler.severe("Packet type unrecognized");
                    return;
                }
                
                LogHandler.fine("Done with packet (%d)", type);
            }
            catch (Exception e)
            {
                LogHandler.severe("Failed to handle packet (%d)", type);
            }
        }
    }
    
    private void handleConfigPacket(DataInputStream data) throws IOException
    {
        ConfigCommon.torchLifespanMax = data.readShort();
        ConfigCommon.lanternLifespanMax = data.readShort();
        ConfigCommon.lanternFuelFat = data.readShort();
        ConfigCommon.lanternFuelOil = data.readShort();
        
        byte b = data.readByte();
        
        ConfigCommon.torchIsSimple = (b & (1 << 0)) == (1 << 0);
        ConfigCommon.lanternIsSimple = (b & (1 << 1)) == (1 << 1);
        ConfigCommon.recipeDisableEmptyTinderbox = (b & (1 << 2)) == (1 << 2);
        ConfigCommon.recipeDisableLanternHandle = (b & (1 << 3)) == (1 << 3);
        ConfigCommon.recipeOverrideTorches = (b & (1 << 4)) == (1 << 4);
        ConfigCommon.torchSingleUse = (b & (1 << 5)) == (1 << 5);
    }
    
    private void handleConfigPacket(byte type, DataInputStream data) throws IOException
    {
        String string = "";
        int size = data.readShort();
        
        for (int i = 0; i < size; i++)
        {
            string += data.readChar();
        }
        
        if (!string.equals("") && string.length() > 0)
        {
            IgnitersHandler.syncIgnitersOrTinder(type, string);
        }
    }

    private void handleAgePacket(int age, int x, int y, int z, int dim)
    {
        World world = Minecraft.getMinecraft().theWorld;
        
        if (world.provider.dimensionId == dim && world.getChunkFromBlockCoords(x, z).isChunkLoaded)
        {
            TileEntityTorch te = BlockTorch.getTileEntityTorch(world, x, y, z);
            
            if (te != null)
            {
                te.setAge(age);
            }
        }
    }

    private void handleEntityPacket(byte type, int id, int dim)
    {
        World world = Minecraft.getMinecraft().theWorld;
        
        if (world.provider.dimensionId == dim)
        {
            Entity e = world.getEntityByID(id);
            
            if (e != null && e instanceof EntityItem)
            {
                EntityItem ei = (EntityItem) e;

                if (type == 6 && ei.getEntityItem().itemID == 50)
                {
                    ei.getEntityItem().setItemDamage(0);
                }
                else if (type == 7 && ei.getEntityItem().itemID == ConfigCommon.blockIdLanternLit)
                {
                    ei.getEntityItem().itemID = ConfigCommon.blockIdLanternUnlit;
                }
            }
        }
    }
    
    private void handleInvUpdatePacket(byte item, byte slot)
    {
        EntityPlayer p = Minecraft.getMinecraft().thePlayer;
        
        if (item == 0)
        {
            p.swingItem();
            p.inventory.addItemStackToInventory(new ItemStack(50, 1, 1));
        }
        else if (item == 1)
        {
            ItemStack bottle = new ItemStack(Item.glassBottle, 1, 0);
            
            if (slot == -1)
            {
                p.inventory.addItemStackToInventory(bottle);
            }
            else
            {
                p.inventory.setInventorySlotContents(slot, bottle);
            }
        }
    }
}
