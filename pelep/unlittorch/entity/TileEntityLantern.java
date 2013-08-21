package pelep.unlittorch.entity;

import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.TickHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class TileEntityLantern extends TileEntity
{
    private boolean lit = false;
    private boolean handle = false;
    private int age = 0;
    
    public TileEntityLantern() {}
    
    public TileEntityLantern(boolean lit, int age)
    {
        this.lit = lit;
        this.age = age;
    }
    
    public void setAge(int age)
    {
        this.age = Math.max(age, 0);
    }
    
    public int getAge()
    {
        return this.age;
    }
    
    public void setHandle(boolean handle)
    {
        this.handle = handle;
    }
    
    public boolean hasHandle()
    {
        return (this.handle || this.getBlockMetadata() < 8);
    }
    
    @Override
    public boolean canUpdate()
    {
        return this.lit && !ConfigCommon.lanternIsSimple;
    }
    
    @Override
    public void updateEntity()
    {
        if (this.age >= ConfigCommon.lanternLifespanMax)
        {
            this.killLantern();
        }
        
        if (TickHandler.updateAge == 0)
        {
            this.age++;
        }
    }
    
    private void killLantern()
    {
        this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, ConfigCommon.blockIdLanternUnlit, this.getBlockMetadata(), 3);
        this.worldObj.playSoundEffect(this.xCoord + 0.5, this.xCoord + 0.5, this.xCoord + 0.5, "fire.fire", 1F, this.worldObj.rand.nextFloat() * 0.4F + 1.5F);
        ((TileEntityLantern)this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord)).setAge(ConfigCommon.lanternLifespanMax);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        this.age = tag.getInteger("age");
        this.lit = tag.getBoolean("lit");
        this.handle = tag.getBoolean("handle");
    }
    
    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setInteger("age", this.age);
        tag.setBoolean("lit", this.lit);
        tag.setBoolean("handle", this.handle);
    }
    
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 0, tag);
    }
    
    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
    {
        this.readFromNBT(pkt.customParam1);
    }
}
