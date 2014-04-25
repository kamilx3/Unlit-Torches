package pelep.unlittorch.item;

import static pelep.unlittorch.config.ConfigCommon.*;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import pelep.pcl.IUpdatingItem;
import pelep.unlittorch.packet.Packet02UpdateEntity;

import java.util.List;

/**
 * @author pelep
 */
public class ItemTorchLit extends ItemTorch implements IUpdatingItem
{
    public static final int UPDATE_INTERVAL = 24;

    public ItemTorchLit(int id)
    {
        super(id);
        setMaxStackSize(1);
        setUnlocalizedName("unlittorch:torch_lit");
        setTextureName("torch_on");
    }


    //--------------------------------register---------------------------------//


    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    @Override
    public void getSubItems(int id, CreativeTabs ct, List list)
    {
        ItemStack ist = new ItemStack(id, 1, 0);
        ist.setTagCompound(new NBTTagCompound());
        list.add(ist);
        list.add(new ItemStack(id, 1, 0));
        list.add(new ItemStack(blockIdTorchUnlit, 1, 0));
    }


    //---------------------------------itemuse---------------------------------//


    @Override
    public boolean onItemUse(ItemStack ist, EntityPlayer p, World world, int x, int y, int z, int side, float i, float j, float k)
    {
        if (!p.isSneaking())
        {
            if (getMaterialClicked(world, p) == Material.water)
            {
                return false;
            }
            else if (world.getBlockId(x, y, z) == itemID)
            {
                return false;
            }
        }

        return super.onItemUse(ist, p, world, x, y, z, side, i, j, k);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer p)
    {
        if (!p.isSneaking() && getMaterialClicked(world, p) == Material.water)
        {
            world.playSoundAtEntity(p, "random.fizz", 0.5F, itemRand.nextFloat() * 0.4F + 0.8F);
            p.swingItem();
            ItemStack torch = new ItemStack(blockIdTorchUnlit, ist.stackSize, ist.getItemDamage());
            torch.setTagCompound(ist.getTagCompound());
            return torch;
        }

        return ist;
    }


    //---------------------------------update---------------------------------//


    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int slot, boolean held)
    {
        EntityPlayer p = (EntityPlayer) e;

        if (p.capabilities.isCreativeMode || ist.stackTagCompound != null || !torchUpdates || world.getTotalWorldTime() % UPDATE_INTERVAL != 0)
            return;

        if (p.isInsideOfMaterial(Material.water))
        {
            if (!world.isRemote)
            {
                world.playSoundAtEntity(p, "random.fizz", 0.8F, 1F);

                for (int i = slot; i < p.inventory.mainInventory.length; i++)
                {
                    ItemStack inv = p.inventory.mainInventory[i];
                    if (inv != null && inv.itemID == itemID)
                        inv.itemID = blockIdTorchUnlit;
                }
            }
        }
        else
        {
            int d = ist.getItemDamage();

            if (d >= torchLifespanMax)
            {
                if (torchSingleUse)
                {
                    destroyItem(world, p, slot);
                }
                else
                {
                    extinguishItem(world, p, ist, "fire.fire", 1F);
                }

                return;
            }

            if (!world.isRemote)
            {
                if (d > torchLifespanMin && torchRandomKillChance > 0 && itemRand.nextInt(torchRandomKillChance) == 0)
                {
                    if (itemRand.nextInt(100) < torchDestroyChance)
                    {
                        destroyItem(world, p, slot);
                    }
                    else
                    {
                        extinguishItem(world, p, ist, "fire.fire", 1F);
                    }

                    return;
                }

                int x = MathHelper.floor_double(p.posX);
                int y = MathHelper.floor_double(p.posY);
                int z = MathHelper.floor_double(p.posZ);

                if (world.canLightningStrikeAt(x, y, z) && ((held && itemRand.nextInt(2) == 0) || itemRand.nextInt(3) == 0))
                {
                    extinguishItem(world, p, ist, "random.fizz", 0.3F);
                    return;
                }
            }

            d += held ? 2 : 1;
            ist.setItemDamage(d);
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem ei)
    {
        if (ei.worldObj.isRemote || !torchUpdates || ei.worldObj.getTotalWorldTime() % UPDATE_INTERVAL != 0)
            return false;

        ItemStack ist = ei.getEntityItem();

        if (ist.itemID != itemID || ist.stackTagCompound != null)
            return false;

        if (ei.handleWaterMovement())
        {
            extinguishEntity(ei, "random.fizz", 0.3F);
            return false;
        }

        int d = ist.getItemDamage();

        if (d >= torchLifespanMax)
        {
            if (torchSingleUse)
            {
                destroyEntity(ei);
            }
            else
            {
                extinguishEntity(ei, "fire.fire", 1F);
            }

            return false;
        }

        if (d > torchLifespanMin && torchRandomKillChance > 0 && itemRand.nextInt(torchRandomKillChance) == 0)
        {
            if (itemRand.nextInt(100) < torchDestroyChance)
            {
                destroyEntity(ei);
            }
            else
            {
                extinguishEntity(ei, "fire.fire", 1F);
            }

            return false;
        }

        int x = MathHelper.floor_double(ei.posX);
        int y = MathHelper.floor_double(ei.posY);
        int z = MathHelper.floor_double(ei.posZ);

        if (ei.worldObj.canLightningStrikeAt(x, y, z) && itemRand.nextInt(3) == 0)
        {
            extinguishEntity(ei, "random.fizz", 0.3F);
            return false;
        }

        ist.setItemDamage(++d);

        return false;
    }


    //----------------------------------util----------------------------------//


    private static void destroyItem(World world, EntityPlayer p, int slot)
    {
        if (world.isRemote) return;
        world.playSoundAtEntity(p, "fire.fire", 1F, world.rand.nextFloat() * 0.4F + 0.8F);
        p.inventory.setInventorySlotContents(slot, null);
    }

    private static void extinguishItem(World world, EntityPlayer p, ItemStack ist, String sound, float volume)
    {
        if (world.isRemote) return;
        world.playSoundAtEntity(p, sound, volume, world.rand.nextFloat() * 0.4F + 0.8F);
        ist.itemID = blockIdTorchUnlit;
    }

    private static void destroyEntity(EntityItem ei)
    {
        ei.worldObj.playSoundEffect(ei.posX, ei.posY, ei.posZ, "fire.fire", 1F, ei.worldObj.rand.nextFloat() * 0.4F + 0.8F);
        ei.setDead();
    }

    private static void extinguishEntity(EntityItem ei, String sound, float volume)
    {
        ei.worldObj.playSoundEffect(ei.posX, ei.posY, ei.posZ, sound, volume, ei.worldObj.rand.nextFloat() * 0.4F + 0.8F);
        ei.getEntityItem().itemID = blockIdTorchUnlit;
        PacketDispatcher.sendPacketToAllInDimension(new Packet02UpdateEntity(ei).create(), ei.worldObj.provider.dimensionId);
    }
}
