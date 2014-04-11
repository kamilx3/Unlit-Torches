package pelep.unlittorch.ai;

import static pelep.unlittorch.ai.EntityAIHandleTorches.canEntitySeeBlock;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import pelep.pcl.util.vec.Coordinate;
import pelep.unlittorch.block.BlockTorchLit;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.tileentity.TileEntityTorch;

/**
 * @author pelep
 */
public class EntityAIBreakTorches extends EntityAIBase
{
    private final EntityLiving el;
    private final World world;
    private Coordinate torch;
    private int delay;

    public EntityAIBreakTorches(EntityLiving el)
    {
        this.el = el;
        this.world = el.worldObj;
        setMutexBits(2);
    }

    @Override
    public boolean shouldExecute()
    {
        if (delay > 0) delay--;
        return delay == 0 && findTorch();
    }

    @Override
    public void startExecuting()
    {
        BlockTorchLit.extinguishBlock(world, torch.x, torch.y, torch.z, "fire.fire", 1F);
        delay = 100;
        el.getLookHelper().setLookPosition(torch.x + 0.5, torch.y + 0.5, torch.z + 0.5, 10F, el.getVerticalFaceSpeed());
        torch = null;
    }

    private boolean findTorch()
    {
        delay = 20;
        int ex = MathHelper.floor_double(el.posX);
        int ey = MathHelper.floor_double(el.posY);
        int ez = MathHelper.floor_double(el.posZ);
        int r = 3;

        for (int i = -r; i <= r; i++)
        {
            for (int j = -r; j <= r; j++)
            {
                for (int k = -r; k <= r; k++)
                {
                    int x = ex + i;
                    int y = ey + j;
                    int z = ez + k;

                    if (world.getBlockId(x, y, z) == ConfigCommon.blockIdTorchLit)
                    {
                        TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);

                        if (te.isEternal()) continue;

                        Coordinate coord = new Coordinate(x, y, z);

                        if (canEntitySeeBlock(el, coord, r + 0.5D))
                        {
                            if (torch == null)
                            {
                                torch = coord;
                            }
                            else
                            {
                                double x1 = torch.x + 0.5D;
                                double y1 = torch.y + 0.5D;
                                double z1 = torch.z + 0.5D;
                                double x2 = coord.x + 0.5D;
                                double y2 = coord.y + 0.5D;
                                double z2 = coord.z + 0.5D;

                                if (el.getDistanceSq(x1, y1, z1) > el.getDistanceSq(x2, y2, z2))
                                    torch = coord;
                            }
                        }
                    }
                }
            }
        }

        return torch != null;
    }
}
