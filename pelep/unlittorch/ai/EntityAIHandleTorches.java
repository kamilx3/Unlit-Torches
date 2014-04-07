package pelep.unlittorch.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import pelep.pcl.util.vec.Coordinate;
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
    private final PriorityQueue<Coordinate> torches;
    private Coordinate torch;
    private int delay;
    private int timer;

    public EntityAIHandleTorches(EntityLiving el)
    {
        this.el = el;
        this.world = el.worldObj;
        this.torches = new PriorityQueue(4, new TorchSorter(el));
        this.setMutexBits(1|2|4);
    }

    @Override
    public boolean shouldExecute()
    {
        if (this.delay > 0) this.delay--;
        long time = this.world.getTotalWorldTime() % 24000;
        return (this.delay == 0 || time == 13000 || time == 23500) && this.findTorches();
    }

    @Override
    public void startExecuting()
    {
        this.nextTorch();
    }

    @Override
    public void updateTask()
    {
        if (canEntitySeeBlock(this.el, this.torch, 4D))
        {
            if (this.isBlockValid(this.torch))
            {
                int id = this.world.getBlockId(this.torch.x, this.torch.y, this.torch.z);

                if (id == ConfigCommon.blockIdTorchLit)
                {
                    BlockTorchLit.extinguishBlock(this.world, this.torch.x, this.torch.y, this.torch.z, "fire.fire", 1F);
                }
                else if (id == ConfigCommon.blockIdTorchUnlit)
                {
                    BlockTorchUnlit.igniteBlock(this.world, this.torch.x, this.torch.y, this.torch.z, "fire.fire");
                }
            }

            this.nextTorch();
        }
        else if (this.timer++ >= 200)
        {
            this.nextTorch();
        }
    }

    @Override
    public boolean continueExecuting()
    {
        return this.torch != null;
    }

    @Override
    public void resetTask()
    {
        this.delay = 300;
        this.torch = null;
        this.torches.clear();
    }

    private void nextTorch()
    {
        this.timer = 0;
        this.torch = this.poll();
        if (this.torch != null)
            this.el.getNavigator().tryMoveToXYZ(this.torch.x + 0.5D, this.torch.y, this.torch.z + 0.5D, 0.6D);
    }

    private Coordinate poll()
    {
        Coordinate torch = this.torches.poll();
        return torch == null ? null : (this.isBlockValid(torch) ? torch : this.poll());
    }

    private boolean findTorches()
    {
        this.delay = 300;
        int x = MathHelper.floor_double(this.el.posX);
        int y = MathHelper.floor_double(this.el.posY);
        int z = MathHelper.floor_double(this.el.posZ);
        int r = 16;

        for (int i = -r; i <= r; i++)
        {
            for (int j = -r; j <= r; j++)
            {
                for (int k = -r; k <= r; k++)
                {
                    Coordinate coord = new Coordinate(x + i, y + j, z + k);
                    if (this.isBlockValid(coord)) this.torches.add(coord);
                }
            }
        }

        return !this.torches.isEmpty();
    }

    private boolean isBlockValid(Coordinate coord)
    {
        int id = this.world.getBlockId(coord.x, coord.y, coord.z);

        if (id == ConfigCommon.blockIdTorchLit)
        {
            TileEntityTorch te = (TileEntityTorch) this.world.getBlockTileEntity(coord.x, coord.y, coord.z);
            return !te.isEternal() && this.world.isDaytime();
        }
        else if (id == ConfigCommon.blockIdTorchUnlit)
        {
            TileEntityTorch te = (TileEntityTorch) this.world.getBlockTileEntity(coord.x, coord.y, coord.z);
            return !te.isEternal() && !this.world.isDaytime() && !this.world.canLightningStrikeAt(coord.x, coord.y, coord.z);
        }

        return false;
    }

    private class TorchSorter implements Comparator<Coordinate>
    {
        private EntityLiving el;

        public TorchSorter(EntityLiving el)
        {
            this.el = el;
        }

        @Override
        public int compare(Coordinate t1, Coordinate t2)
        {
            double d1 = this.el.getDistanceSq(t1.x + 0.5, t1.y + 0.5, t1.z + 0.5);
            double d2 = this.el.getDistanceSq(t2.x + 0.5, t2.y + 0.5, t2.z + 0.5);
            return d1 < d2 ? -1 : (d1 > d2 ? 1 : 0);
        }
    }

    static boolean canEntitySeeBlock(EntityLiving el, Coordinate coord, double r)
    {
        if (coord == null || el.getDistance(coord.x + 0.5D, coord.y + 0.5D, coord.z + 0.5D) > r) return false;
        Vec3 p1 = el.worldObj.getWorldVec3Pool().getVecFromPool(el.posX, el.posY + el.getEyeHeight(), el.posZ);
        Vec3 p2 = el.worldObj.getWorldVec3Pool().getVecFromPool(coord.x + 0.5, coord.y + 0.5, coord.z + 0.5);
        MovingObjectPosition mop = el.worldObj.clip(p1, p2);
        return mop == null || (mop.blockX == coord.x && mop.blockY == coord.y && mop.blockZ == coord.z);
    }
}
