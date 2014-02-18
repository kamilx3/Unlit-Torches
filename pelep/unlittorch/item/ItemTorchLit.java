package pelep.unlittorch.item;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import pelep.pcl.IUpdatingItem;
import pelep.pcl.helper.RayTraceHelper;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.IgnitersHandler;
import pelep.unlittorch.packet.Packet02UpdateEntity;

import java.util.List;

/**
 * @author pelep
 */
public class ItemTorchLit extends ItemBlock implements IUpdatingItem
{
    public ItemTorchLit(int id)
    {
        super(id);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHasSubtypes(true);
        this.setNoRepair();
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
            MovingObjectPosition mop = RayTraceHelper.rayTraceFromPlayer(world, p, false, true);

            if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE)
            {
                Material m = world.getBlockMaterial(mop.blockX, mop.blockY, mop.blockZ);

                if (m == Material.water || m == Material.lava || m == Material.fire)
                {
                    return false;
                }
            }

            int id = world.getBlockId(x, y, z);

            if (id == this.itemID)
            {
                return false;
            }
            else if (IgnitersHandler.canIgniteHeldTorch(id, world.getBlockMetadata(x, y, z)))
            {
                p.swingItem();
                world.playSoundAtEntity(p, "fire.fire", 1F, itemRand.nextFloat() * 0.4F + 0.8F);
                ist.setItemDamage(0);
                return true;
            }
        }

        return super.onItemUse(ist, p, world, x, y, z, side, i, j, k);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer p)
    {
        if (!p.isSneaking())
        {
            MovingObjectPosition mop = RayTraceHelper.rayTraceFromPlayer(world, p, false, true);

            if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE)
            {
                Material m = world.getBlockMaterial(mop.blockX, mop.blockY, mop.blockZ);

                if (m == Material.water)
                {
                    world.playSoundAtEntity(p, "random.fizz", 0.5F, itemRand.nextFloat() * 0.4F + 0.8F);
                    p.swingItem();
                    return new ItemStack(ConfigCommon.blockIdTorchUnlit, ist.stackSize, 0);
                }
                else if (m == Material.lava || m == Material.fire)
                {
                    world.playSoundAtEntity(p, "fire.fire", 1F, itemRand.nextFloat() * 0.4F + 0.8F);
                    p.swingItem();
                    ist.setItemDamage(0);
                    return ist;
                }
            }
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
            int d = ist.getItemDamage();

            if (p.handleLavaMovement() || p.isBurning())
            {
                if (d != 0 && !world.isRemote)
                {
                    world.playSoundAtEntity(p, "fire.fire", 1F, 1F);
                }

                for (int i = slot; i < p.inventory.mainInventory.length; i++)
                {
                    ItemStack inv = p.inventory.mainInventory[i];

                    if (inv != null)
                    {
                        if (inv.itemID == this.itemID)
                        {
                            inv.setItemDamage(0);
                        }
                        else if (inv.itemID == ConfigCommon.blockIdTorchUnlit)
                        {
                            inv.itemID = 50;
                        }
                    }
                }

                return;
            }
            else if (p.isInsideOfMaterial(Material.water))
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
                        inv.setItemDamage(0);
                    }
                }

                return;
            }
            else
            {
                if (d >= ConfigCommon.torchLifespanMax)
                {
                    if (ConfigCommon.torchSingleUse)
                    {
                        destroyItemTorch(world, p, slot);
                    }
                    else
                    {
                        killItemTorch(world, p, ist, "fire.fire");
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
                        killItemTorch(world, p, ist, "fire.fire");
                    }

                    return;
                }

                int x = MathHelper.floor_double(p.posX);
                int y = MathHelper.floor_double(p.posY);
                int z = MathHelper.floor_double(p.posZ);

                if (!world.isRemote && world.canLightningStrikeAt(x, y, z) && ((held && itemRand.nextInt(50) == 0) || itemRand.nextInt(80) == 0))
                {
                    killItemTorch(world, p, ist, "random.fizz");
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
            int d = ist.getItemDamage();

            if (ist.itemID == this.itemID)
            {
                if (ei.handleWaterMovement())
                {
                    killEntityTorch(ei, "random.fizz");
                    return false;
                }

                if (d >= ConfigCommon.torchLifespanMax)
                {
                    if (ConfigCommon.torchSingleUse)
                    {
                        destroyEntityTorch(ei);
                    }
                    else
                    {
                        killEntityTorch(ei, "fire.fire");
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
                        killEntityTorch(ei, "fire.fire");
                    }

                    return false;
                }

                int x = MathHelper.floor_double(ei.posX);
                int y = MathHelper.floor_double(ei.posY);
                int z = MathHelper.floor_double(ei.posZ);

                if (ei.worldObj.canLightningStrikeAt(x, y, z) && itemRand.nextInt(30) == 0)
                {
                    killEntityTorch(ei, "random.fizz");
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
        world.playSoundAtEntity(p, "fire.fire", 1F, 1F);
        p.inventory.setInventorySlotContents(slot, null);
    }

    private static void killItemTorch(World world, EntityPlayer p, ItemStack ist, String sound)
    {
        world.playSoundAtEntity(p, sound, 0.8F, 1F);
        ist.itemID = ConfigCommon.blockIdTorchUnlit;
        ist.setItemDamage(0);
    }

    private static void destroyEntityTorch(EntityItem ei)
    {
        ei.worldObj.playSoundEffect(ei.posX, ei.posY, ei.posZ, "fire.fire", 1F, 1F);
        ei.setDead();
    }

    private static void killEntityTorch(EntityItem ei, String sound)
    {
        ei.worldObj.playSoundEffect(ei.posX, ei.posY, ei.posZ, sound, 0.6F, 1F);
        ei.getEntityItem().itemID = ConfigCommon.blockIdTorchUnlit;
        ei.getEntityItem().setItemDamage(0);
        PacketDispatcher.sendPacketToAllInDimension(new Packet02UpdateEntity(ei).create(), ei.worldObj.provider.dimensionId);
    }
}
