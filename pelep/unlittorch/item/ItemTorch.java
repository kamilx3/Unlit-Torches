package pelep.unlittorch.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import pelep.pcl.helper.RayTraceHelper;

/**
 * @author pelep
 */
class ItemTorch extends ItemBlock
{
    public ItemTorch(int id)
    {
        super(id);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHasSubtypes(true);
        this.setNoRepair();
    }

    protected static Material rayTraceFromPlayer(World world, EntityPlayer p)
    {
        MovingObjectPosition mop = RayTraceHelper.rayTraceFromPlayer(world, p, false, true);

        if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE)
        {
            return world.getBlockMaterial(mop.blockX, mop.blockY, mop.blockZ);
        }

        return null;
    }
}
