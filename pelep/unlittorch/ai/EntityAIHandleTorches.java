package pelep.unlittorch.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import pelep.pcl.util.vec.Coordinates;
import pelep.unlittorch.block.BlockTorchLit;
import pelep.unlittorch.block.BlockTorchUnlit;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.tileentity.TileEntityTorch;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author pelep
 */
public class EntityAIHandleTorches extends EntityAIBase
{
    private final EntityLiving el;
    private final World world;
    private final PriorityQueue<Coordinates> torches;
    private Coordinates torch;
    private int delay;
    private int timer;

    public EntityAIHandleTorches(EntityLiving el)
    {
        this.el = el;
        world = el.worldObj;
        torches = new PriorityQueue<Coordinates>(4, new TorchSorter(el));
        setMutexBits(1|2|4);
    }

    @Override
    public boolean shouldExecute()
    {
        if (delay > 0) delay--;
        long time = world.getTotalWorldTime() % 24000;
        return (delay == 0 || time == 13000 || time == 23500) && findTorches();
    }

    @Override
    public void startExecuting()
    {
        nextTorch();
    }

    @Override
    public void updateTask()
    {
        if (canEntitySeeBlock(el, torch, 4D))
        {
            if (isBlockValid(torch))
            {
                int id = world.getBlockId(torch.x, torch.y, torch.z);

                if (id == ConfigCommon.blockIdTorchLit)
                {
                    BlockTorchLit.extinguishBlock(world, torch.x, torch.y, torch.z, "fire.fire", 1F);
                }
                else if (id == ConfigCommon.blockIdTorchUnlit)
                {
                    BlockTorchUnlit.igniteBlock(world, torch.x, torch.y, torch.z, "fire.fire");
                }
            }

            nextTorch();
        }
        else if (timer++ >= 200)
        {
            nextTorch();
        }
    }

    @Override
    public boolean continueExecuting()
    {
        return torch != null;
    }

    @Override
    public void resetTask()
    {
        delay = 300;
        torch = null;
        torches.clear();
    }

    private void nextTorch()
    {
        timer = 0;
        torch = poll();
        if (torch != null)
            el.getNavigator().tryMoveToXYZ(torch.x + 0.5D, torch.y, torch.z + 0.5D, 0.6D);
    }

    private Coordinates poll()
    {
        Coordinates torch = torches.poll();
        return torch == null ? null : (isBlockValid(torch) ? torch : poll());
    }

    private boolean findTorches()
    {
        delay = 300;
        int x = MathHelper.floor_double(el.posX);
        int y = MathHelper.floor_double(el.posY);
        int z = MathHelper.floor_double(el.posZ);
        int r = 16;

        for (int i = -r; i <= r; i++)
        {
            for (int j = -r; j <= r; j++)
            {
                for (int k = -r; k <= r; k++)
                {
                    Coordinates coord = new Coordinates(x + i, y + j, z + k);
                    if (isBlockValid(coord)) torches.add(coord);
                }
            }
        }

        return !torches.isEmpty();
    }

    private boolean isBlockValid(Coordinates coord)
    {
        int id = world.getBlockId(coord.x, coord.y, coord.z);

        if (id == ConfigCommon.blockIdTorchLit)
        {
            TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(coord.x, coord.y, coord.z);
            return !te.eternal && world.isDaytime();
        }
        else if (id == ConfigCommon.blockIdTorchUnlit)
        {
            TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(coord.x, coord.y, coord.z);
            return !te.eternal && !world.isDaytime() && !world.canLightningStrikeAt(coord.x, coord.y, coord.z);
        }

        return false;
    }

    private class TorchSorter implements Comparator<Coordinates>
    {
        private EntityLiving el;

        public TorchSorter(EntityLiving el)
        {
            this.el = el;
        }

        @Override
        public int compare(Coordinates t1, Coordinates t2)
        {
            double d1 = el.getDistanceSq(t1.x + 0.5, t1.y + 0.5, t1.z + 0.5);
            double d2 = el.getDistanceSq(t2.x + 0.5, t2.y + 0.5, t2.z + 0.5);
            return d1 < d2 ? -1 : (d1 > d2 ? 1 : 0);
        }
    }

    static boolean canEntitySeeBlock(EntityLiving el, Coordinates coord, double r)
    {
        if (coord == null || el.getDistance(coord.x + 0.5D, coord.y + 0.5D, coord.z + 0.5D) > r) return false;
        Vec3 p1 = el.worldObj.getWorldVec3Pool().getVecFromPool(el.posX, el.posY + el.getEyeHeight(), el.posZ);
        Vec3 p2 = el.worldObj.getWorldVec3Pool().getVecFromPool(coord.x + 0.5, coord.y + 0.5, coord.z + 0.5);
        MovingObjectPosition mop = el.worldObj.clip(p1, p2);
        return mop == null || (mop.blockX == coord.x && mop.blockY == coord.y && mop.blockZ == coord.z);
    }
}
