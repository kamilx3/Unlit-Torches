package pelep.unlittorch.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import pelep.pcl.helper.RayTraceHelper;
import pelep.unlittorch.config.ConfigCommon;

import java.util.List;

/**
 * @author pelep
 */
public class ItemCloth extends Item
{
    @SideOnly(Side.CLIENT)
    private static Icon icon_wet;

    public ItemCloth()
    {
        super(ConfigCommon.itemIdCloth - 256);
        setCreativeTab(CreativeTabs.tabMaterials);
        setHasSubtypes(true);
        setMaxDamage(0);
        setNoRepair();
        setUnlocalizedName("unlittorch:cloth");
        setTextureName("unlittorch:cloth");
    }

    @Override
    public boolean isItemTool(ItemStack ist)
    {
        return false;
    }


    //--------------------------------register---------------------------------//


    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(int id, CreativeTabs ct, List list)
    {
        list.add(new ItemStack(id, 1, 0));
        list.add(new ItemStack(id, 1, 1));
    }

    @Override
    public String getUnlocalizedName(ItemStack ist)
    {
        switch(ist.getItemDamage())
        {
            case 0: return "item.unlittorch:cloth_dry";
            case 1: return "item.unlittorch:cloth_wet";
            default: return "item.unlittorch:cloth";
        }
    }


    //--------------------------------rendering--------------------------------//


    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister ir)
    {
        itemIcon = ir.registerIcon("unlittorch:cloth_dry");
        icon_wet = ir.registerIcon("unlittorch:cloth_wet");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIconFromDamage(int d)
    {
        switch (d)
        {
            case 0: return itemIcon;
            default: return icon_wet;
        }
    }


    //---------------------------------itemuse---------------------------------//


    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer p)
    {
        if (!p.isSneaking())
        {
            MovingObjectPosition mop = RayTraceHelper.rayTraceFromPlayer(world, p, false, true);

            if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE)
            {
                Material m = world.getBlockMaterial(mop.blockX, mop.blockY, mop.blockZ);

                if (ist.getItemDamage() == 0 && m == Material.water)
                {
                    p.swingItem();
                    ist.setItemDamage(1);
                }
                else if (ist.getItemDamage() == 1 && (m == Material.fire || m == Material.lava))
                {
                    p.swingItem();
                    ist.setItemDamage(0);
                }
            }
        }

        return ist;
    }
}
