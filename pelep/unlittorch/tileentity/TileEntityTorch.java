package pelep.unlittorch.tileentity;

import static pelep.unlittorch.config.ConfigCommon.*;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import pelep.unlittorch.item.ItemTorchLit;
import pelep.unlittorch.packet.Packet03BurnFX;

/**
 * @author pelep
 */
public class TileEntityTorch extends TileEntity
{
    private Chunk chunk;
    private boolean lit;
    public boolean eternal;
    public int age;

    public TileEntityTorch() {}

    public TileEntityTorch(boolean lit)
    {
        this.lit = lit;
    }

    @Override
    public boolean canUpdate()
    {
        return lit && !eternal && torchUpdates;
    }

    @Override
    public void updateEntity()
    {
        if (worldObj.getTotalWorldTime() % ItemTorchLit.UPDATE_INTERVAL != 0) return;

        if (eternal)
        {
            worldObj.markTileEntityForDespawn(this);
            return;
        }

        if (age >= torchLifespanMax)
        {
            if (torchSingleUse)
            {
                destroyTorch();
            }
            else
            {
                extinguishTorch("fire.fire", 1F);
            }

            return;
        }

        if (!worldObj.isRemote)
        {
            if (worldObj.canLightningStrikeAt(xCoord, yCoord, zCoord) && worldObj.rand.nextInt(10) == 0)
            {
                extinguishTorch("random.fizz", 0.3F);
                return;
            }

            if (age > torchLifespanMin && torchRandomKillChance > 0 && worldObj.rand.nextInt(torchRandomKillChance) == 0)
            {
                if (worldObj.rand.nextInt(100) < torchDestroyChance)
                {
                    destroyTorch();
                }
                else
                {
                    extinguishTorch("fire.fire", 1F);
                }

                return;
            }

            if (chunk == null) chunk = worldObj.getChunkFromBlockCoords(xCoord, zCoord);
            chunk.setChunkModified();
        }

        age++;
    }

    private void destroyTorch()
    {
        Packet03BurnFX pkt = new Packet03BurnFX(xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
        PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 64D, worldObj.provider.dimensionId, pkt.create());
        worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "fire.fire", 1F, worldObj.rand.nextFloat() * 0.4F + 0.8F);
        worldObj.setBlockToAir(xCoord, yCoord, zCoord);
    }

    private void extinguishTorch(String sound, float volume)
    {
        int md = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        worldObj.setBlock(xCoord, yCoord, zCoord, blockIdTorchUnlit, md, 1|2);
        worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, sound, volume, worldObj.rand.nextFloat() * 0.4F + 0.8F);
        ((TileEntityTorch)worldObj.getBlockTileEntity(xCoord, yCoord, zCoord)).age = age;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        age = tag.getInteger("age");
        lit = tag.getBoolean("lit");
        eternal = tag.getBoolean("eternal");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setInteger("age", age);
        tag.setBoolean("lit", lit);
        tag.setBoolean("eternal", eternal);
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("age", age);
        tag.setBoolean("eternal", eternal);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
    {
        switch (pkt.actionType)
        {
            case 0:
                eternal = pkt.data.getBoolean("eternal");
                age = pkt.data.getInteger("age");
        }
    }
}
