package pelep.unlittorch.tileentity;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.packet.Packet04BurnFX;

/**
 * @author pelep
 */
public class TileEntityTorch extends TileEntity
{
    private boolean lit;
    private int age = 0;
    private Chunk chunk;

    public TileEntityTorch() {}

    public TileEntityTorch(boolean lit)
    {
        this.lit = lit;
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
        return this.lit && ConfigCommon.torchUpdates;
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
                this.killTorch("fire.fire", 1F);
            }

            return;
        }

        if (!this.worldObj.isRemote)
        {
            if (this.worldObj.canLightningStrikeAt(this.xCoord, this.yCoord, this.zCoord) && this.worldObj.rand.nextInt(30) == 0)
            {
                this.killTorch("random.fizz", 0.3F);
                return;
            }

            if (this.age > ConfigCommon.torchLifespanMin && ConfigCommon.torchRandomKillChance > 0 && this.worldObj.rand.nextInt(ConfigCommon.torchRandomKillChance) == 0)
            {
                if (this.worldObj.rand.nextInt(100) < ConfigCommon.torchDestroyChance)
                {
                    this.destroyTorch();
                }
                else
                {
                    this.killTorch("fire.fire", 1F);
                }

                return;
            }
        }

        if (worldObj.getTotalWorldTime() % 3 == 0)
        {
            this.age++;
            if (this.chunk == null) this.chunk = this.worldObj.getChunkFromBlockCoords(this.xCoord, this.zCoord);
            this.chunk.setChunkModified();
        }
    }

    private void destroyTorch()
    {
        this.invalidate();
//        int md = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
//        this.worldObj.playAuxSFX(2001, this.xCoord, this.yCoord, this.zCoord, 50 + (md << 12));
        Packet04BurnFX pkt = new Packet04BurnFX(this.xCoord, this.yCoord, this.zCoord, this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord));
        PacketDispatcher.sendPacketToAllAround(this.xCoord, this.yCoord, this.zCoord, 64D, this.worldObj.provider.dimensionId, pkt.create());
        this.worldObj.playSoundEffect(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5, "fire.fire", 1F, this.worldObj.rand.nextFloat() * 0.4F + 0.8F);
        this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
    }

    private void killTorch(String sound, float volume)
    {
        int md = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, ConfigCommon.blockIdTorchUnlit, md, 1|2);
        this.worldObj.playSoundEffect(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5, sound, volume, this.worldObj.rand.nextFloat() * 0.4F + 0.8F);
        ((TileEntityTorch)this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord)).setAge(this.age);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        this.age = tag.getInteger("age");
        this.lit = tag.getBoolean("lit");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setInteger("age", this.age);
        tag.setBoolean("lit", this.lit);
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("age", this.age);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
    {
        switch (pkt.actionType)
        {
            case 0:
                this.age = pkt.data.getInteger("age");
        }
    }
}
