package pelep.unlittorch.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.IgnitersHandler;
import pelep.unlittorch.tileentity.TileEntityTorch;

/**
 * @author pelep
 */
public class BlockTorchUnlit extends BlockTorch
{
    public BlockTorchUnlit()
    {
        super(ConfigCommon.blockIdTorchUnlit, false);
        this.setLightValue(0F);
        this.setUnlocalizedName("unlittorch:torch_unlit");
        this.setTextureName("unlittorch:torch_off");
    }


    //-------------------------------interact--------------------------------//


    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e)
    {
        if (!world.isRemote && (e.isBurning() || e instanceof EntityBlaze || e instanceof EntityMagmaCube || e instanceof EntityFireball))
        {
            igniteBlockTorch((ConfigCommon.torchLifespanMax / 3), world, x, y, z, "fire.fire");
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

        if (IgnitersHandler.canIgniteSetTorch(id, d))
        {
            int age = ((TileEntityTorch)world.getBlockTileEntity(x, y, z)).getAge();

            if (id == 50)
            {
                igniteBlockTorch(age, world, x, y, z, "fire.fire");
            }
            else if (id == Item.flint.itemID)
            {
                igniteBlockTorch(age, world, x, y, z, "fire.ignite");

                if (!p.capabilities.isCreativeMode)
                {
                    p.inventory.decrStackSize(p.inventory.currentItem, 1);
                }
            }
            else if (id == Item.flintAndSteel.itemID)
            {
                igniteBlockTorch(age, world, x, y, z, "fire.ignite");
                ist.damageItem(1, p);
            }
            else if (id == Item.bucketLava.itemID)
            {
                igniteBlockTorch(age, world, x, y, z, "fire.fire");
            }
            else
            {
                igniteBlockTorch(age, world, x, y, z, "fire.fire");

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


    public static void igniteBlockTorch(int age, World world, int x, int y, int z, String sound)
    {
        world.setBlock(x, y, z, 50, world.getBlockMetadata(x, y, z), 2);
        setTileEntityAge(age, world, x, y, z, sound);
    }
}
