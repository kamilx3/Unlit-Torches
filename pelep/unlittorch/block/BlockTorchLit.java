package pelep.unlittorch.block;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.packet.Packet06UpdateInv;
import pelep.unlittorch.tileentity.TileEntityTorch;

import java.util.Random;

/**
 * @author pelep
 */
public class BlockTorchLit extends BlockTorch
{
    public static BlockTorchLit instance;

    public BlockTorchLit()
    {
        super(ConfigCommon.blockIdTorchLit, true);
        setLightValue(0.9375F);
        setUnlocalizedName("unlittorch:torch_lit");
        setTextureName("torch_on");
        instance = this;
    }


    //--------------------------------rendering-------------------------------//


    @Override
    public int getRenderType()
    {
        return 2;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand)
    {
        displayFlame(world, x, y, z, world.getBlockMetadata(x, y, z));
    }


    //-------------------------------interact--------------------------------//


    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e)
    {
        if (world.isRemote || e.onGround || !(e instanceof EntityArrow)) return;
        if (MathHelper.sqrt_double(e.motionX * e.motionX + e.motionY * e.motionY + e.motionZ * e.motionZ) > 1.2D)
            extinguishBlock(world, x, y, z, "fire.fire", 1F);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int side, float i, float j, float k)
    {
        ItemStack ist = p.inventory.getCurrentItem();

        if (p.isSneaking())
        {
            if (ist != null) return false;
            grabBlock(world, x, y, z, p);
            return true;
        }
        else if (ist != null)
        {
            int id = ist.itemID;

            if (id == blockID)
            {
                renewTorches(world, p, ist, x, y, z);
                return true;
            }
            else if (id == ConfigCommon.blockIdTorchUnlit)
            {
                igniteHeldTorch(world, ist, p);
                return true;
            }
            else if (id == ConfigCommon.itemIdCloth && ist.getItemDamage() == 0)
            {
                extinguishBlock(world, x, y, z, "fire.fire", 1F);
                consumeItem(p.inventory.currentItem, p, 1);
                return true;
            }
            else if (id == ConfigCommon.itemIdCloth && ist.getItemDamage() == 1)
            {
                extinguishBlock(world, x, y, z, "random.fizz", 0.3F);
                return true;
            }
            else if (id == Item.bucketMilk.itemID || id == Item.bucketWater.itemID)
            {
                extinguishBlock(world, x, y, z, "random.fizz", 0.3F);
                return true;
            }
            else if (id == Block.cloth.blockID || id == Block.carpet.blockID)
            {
                extinguishBlock(world, x, y, z, "fire.fire", 1F);
                consumeItem(p.inventory.currentItem, p, 1);
                return true;
            }
            else if (id == Item.gunpowder.itemID)
            {
                createExplosion(world, x, y, z, p, ist.stackSize);
                consumeItem(p.inventory.currentItem, p, 5);
                return true;
            }
        }

        return false;
    }


    //----------------------------------util----------------------------------//


    public static void extinguishBlock(World world, int x, int y, int z, String sound, float volume)
    {
        if (world.isRemote) return;

        TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
        int age = te.age;
        boolean eternal = te.eternal;

        world.setBlock(x, y, z, ConfigCommon.blockIdTorchUnlit, world.getBlockMetadata(x, y, z), 1|2);
        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, sound, volume, world.rand.nextFloat() * 0.4F + 0.8F);

        te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
        te.age = age;
        te.eternal = eternal;
    }

    private static void renewTorches(World world, EntityPlayer p, ItemStack ist, int x, int y, int z)
    {
        p.swingItem();
        if (world.isRemote) return;

        TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
        int ta = te.age;
        int ia = ist.getItemDamage();

        if (ta == ia) return;

        double d = (ta + ia) / 2;
        int age = MathHelper.ceiling_double_int(d);

        ist.setItemDamage(age);
        te.age = age;

        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "fire.fire", 1F, world.rand.nextFloat() * 0.4F + 0.8F);
        world.markBlockForUpdate(x, y, z);
        PacketDispatcher.sendPacketToPlayer(new Packet06UpdateInv(age).create(), (Player)p);
    }

    public static void createExplosion(World world, int x, int y, int z, EntityPlayer ep, int size)
    {
        if (world.isRemote) return;
        float str = size < 5 ? (size * 0.2F) : 1F;
        world.newExplosion(ep, x, y, z, str, size > 5, true);
    }

    public static void igniteHeldTorch(World world, ItemStack ist, EntityPlayer p)
    {
        if (world.isRemote) return;

        if (ist.stackSize > 1)
        {
            ItemStack torch = new ItemStack(ConfigCommon.blockIdTorchLit, 1, ist.getItemDamage());
            torch.setTagCompound(ist.getTagCompound());
            ist.stackSize--;
            if (!p.inventory.addItemStackToInventory(torch))
                p.dropPlayerItemWithRandomChoice(torch, false).delayBeforeCanPickup = 10;
        }
        else
        {
            ist.itemID = ConfigCommon.blockIdTorchLit;
        }

        world.playSoundAtEntity(p, "fire.fire", 1F, world.rand.nextFloat() * 0.4F + 0.8F);
    }

    @SideOnly(Side.CLIENT)
    public static void displayFlame(World world, double x, double y, double z, int md)
    {
        x += 0.5D;
        y += 0.7D;
        z += 0.5D;
        double vos = 0.2199999988079071D;
        double hos = 0.27000001072883606D;

        switch (md)
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
}
