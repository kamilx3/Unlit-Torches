package pelep.unlittorch.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.Comparator;

/**
 * @author pelep
 */
class EntityAIHelper
{
    static class TorchSorter implements Comparator<TorchInfo>
    {
        private EntityLivingBase el;

        public TorchSorter(EntityLivingBase el)
        {
            this.el = el;
        }

        @Override
        public int compare(TorchInfo t1, TorchInfo t2)
        {
            double d1 = this.el.getDistanceSq(t1.x + 0.5, t1.y + 0.5, t1.z + 0.5);
            double d2 = this.el.getDistanceSq(t2.x + 0.5, t2.y + 0.5, t2.z + 0.5);
            return d1 < d2 ? -1 : (d1 > d2 ? 1 : 0);
        }
    }

    static class TorchInfo
    {
        public final int x;
        public final int y;
        public final int z;
        public final boolean lit;

        public TorchInfo(int x, int y, int z)
        {
            this(x, y, z, true);
        }

        public TorchInfo(int x, int y, int z, boolean lit)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.lit = lit;
        }
    }

    static boolean canEntitySeeTorch(EntityLivingBase el, TorchInfo t, double r)
    {
        if (t == null || el.getDistance(t.x + 0.5D, t.y + 0.5D, t.z + 0.5D) > r) return false;

        Vec3 p1 = el.worldObj.getWorldVec3Pool().getVecFromPool(el.posX, el.posY + el.getEyeHeight(), el.posZ);
        Vec3 p2 = el.worldObj.getWorldVec3Pool().getVecFromPool(t.x + 0.5, t.y + 0.5, t.z + 0.5);

        if (!Double.isNaN(p1.xCoord) &&
            !Double.isNaN(p1.yCoord) &&
            !Double.isNaN(p1.zCoord) &&
            !Double.isNaN(p2.xCoord) &&
            !Double.isNaN(p2.yCoord) &&
            !Double.isNaN(p2.zCoord))
        {
            int curx = MathHelper.floor_double(p1.xCoord);
            int cury = MathHelper.floor_double(p1.yCoord);
            int curz = MathHelper.floor_double(p1.zCoord);

            if (curx == t.x && cury == t.y && curz == t.z) return true;

            World world = el.worldObj;
            int id = world.getBlockId(curx, cury, curz);
            int md = world.getBlockMetadata(curx, cury, curz);
            Block block = Block.blocksList[id];

            if (id > 0 && block != null && block.canCollideCheck(md, false))
            {
                MovingObjectPosition mop = block.collisionRayTrace(world, curx, cury, curz, p1, p2);

                if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE)
                {
                    return mop.blockX == t.x && mop.blockY == t.y && mop.blockZ == t.z;
                }
            }

            int i = 200;

            while (i-- >= 0)
            {
                if (Double.isNaN(p1.xCoord) || Double.isNaN(p1.yCoord) || Double.isNaN(p1.zCoord))
                {
                    return false;
                }

                if (curx == t.x && cury == t.y && curz == t.z)
                {
                    return true;
                }

                boolean offsetx = true;
                boolean offsety = true;
                boolean offsetz = true;

                double newx = 999D;
                double newy = 999D;
                double newz = 999D;

                if (t.x > curx)
                {
                    newx = curx + 1D;
                }
                else if (t.x < curx)
                {
                    newx = curx;
                }
                else
                {
                    offsetx = false;
                }

                if (t.y > cury)
                {
                    newy = cury + 1D;
                }
                else if (t.y < cury)
                {
                    newy = cury;
                }
                else
                {
                    offsety = false;
                }

                if (t.z > curz)
                {
                    newz = curz + 1D;
                }
                else if (t.z < curz)
                {
                    newz = curz;
                }
                else
                {
                    offsetz = false;
                }

                double xf = 999D;
                double yf = 999D;
                double zf = 999D;
                double lenx = p2.xCoord - p1.xCoord;
                double leny = p2.yCoord - p1.yCoord;
                double lenz = p2.zCoord - p1.zCoord;

                if (offsetx) xf = (newx - p1.xCoord) / lenx;
                if (offsety) yf = (newy - p1.yCoord) / leny;
                if (offsetz) zf = (newz - p1.zCoord) / lenz;

                byte side;

                if (xf < yf && xf < zf)
                {
                    side = t.x > curx ? (byte) 4 : 5;
                    p1.xCoord = newx;
                    p1.yCoord += leny * xf;
                    p1.zCoord += lenz * xf;
                }
                else if (yf < zf)
                {
                    side = t.y > cury ? (byte) 0 : 1;
                    p1.xCoord += lenx * yf;
                    p1.yCoord = newy;
                    p1.zCoord += lenz * yf;
                }
                else
                {
                    side = t.z > curz ? (byte) 2 : 3;
                    p1.xCoord += lenx * zf;
                    p1.yCoord += leny * zf;
                    p1.zCoord = newz;
                }

                Vec3 p3 = world.getWorldVec3Pool().getVecFromPool(p1.xCoord, p1.yCoord, p1.zCoord);
                curx = (int)(p3.xCoord = MathHelper.floor_double(p1.xCoord));
                cury = (int)(p3.yCoord = MathHelper.floor_double(p1.yCoord));
                curz = (int)(p3.zCoord = MathHelper.floor_double(p1.zCoord));

                switch (side)
                {
                    case 5:
                        curx--;
                        p3.xCoord++;
                        break;
                    case 1:
                        cury--;
                        p3.yCoord++;
                        break;
                    case 3:
                        curz--;
                        p3.zCoord++;
                }

                if (curx == t.x && cury == t.y && curz == t.z)
                {
                    return true;
                }

                id = world.getBlockId(curx, cury, curz);
                md = world.getBlockMetadata(curx, cury, curz);
                block = Block.blocksList[id];

                if (id > 0 && block != null && block.canCollideCheck(md, false))
                {
                    MovingObjectPosition mop = block.collisionRayTrace(world, curx, cury, curz, p1, p2);

                    if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE)
                    {
                        return mop.blockX == t.x && mop.blockY == t.y && mop.blockZ == t.z;
                    }
                }
            }
        }

        return false;
    }
}
