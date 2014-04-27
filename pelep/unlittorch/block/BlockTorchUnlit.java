package pelep.unlittorch.block;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
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
    private static Icon icon_used;
    public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

    public BlockTorchUnlit()
    {
        super(ConfigCommon.blockIdTorchUnlit, false);
        setLightValue(0F);
        setUnlocalizedName("unlittorch:torch_unlit");
        setTextureName("unlittorch:torch_off");
        instance = this;
    }


    //--------------------------------render---------------------------------//


    @Override
    public int getRenderType()
    {
        return RENDER_ID;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister ir)
    {
        blockIcon = ir.registerIcon(getTextureName());
        icon_used = ir.registerIcon("unlittorch:torch_used");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int side, int md)
    {
        return side == 1 && md == 1 ? icon_used : blockIcon;
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
            grabBlock(world, x, y, z, p);
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
                    consumeItem(p.inventory.currentItem, p, 1);
                }
                else if (id == Item.flintAndSteel.itemID)
                {
                    igniteBlock(world, x, y, z, "fire.ignite");
                    damageItem(ist, p);
                }
                else
                {
                    igniteBlock(world, x, y, z, "fire.fire");
                    useItem(p.inventory.currentItem, ist, p);
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
        if (world.isRemote) return;

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

        if (!ep.worldObj.isRemote)
        {
            ItemStack ist = ep.inventory.mainInventory[slot];
            int id = ist.itemID;
            if (id != Item.bucketLava.itemID && id != ConfigCommon.blockIdTorchLit && id != Block.torchWood.blockID)
                useItem(slot, ist, ep);
        }

        return true;
    }

    public static void damageItem(ItemStack ist, EntityPlayer ep)
    {
        if (!ep.worldObj.isRemote) ist.damageItem(1, ep);
    }

    public static void useItem(int slot, ItemStack ist, EntityPlayer ep)
    {
        if (!ep.capabilities.isCreativeMode)
        {
            if (Item.itemsList[ist.itemID].isDamageable())
            {
                damageItem(ist, ep);
            }
            else
            {
                consumeItem(slot, ep, 1);
            }
        }
    }
}
