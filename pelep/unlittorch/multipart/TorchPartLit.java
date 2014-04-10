package pelep.unlittorch.multipart;

import static pelep.unlittorch.UnlitTorch.LOGGER;

import codechicken.lib.vec.BlockCoord;
import codechicken.multipart.IRandomDisplayTick;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import pelep.pcl.util.vec.Coordinate;
import pelep.unlittorch.block.BlockTorchLit;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.packet.Packet03BurnFX;
import pelep.unlittorch.packet.Packet05UpdatePart;

import java.util.Random;

/**
 * @author pelep
 */
public class TorchPartLit extends TorchPart implements IRandomDisplayTick
{
    private Chunk chunk;

    public TorchPartLit() {}

    public TorchPartLit(int md, int age, boolean eternal)
    {
        super(md, age, eternal);
    }

    @Override
    public Block getBlock()
    {
        return BlockTorchLit.instance;
    }

    @Override
    public String getType()
    {
        return "unlittorch:torch_lit";
    }

    @Override
    public void randomDisplayTick(Random rand)
    {
        World world = this.world();
        double x = this.x() + 0.5D;
        double y = this.y() + 0.7D;
        double z = this.z() + 0.5D;
        double vos = 0.2199999988079071D;
        double hos = 0.27000001072883606D;

        switch (this.meta)
        {
            case 1:
                world.spawnParticle("smoke", x - hos, y + vos, z, 0D, 0D, 0D);
                world.spawnParticle("flame", x - hos, y + vos, z, 0D, 0D, 0D);
                break;
            case 2:
                world.spawnParticle("smoke", x + hos, y + vos, z, 0D, 0D, 0D);
                world.spawnParticle("flame", x + hos, y + vos, z, 0D, 0D, 0D);
                break;
            case 3:
                world.spawnParticle("smoke", x, y + vos, z - hos, 0D, 0D, 0D);
                world.spawnParticle("flame", x, y + vos, z - hos, 0D, 0D, 0D);
                break;
            case 4:
                world.spawnParticle("smoke", x, y + vos, z + hos, 0D, 0D, 0D);
                world.spawnParticle("flame", x, y + vos, z + hos, 0D, 0D, 0D);
                break;
            default:
                world.spawnParticle("smoke", x, y, z, 0D, 0D, 0D);
                world.spawnParticle("flame", x, y, z, 0D, 0D, 0D);
        }
    }

    @Override
    public void onEntityCollision(Entity e)
    {
        if (this.world().isRemote || e.onGround || !(e instanceof EntityArrow)) return;
        if (MathHelper.sqrt_double(e.motionX * e.motionX + e.motionY * e.motionY + e.motionZ * e.motionZ) > 1.2D)
            this.extinguishPart("fire.fire", 1F);
    }

    @Override
    public boolean activate(EntityPlayer ep, MovingObjectPosition mop, ItemStack ist)
    {
        if (ep.isSneaking())
        {
            if (ist != null) return false;

            if (!this.world().isRemote)
            {
                ItemStack torch = new ItemStack(this.getBlockId(), 1, this.age);
                torch.setTagCompound(this.eternal ? new NBTTagCompound() : null);
                ep.inventory.setInventorySlotContents(ep.inventory.currentItem, torch);
                this.tile().remPart(this);
            }

            return true;
        }
        else if (ist != null)
        {
            int id = ist.itemID;

            if (id == this.getBlockId())
            {
                if (ep.swingProgress == 0F) //because multipart activates the block again too quickly
                    this.renewTorches(ep, ist);
                return true;
            }
            else if (id == ConfigCommon.blockIdTorchUnlit)
            {
                BlockTorchLit.igniteHeldTorch(this.world(), ist, ep);
                return true;
            }
            else if (id == ConfigCommon.itemIdCloth)
            {
                if (ist.getItemDamage() == 0)
                {
                    this.extinguishPart("fire.fire", 1F);
                    if (!ep.capabilities.isCreativeMode) ep.inventory.decrStackSize(ep.inventory.currentItem, 1);
                }
                else
                {
                    this.extinguishPart("random.fizz", 0.3F);
                }

                return true;
            }
            else if (id == Item.bucketMilk.itemID || id == Item.bucketWater.itemID)
            {
                this.extinguishPart("random.fizz", 0.3F);
                return true;
            }
            else if (id == Block.cloth.blockID || id == Block.carpet.blockID)
            {
                this.extinguishPart("fire.fire", 1F);
                if (!ep.capabilities.isCreativeMode) ep.inventory.decrStackSize(ep.inventory.currentItem, 1);
                return true;
            }
            else if (id == Item.gunpowder.itemID)
            {
                if (!ep.capabilities.isCreativeMode) ep.inventory.decrStackSize(ep.inventory.currentItem, 5);
                int size = ist.stackSize;
                float strength = size < 5 ? (size * 0.2F) : 1F;
                this.world().newExplosion(ep, this.x(), this.y(), this.z(), strength, size > 5, true);
                return true;
            }
        }

        return false;
    }

    @Override
    public void onConverted()
    {
        //because multipart is weird and isn't adding this automagically
        if (!this.world().isRemote && ConfigCommon.torchUpdates) this.world().addTileEntity(this.tile());
    }

    @Override
    public boolean doesTick()
    {
        return ConfigCommon.torchUpdates && !this.eternal;
    }

    @Override
    public void update()
    {
        if (this.world().getTotalWorldTime() % 3 != 0 || this.eternal || !ConfigCommon.torchUpdates) return;

        if (this.age >= ConfigCommon.torchLifespanMax)
        {
            if (ConfigCommon.torchSingleUse)
            {
                this.destroyPart();
            }
            else
            {
                this.extinguishPart("fire.fire", 1F);
            }

            return;
        }

        if (!this.world().isRemote)
        {
            if (this.isWet() && this.world().rand.nextInt(10) == 0)
            {
                this.extinguishPart("random.fizz", 0.3F);
                return;
            }

            if (this.age > ConfigCommon.torchLifespanMin && ConfigCommon.torchRandomKillChance > 0 && this.world().rand.nextInt(ConfigCommon.torchRandomKillChance) == 0)
            {
                if (this.world().rand.nextInt(100) < ConfigCommon.torchDestroyChance)
                {
                    this.destroyPart();
                }
                else
                {
                    this.extinguishPart("fire.fire", 1F);
                }

                return;
            }

            if (this.chunk == null) this.chunk = this.world().getChunkFromBlockCoords(this.x(), this.z());
            this.chunk.setChunkModified();
        }

        this.age++;
    }


    //----------------------------------util----------------------------------//


    private void extinguishPart(String sound, float volume)
    {
        if (this.world().isRemote) return;

        World world = this.world();
        int x = this.x();
        int y = this.y();
        int z = this.z();

        this.tile().remPart(this);
        TileMultipart.addPart(world, new BlockCoord(x, y, z), new TorchPartUnlit(this.meta, this.age, this.eternal));

        if (!"".equals(sound)) world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, sound, volume, world.rand.nextFloat() * 0.4F + 0.8F);
    }

    private void destroyPart()
    {
        if (this.world().isRemote) return;
        Packet03BurnFX pkt = new Packet03BurnFX(this.x(), this.y(), this.z(), this.meta);
        PacketDispatcher.sendPacketToAllAround(this.x(), this.y(), this.z(), 64D, this.world().provider.dimensionId, pkt.create());
        this.world().playSoundEffect(this.x() + 0.5, this.y() + 0.5, this.z() + 0.5, "fire.fire", 1F, this.world().rand.nextFloat() * 0.4F + 0.8F);
        this.tile().remPart(this);
    }

    private void renewTorches(EntityPlayer p, ItemStack ist)
    {
        int ia = ist.getItemDamage();
        if (this.age == ia) return;

        double d = (this.age + ia) / 2;
        int age = MathHelper.ceiling_double_int(d);
        ist.setItemDamage(age);
        p.swingItem();

        if (this.world().isRemote) return;

        this.age = age;
        this.world().playSoundEffect(this.x() + 0.5, this.y() + 0.5, this.z() + 0.5, "fire.fire", 1F, this.world().rand.nextFloat() * 0.4F + 0.8F);

        int dim = this.world().provider.dimensionId;
        int i = this.tile().jPartList().indexOf(this);
        PacketDispatcher.sendPacketToAllInDimension(new Packet05UpdatePart(i, this.x(), this.y(), this.z(), dim, this.age).create(), dim);
    }

    //replacement for world#canLightningStrikeAt(x, y, z)
    private boolean isWet()
    {
        if (!this.world().isRaining())
        {
            return false;
        }
        else if (!this.world().canBlockSeeTheSky(this.x(), this.y(), this.z()))
        {
            return false;
        }
        else if (this.world().getPrecipitationHeight(this.x(), this.z()) > this.y() + 1) //multipart sorta fucks this part up
        {
            return false;
        }

        return true;
    }

    public static void updatePart(World world, Coordinate coord, int index, int age)
    {
        TileEntity te = world.getBlockTileEntity(coord.x, coord.y, coord.z);

        if (te instanceof TileMultipart)
        {
            try
            {
                TileMultipart tm = (TileMultipart) te;
                TMultiPart part = tm.jPartList().get(index);

                if (part != null && "unlittorch:torch_lit".equals(part.getType()))
                    ((TorchPartLit)part).age = age;
            }
            catch (IndexOutOfBoundsException e)
            {
                LOGGER.warning("Index %d for TileMultipart at (%d,%d,%d) is out of bounds", index, coord.x, coord.y, coord.z);
            }
        }
    }
}
