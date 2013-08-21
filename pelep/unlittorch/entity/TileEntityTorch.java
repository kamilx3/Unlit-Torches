package pelep.unlittorch.entity;

import pelep.unlittorch.block.BlockTorch;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.TickHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;

public class TileEntityTorch extends TileEntity
{
    private int age = 1;
    
    public TileEntityTorch() {}
    
    public TileEntityTorch(int age)
    {
        this.age = age;
    }
    
    public void setAge(int age)
    {
        this.age = age;
    }
    
    public int getAge()
    {
        return this.age;
    }

    @Override
    public boolean canUpdate()
    {
        return !ConfigCommon.torchIsSimple;
    }
    
    @Override
    public void updateEntity()
    {
        if (this.age >= ConfigCommon.torchLifespanMax)
        {
            if (ConfigCommon.torchSingleUse)
            {
                this.destroyTorch();
            }
            else
            {
                this.killTorch("fire.fire");
            }
            
            return;
        }
        
        if (!this.worldObj.isRemote)
        {
            if (this.worldObj.canLightningStrikeAt(this.xCoord, this.yCoord, this.zCoord) && this.worldObj.rand.nextInt(40) == 0)
            {
                this.killTorch("random.fizz");
                return;
            }
            
            if (this.age > ConfigCommon.torchLifespanMin && ConfigCommon.torchKillChance > 0 && this.worldObj.rand.nextInt(ConfigCommon.torchKillChance) == 0)
            {
                if (ConfigCommon.torchSingleUse)
                {
                    this.destroyTorch();
                }
                else
                {
                    this.killTorch("fire.fire");
                }
                
                return;
            }
        }
        
        if (TickHandler.updateAge == 0)
        {
            this.age++;
        }
    }
    
    private void destroyTorch()
    {
        int md = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        this.invalidate();
        this.worldObj.playAuxSFX(2001, this.xCoord, this.yCoord, this.zCoord, 50 + (md << 12));
        this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
    }
    
    private void killTorch(String sound)
    {
        this.invalidate();
        
        int md = BlockTorch.getTorchMetadata(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        if (md < 6) md += 5;
        
        this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, md, 3);
        this.worldObj.updateLightByType(EnumSkyBlock.Block, this.xCoord, this.yCoord, this.zCoord);
        this.worldObj.playSoundEffect(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5, sound, 1F, this.worldObj.rand.nextFloat() * 0.4F + 0.8F);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        this.age = tag.getInteger("age");
    }
    
    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setInteger("age", this.age);
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
