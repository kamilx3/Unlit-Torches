package pelep.unlittorch.block;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.IgnitersHandler;
import pelep.unlittorch.packet.Packet03UpdateTile;
import pelep.unlittorch.tileentity.TileEntityTorch;

import java.util.Random;

/**
 * @author pelep
 */
public class BlockTorchLit extends BlockTorch
{
    public BlockTorchLit()
    {
        super(ConfigCommon.blockIdTorchLit, true);
        this.setLightValue(0.9375F);
        this.setUnlocalizedName("unlittorch:torch_lit");
        this.setTextureName("torch_on");
    }


    //--------------------------------rendering-------------------------------//


    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand)
    {
        double fx = x + 0.5D;
        double fy = y + 0.7D;
        double fz = z + 0.5D;

        double hos = 0.2199999988079071D;
        double dos = 0.27000001072883606D;

        switch (world.getBlockMetadata(x, y, z))
        {
            case 1:
                world.spawnParticle("smoke", fx - dos, fy + hos, fz, 0D, 0D, 0D);
                world.spawnParticle("flame", fx - dos, fy + hos, fz, 0D, 0D, 0D);
                break;
            case 2:
                world.spawnParticle("smoke", fx + dos, fy + hos, fz, 0D, 0D, 0D);
                world.spawnParticle("flame", fx + dos, fy + hos, fz, 0D, 0D, 0D);
                break;
            case 3:
                world.spawnParticle("smoke", fx, fy + hos, fz - dos, 0D, 0D, 0D);
                world.spawnParticle("flame", fx, fy + hos, fz - dos, 0D, 0D, 0D);
                break;
            case 4:
                world.spawnParticle("smoke", fx, fy + hos, fz + dos, 0D, 0D, 0D);
                world.spawnParticle("flame", fx, fy + hos, fz + dos, 0D, 0D, 0D);
                break;
            default:
                world.spawnParticle("smoke", fx, fy, fz, 0D, 0D, 0D);
                world.spawnParticle("flame", fx, fy, fz, 0D, 0D, 0D);
        }
    }


    //-------------------------------interact--------------------------------//


    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e)
    {
        if (!world.isRemote && !e.onGround && e instanceof EntityArrow && (Math.abs(e.motionX) > 1 || Math.abs(e.motionY) > 1 || Math.abs(e.motionZ) > 1))
        {
            if (e.isBurning())
            {
                TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
                int age = te.getAge() - (ConfigCommon.torchLifespanMax / 3);
                setTileEntityAge(Math.max(age, 0), world, x, y, z, "fire.fire");
            }
            else
            {
                killBlockTorch(world, x, y, z, "fire.fire", 1F);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int side, float i, float j, float k)
    {
        if (p.isSneaking()) return false;

        ItemStack ist = p.inventory.getCurrentItem();

        if (ist == null)
        {
            TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
            p.inventory.setInventorySlotContents(p.inventory.currentItem, new ItemStack(this.blockID, 1, te.getAge()));
            world.setBlockToAir(x, y, z);
            return true;
        }

        int id = ist.itemID;

        if (id == this.blockID)
        {
            renewTorches(world, p, ist, x, y, z);
            return true;
        }
        else if (id == ConfigCommon.blockIdTorchUnlit && IgnitersHandler.canIgniteHeldTorch(this.blockID, world.getBlockMetadata(x, y, z)))
        {
            igniteHeldTorch(world, ist, p);
            return true;
        }
        else if (id == ConfigCommon.itemIdCloth)
        {
            if (ist.getItemDamage() == 0)
            {
                killBlockTorch(world, x, y, z, "fire.fire", 1F);
                if (!p.capabilities.isCreativeMode) p.inventory.decrStackSize(p.inventory.currentItem, 1);
            }
            else
            {
                killBlockTorch(world, x, y, z, "random.fizz", 0.3F);
            }

            return true;
        }
        else if (id == Item.bucketMilk.itemID || id == Item.bucketWater.itemID)
        {
            killBlockTorch(world, x, y, z, "random.fizz", 0.3F);
            return true;
        }
        else if (id == Block.cloth.blockID || id == Block.carpet.blockID)
        {
            killBlockTorch(world, x, y, z, "fire.fire", 1F);

            if (!p.capabilities.isCreativeMode)
            {
                p.inventory.decrStackSize(p.inventory.currentItem, 1);
            }

            return true;
        }
        else if (id == Item.gunpowder.itemID)
        {
            int size = ist.stackSize;
            float strength = size < 5 ? (size * 0.2F) : 1F;

            world.newExplosion(p, x, y, z, strength, size > 5, true);

            if (!p.capabilities.isCreativeMode)
            {
                p.inventory.decrStackSize(p.inventory.currentItem, 5);
            }

            return true;
        }

        return false;
    }


    //----------------------------------mine----------------------------------//


    public static void killBlockTorch(World world, int x, int y, int z, String sound, float volume)
    {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        int age = te != null ? ((TileEntityTorch)te).getAge() : 0;

        world.setBlock(x, y, z, ConfigCommon.blockIdTorchUnlit, world.getBlockMetadata(x, y, z), 1|2);
        setTileEntityAge(age, world, x, y, z, sound, volume);
    }

    private static void renewTorches(World world, EntityPlayer p, ItemStack ist, int x, int y, int z)
    {
        TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
        int ta = te.getAge();
        int ia = ist.getItemDamage();

        if (ta == ia)
        {
            return;
        }

        double d = (ta + ia) / 2;
        int age = MathHelper.ceiling_double_int(d);

        if (!world.isRemote)
        {
            int dim = world.provider.dimensionId;
            PacketDispatcher.sendPacketToAllInDimension(new Packet03UpdateTile(x, y, z, dim, age).create(), dim);
        }

        ist.setItemDamage(age);
        setTileEntityAge(age, world, x, y, z, "fire.fire");
        p.swingItem();
    }

    public static void igniteHeldTorch(World world, ItemStack ist, EntityPlayer p)
    {
        if (ist.stackSize > 1)
        {
            ItemStack torch = new ItemStack(50, 1, ist.getItemDamage());
            ist.stackSize--;

            if (!p.inventory.addItemStackToInventory(torch))
            {
                p.dropPlayerItemWithRandomChoice(torch, false).delayBeforeCanPickup = 10;
            }
        }
        else
        {
            ist.itemID = ConfigCommon.blockIdTorchLit;
        }

        world.playSoundAtEntity(p, "fire.fire", 1F, world.rand.nextFloat() * 0.4F + 0.8F);
    }
}
