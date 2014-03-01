package pelep.unlittorch.item;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import pelep.pcl.IUpdatingItem;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.packet.Packet02UpdateEntity;

import java.util.List;

/**
 * @author pelep
 */
public class ItemTorchLit extends ItemTorch implements IUpdatingItem
{
    public ItemTorchLit(int id)
    {
        super(id);
        this.setMaxStackSize(1);
        this.setUnlocalizedName("unlittorch:torch_lit");
        this.setTextureName("torch_on");
    }


    //--------------------------------register---------------------------------//


    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(int id, CreativeTabs ct, List list)
    {
        list.add(new ItemStack(id, 1, 0));
        list.add(new ItemStack(ConfigCommon.blockIdTorchUnlit, 1, 0));
    }


    //---------------------------------itemuse---------------------------------//


    @Override
    public boolean onItemUse(ItemStack ist, EntityPlayer p, World world, int x, int y, int z, int side, float i, float j, float k)
    {
        if (!p.isSneaking())
        {
            if (rayTraceFromPlayer(world, p) == Material.water)
            {
                return false;
            }
            else if (world.getBlockId(x, y, z) == this.itemID)
            {
                return false;
            }
        }

        return super.onItemUse(ist, p, world, x, y, z, side, i, j, k);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer p)
    {
        if (!p.isSneaking() && rayTraceFromPlayer(world, p) == Material.water)
        {
            world.playSoundAtEntity(p, "random.fizz", 0.5F, itemRand.nextFloat() * 0.4F + 0.8F);
            p.swingItem();
            return new ItemStack(ConfigCommon.blockIdTorchUnlit, ist.stackSize, ist.getItemDamage());
        }

        return ist;
    }


    //---------------------------------update---------------------------------//


    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int slot, boolean held)
    {
        EntityPlayer p = (EntityPlayer) e;

        if (!p.capabilities.isCreativeMode && ConfigCommon.torchUpdates)
        {
            if (p.isInsideOfMaterial(Material.water))
            {
                if (!world.isRemote)
                {
                    world.playSoundAtEntity(p, "random.fizz", 0.8F, 1F);
                }

                for (int i = slot; i < p.inventory.mainInventory.length; i++)
                {
                    ItemStack inv = p.inventory.mainInventory[i];

                    if (inv != null && inv.itemID == this.itemID)
                    {
                        inv.itemID = ConfigCommon.blockIdTorchUnlit;
                    }
                }

                return;
            }
            else
            {
                int d = ist.getItemDamage();

                if (d >= ConfigCommon.torchLifespanMax)
                {
                    if (ConfigCommon.torchSingleUse)
                    {
                        destroyItemTorch(world, p, slot);
                    }
                    else
                    {
                        killItemTorch(world, p, ist, "fire.fire", 1F);
                    }

                    return;
                }

                if (!world.isRemote && d > ConfigCommon.torchLifespanMin && ConfigCommon.torchRandomKillChance > 0 && itemRand.nextInt(ConfigCommon.torchRandomKillChance) == 0)
                {
                    if (itemRand.nextInt(100) < ConfigCommon.torchDestroyChance)
                    {
                        destroyItemTorch(world, p, slot);
                    }
                    else
                    {
                        killItemTorch(world, p, ist, "fire.fire", 1F);
                    }

                    return;
                }

                int x = MathHelper.floor_double(p.posX);
                int y = MathHelper.floor_double(p.posY);
                int z = MathHelper.floor_double(p.posZ);

                if (!world.isRemote && world.canLightningStrikeAt(x, y, z) && ((held && itemRand.nextInt(50) == 0) || itemRand.nextInt(80) == 0))
                {
                    killItemTorch(world, p, ist, "random.fizz", 0.3F);
                    return;
                }

                if (world.getTotalWorldTime() % 3 == 0)
                {
                    int add = held ? 2 : 1;
                    ist.setItemDamage(d + add);
                }
            }
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem ei)
    {
        if (!ei.worldObj.isRemote && ConfigCommon.torchUpdates)
        {
            ItemStack ist = ei.getEntityItem();

            if (ist.itemID == this.itemID)
            {
                if (ei.handleWaterMovement())
                {
                    killEntityTorch(ei, "random.fizz", 0.3F);
                    return false;
                }

                int d = ist.getItemDamage();

                if (d >= ConfigCommon.torchLifespanMax)
                {
                    if (ConfigCommon.torchSingleUse)
                    {
                        destroyEntityTorch(ei);
                    }
                    else
                    {
                        killEntityTorch(ei, "fire.fire", 1F);
                    }

                    return false;
                }

                if (d > ConfigCommon.torchLifespanMin && ConfigCommon.torchRandomKillChance > 0 && itemRand.nextInt(ConfigCommon.torchRandomKillChance) == 0)
                {
                    if (itemRand.nextInt(100) < ConfigCommon.torchDestroyChance)
                    {
                        destroyEntityTorch(ei);
                    }
                    else
                    {
                        killEntityTorch(ei, "fire.fire", 1F);
                    }

                    return false;
                }

                int x = MathHelper.floor_double(ei.posX);
                int y = MathHelper.floor_double(ei.posY);
                int z = MathHelper.floor_double(ei.posZ);

                if (ei.worldObj.canLightningStrikeAt(x, y, z) && itemRand.nextInt(30) == 0)
                {
                    killEntityTorch(ei, "random.fizz", 0.3F);
                    return false;
                }

                if (ei.worldObj.getTotalWorldTime() % 3 == 0)
                {
                    ist.setItemDamage(d + 1);
                }
            }
        }

        return false;
    }


    //----------------------------------mine----------------------------------//


    private static void destroyItemTorch(World world, EntityPlayer p, int slot)
    {
        world.playSoundAtEntity(p, "fire.fire", 1F, world.rand.nextFloat() * 0.4F + 0.8F);
        p.inventory.setInventorySlotContents(slot, null);
    }

    private static void killItemTorch(World world, EntityPlayer p, ItemStack ist, String sound, float volume)
    {
        world.playSoundAtEntity(p, sound, volume, world.rand.nextFloat() * 0.4F + 0.8F);
        ist.itemID = ConfigCommon.blockIdTorchUnlit;
    }

    private static void destroyEntityTorch(EntityItem ei)
    {
        ei.worldObj.playSoundEffect(ei.posX, ei.posY, ei.posZ, "fire.fire", 1F, ei.worldObj.rand.nextFloat() * 0.4F + 0.8F);
        ei.setDead();
    }

    private static void killEntityTorch(EntityItem ei, String sound, float volume)
    {
        ei.worldObj.playSoundEffect(ei.posX, ei.posY, ei.posZ, sound, volume, ei.worldObj.rand.nextFloat() * 0.4F + 0.8F);
        ei.getEntityItem().itemID = ConfigCommon.blockIdTorchUnlit;
        PacketDispatcher.sendPacketToAllInDimension(new Packet02UpdateEntity(ei).create(), ei.worldObj.provider.dimensionId);
    }
}
