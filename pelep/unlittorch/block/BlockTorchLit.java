package pelep.unlittorch.block;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.IgnitersHandler;
import pelep.unlittorch.packet.Packet03UpdateTile;
import pelep.unlittorch.tileentity.TileEntityTorch;

/**
 * @author pelep
 */
import java.util.ArrayList;
import java.util.Random;

public class BlockTorchLit extends BlockTorch implements ITileEntityProvider
{
    public BlockTorchLit()
    {
        super(50);
        this.setLightValue(0.9375F);
        this.setUnlocalizedName("unlittorch:torch_lit");
        this.setTextureName("torch_on");
        this.isBlockContainer = true;
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


    //--------------------------------container-------------------------------//


    @Override
    public boolean hasTileEntity(int md)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int md)
    {
        return new TileEntityTorch();
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityTorch();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int id, int md)
    {
        super.breakBlock(world, x, y, z, id, md);
        world.removeBlockTileEntity(x, y, z);
    }


    //----------------------------------drop----------------------------------//


    @Override
    public boolean removeBlockByPlayer(World world, EntityPlayer p, int x, int y, int z)
    {
        int age = ((TileEntityTorch)world.getBlockTileEntity(x, y, z)).getAge();
        boolean drop = world.setBlockToAir(x, y, z);

        if (drop && !world.isRemote && !ConfigCommon.torchDropsUnlit && !p.capabilities.isCreativeMode)
        {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(this.blockID, 1, age));
        }

        return drop;
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int md, int fortune)
    {
        ArrayList<ItemStack> stacks = new ArrayList();

        if (ConfigCommon.torchDropsUnlit)
        {
            stacks.add(new ItemStack(ConfigCommon.blockIdTorchUnlit, 1, 0));
        }
        else
        {
            TileEntity te = world.getBlockTileEntity(x, y, z);

            if (te != null)
            {
                stacks.add(new ItemStack(this.blockID, 1, ((TileEntityTorch)te).getAge()));
            }
        }

        return stacks;
    }


    //---------------------------------place----------------------------------//


    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase p, ItemStack ist)
    {
        setTileEntityAge(ist.getItemDamage(), world, x, y, z, null);
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
        int d = ist.getItemDamage();

        if (IgnitersHandler.canIgniteSetTorch(id, d) || (id == ConfigCommon.blockIdTorchUnlit && IgnitersHandler.canIgniteHeldTorch(50, world.getBlockMetadata(x, y, z))))
        {
            if (id == this.blockID)
            {
                renewTorches(world, p, ist, x, y, z);
            }
            else if (id == ConfigCommon.blockIdTorchUnlit)
            {
                igniteTorch(world, ist, p, ((TileEntityTorch) world.getBlockTileEntity(x, y, z)).getAge());
            }
            else if (id == Item.flint.itemID)
            {
                setTileEntityAge(0, world, x, y, z, "fire.ignite");

                if (!p.capabilities.isCreativeMode)
                {
                    p.inventory.decrStackSize(p.inventory.currentItem, 1);
                }
            }
            else if (id == Item.flintAndSteel.itemID)
            {
                setTileEntityAge(0, world, x, y, z, "fire.ignite");
                ist.damageItem(1, p);
            }
            else if (id == Item.bucketLava.itemID)
            {
                setTileEntityAge(0, world, x, y, z, "fire.fire");
            }
            else
            {
                setTileEntityAge(0, world, x, y, z, "fire.fire");

                if (!p.capabilities.isCreativeMode)
                {
                    if (Item.itemsList[id].isDamageable())
                    {
                        ist.damageItem(1, p);
                    }
                    else
                    {
                        p.inventory.decrStackSize(p.inventory.currentItem, 1);
                    }
                }
            }

            return true;
        }
        else if (id == Item.bucketMilk.itemID || id == Item.bucketWater.itemID)
        {
            killBlockTorch(world, x, y, z, "random.fizz", 0.5F);
            return true;
        }
        else if (id == Block.cloth.blockID)
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
        if (te != null) te.invalidate();

        world.setBlock(x, y, z, ConfigCommon.blockIdTorchUnlit, world.getBlockMetadata(x, y, z), 2);
        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, sound, volume, world.rand.nextFloat() * 0.4F + 0.8F);
    }

    private static void renewTorches(World world, EntityPlayer p, ItemStack ist, int x, int y, int z)
    {
        String sound = "fire.fire";
        TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);

        int ta = te.getAge();
        int ia = ist.getItemDamage();

        if (ta == ia)
        {
            return;
        }
        else if (ta < ia)
        {
            ist.setItemDamage(ta);
        }
        else
        {
            te.setAge(ia);
            if (!world.isRemote)
            {
                int dim = world.provider.dimensionId;
                PacketDispatcher.sendPacketToAllInDimension(new Packet03UpdateTile(x, y, z, dim, ia).create(), dim);
            }
        }

        p.swingItem();
        world.playSoundAtEntity(p, sound, 1F, world.rand.nextFloat() * 0.4F + 0.8F);
    }

    private static void igniteTorch(World world, ItemStack ist, EntityPlayer p, int age)
    {
        if (ist.stackSize > 1)
        {
            ItemStack torch = new ItemStack(50, 1, age);
            ist.stackSize--;

            if (!p.inventory.addItemStackToInventory(torch))
            {
                p.dropPlayerItemWithRandomChoice(torch, false).delayBeforeCanPickup = 10;
            }
        }
        else
        {
            ist.itemID = 50;
            ist.setItemDamage(age);
        }

        world.playSoundAtEntity(p, "fire.fire", 1F, world.rand.nextFloat() * 0.4F + 0.8F);
    }
}
