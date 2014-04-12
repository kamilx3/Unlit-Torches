package pelep.unlittorch.block;

import net.minecraft.block.Block;
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
        setLightValue(0F);
        setUnlocalizedName("unlittorch:torch_unlit");
        setTextureName("unlittorch:torch_off");
        instance = this;
    }


    //-------------------------------interact--------------------------------//


    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e)
    {
        if (!world.isRemote && (e.isBurning() || e instanceof EntityBlaze || e instanceof EntityMagmaCube || e instanceof EntityFireball))
            igniteBlock(world, x, y, z, "fire.fire");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int side, float i, float j, float k)
    {
        ItemStack ist = p.inventory.getCurrentItem();

        if (p.isSneaking())
        {
            if (ist != null) return false;

            TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
            ItemStack torch = new ItemStack(blockID, 1, te.age);
            torch.setTagCompound(te.eternal ? new NBTTagCompound() : null);
            p.inventory.setInventorySlotContents(p.inventory.currentItem, torch);
            world.setBlockToAir(x, y, z);

            return true;
        }
        else if (ist != null)
        {
            int id = ist.itemID;
            int d = ist.getItemDamage();

            if (id == ConfigCommon.blockIdTorchLit || id == Block.torchWood.blockID)
            {
                igniteBlock(world, x, y, z, "fire.fire");
                return true;
            }
            else if (IgnitersHandler.canIgniteSetTorch(id, d))
            {
                if (id == Item.bucketLava.itemID)
                {
                    igniteBlock(world, x, y, z, "fire.fire");
                }
                else if (id == Item.flint.itemID)
                {
                    igniteBlock(world, x, y, z, "fire.ignite");
                    if (!p.capabilities.isCreativeMode) p.inventory.decrStackSize(p.inventory.currentItem, 1);
                }
                else if (id == Item.flintAndSteel.itemID)
                {
                    igniteBlock(world, x, y, z, "fire.ignite");
                    ist.damageItem(1, p);
                }
                else
                {
                    igniteBlock(world, x, y, z, "fire.fire");

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
        }

        if (useIgniter(p))
        {
            igniteBlock(world, x, y, z, "fire.fire");
            return true;
        }

        return false;
    }


    //----------------------------------util----------------------------------//


    public static void igniteBlock(World world, int x, int y, int z, String sound)
    {
        TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
        int age = te.age;
        boolean eternal = te.eternal;

        world.setBlock(x, y, z, ConfigCommon.blockIdTorchLit, world.getBlockMetadata(x, y, z), 1|2);
        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, sound, 1F, world.rand.nextFloat() * 0.4F + 0.8F);

        te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
        te.age = age;
        te.eternal = eternal;
    }

    public static boolean useIgniter(EntityPlayer ep)
    {
        int slot = -1;

        for (int i = 0; i < ep.inventory.mainInventory.length; i++)
        {
            ItemStack ist = ep.inventory.mainInventory[i];

            if (ist != null)
            {
                if (ist.itemID == ConfigCommon.blockIdTorchLit || ist.itemID == Block.torchWood.blockID)
                {
                    slot = i;
                    break;
                }
                else if (IgnitersHandler.canIgniteSetTorch(ist.itemID, ist.getItemDamage()))
                {
                    slot = i;
                    if (ist.itemID == Item.bucketLava.itemID) break;
                }
            }
        }

        if (slot == -1) return false;

        ItemStack ist = ep.inventory.mainInventory[slot];
        int id = ist.itemID;

        if (!ep.capabilities.isCreativeMode &&
            id != Item.bucketLava.itemID &&
            id != ConfigCommon.blockIdTorchLit &&
            id != Block.torchWood.blockID)
        {
            if (Item.itemsList[id].isDamageable())
            {
                ist.damageItem(1, ep);
            }
            else
            {
                ep.inventory.decrStackSize(slot, 1);
            }
        }

        return true;
    }
}
