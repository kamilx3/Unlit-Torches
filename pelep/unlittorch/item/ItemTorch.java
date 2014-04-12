package pelep.unlittorch.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import pelep.pcl.helper.RayTraceHelper;

import java.util.List;

/**
 * @author pelep
 */
abstract class ItemTorch extends ItemBlock
{
    public ItemTorch(int id)
    {
        super(id);
        setCreativeTab(CreativeTabs.tabDecorations);
        setHasSubtypes(true);
        setNoRepair();
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    @Override
    public void addInformation(ItemStack ist, EntityPlayer p, List list, boolean adv)
    {
        if (ist.stackTagCompound != null)
            list.add(StatCollector.translateToLocalFormatted("unlittorch.eternal"));
    }

    protected static Material getMaterialClicked(World world, EntityPlayer p)
    {
        MovingObjectPosition mop = RayTraceHelper.rayTraceFromPlayer(world, p, false, true);

        if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE)
        {
            return world.getBlockMaterial(mop.blockX, mop.blockY, mop.blockZ);
        }

        return null;
    }
}
