package pelep.unlittorch.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.IgnitersHandler;
import pelep.unlittorch.tileentity.TileEntityTorch;

/**
 * @author pelep
 */
public class BlockTorchUnlit extends BlockTorch
{
    public static BlockTorchUnlit instance;

    public BlockTorchUnlit()
    {
        super(ConfigCommon.blockIdTorchUnlit, false);
        this.setLightValue(0F);
        this.setUnlocalizedName("unlittorch:torch_unlit");
        this.setTextureName("unlittorch:torch_off");
        instance = this;
    }


    //-------------------------------interact--------------------------------//


    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e)
    {
        if (!world.isRemote && (e.isBurning() || e instanceof EntityBlaze || e instanceof EntityMagmaCube || e instanceof EntityFireball))
        {
            TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
            igniteBlockTorch(te.isEternal(), te.getAge(), world, x, y, z, "fire.fire");
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
            ItemStack torch = new ItemStack(this.blockID, 1, te.getAge());
            torch.setTagCompound(te.isEternal() ? new NBTTagCompound() : null);
            p.inventory.setInventorySlotContents(p.inventory.currentItem, torch);
            world.setBlockToAir(x, y, z);
            return true;
        }

        int id = ist.itemID;
        int d = ist.getItemDamage();

        if (IgnitersHandler.canIgniteSetTorch(id, d))
        {
            TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
            int age = te.getAge();
            boolean eternal = te.isEternal();

            if (id == ConfigCommon.blockIdTorchLit)
            {
                igniteBlockTorch(eternal, age, world, x, y, z, "fire.fire");
            }
            else if (id == Item.flint.itemID)
            {
                igniteBlockTorch(eternal, age, world, x, y, z, "fire.ignite");

                if (!p.capabilities.isCreativeMode)
                {
                    p.inventory.decrStackSize(p.inventory.currentItem, 1);
                }
            }
            else if (id == Item.flintAndSteel.itemID)
            {
                igniteBlockTorch(eternal, age, world, x, y, z, "fire.ignite");
                ist.damageItem(1, p);
            }
            else if (id == Item.bucketLava.itemID)
            {
                igniteBlockTorch(eternal, age, world, x, y, z, "fire.fire");
            }
            else
            {
                igniteBlockTorch(eternal, age, world, x, y, z, "fire.fire");

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

        return false;
    }


    //----------------------------------mine----------------------------------//


    public static void igniteBlockTorch(boolean eternal, int age, World world, int x, int y, int z, String sound)
    {
        world.setBlock(x, y, z, ConfigCommon.blockIdTorchLit, world.getBlockMetadata(x, y, z), 1|2);
        TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
        te.setAge(age);
        te.setEternal(eternal);
        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, sound, 1F, world.rand.nextFloat() * 0.4F + 0.8F);
    }
}
