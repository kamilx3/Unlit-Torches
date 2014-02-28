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
public class EntityAIHelper
{
    public static class TorchSorter implements Comparator<TorchInfo>
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

    public static class TorchInfo
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

    public static boolean canEntitySeeTorch(EntityLivingBase el, TorchInfo t, double r)
    {
        if (el.getDistance(t.x + 0.5D, t.y + 0.5D, t.z + 0.5D) > r) return false;

        Vec3 v1 = el.worldObj.getWorldVec3Pool().getVecFromPool(el.posX, el.posY + el.getEyeHeight(), el.posZ);
        Vec3 v2 = el.worldObj.getWorldVec3Pool().getVecFromPool(t.x + 0.5, t.y + 0.5, t.z + 0.5);

        if (!Double.isNaN(v1.xCoord) &&
            !Double.isNaN(v1.yCoord) &&
            !Double.isNaN(v1.zCoord) &&
            !Double.isNaN(v2.xCoord) &&
            !Double.isNaN(v2.yCoord) &&
            !Double.isNaN(v2.zCoord))
        {
            int v1x = MathHelper.floor_double(v1.xCoord);
            int v1y = MathHelper.floor_double(v1.yCoord);
            int v1z = MathHelper.floor_double(v1.zCoord);
            int v2x = MathHelper.floor_double(v2.xCoord);
            int v2y = MathHelper.floor_double(v2.yCoord);
            int v2z = MathHelper.floor_double(v2.zCoord);

            if (v1x == t.x && v1y == t.y && v1z == t.z) return true;

            World world = el.worldObj;
            int id = world.getBlockId(v1x, v1y, v1z);
            int md = world.getBlockMetadata(v1x, v1y, v1z);
            Block block = Block.blocksList[id];

            if (block != null)
            {
                MovingObjectPosition mop;

                if (id > 0 && block.canCollideCheck(md, false))
                {
                    mop = block.collisionRayTrace(world, v1x, v1y, v1z, v1, v2);

                    if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE)
                    {
                        return mop.blockX == t.x && mop.blockY == t.y && mop.blockZ == t.z;
                    }
                }
            }

            int i = 200;

            while (i-- >= 0)
            {
                if (Double.isNaN(v1.xCoord) || Double.isNaN(v1.yCoord) || Double.isNaN(v1.zCoord))
                {
                    return false;
                }

                if (v1x == v2x && v1y == v2y && v1z == v2z)
                {
                    return false;
                }

                boolean xIsAngled = true;
                boolean yIsAngled = true;
                boolean zIsAngled = true;
                double d0 = 999D;
                double d1 = 999D;
                double d2 = 999D;

                if (v2x > v1x)
                {
                    d0 = v1x + 1D;
                }
                else if (v2x < v1x)
                {
                    d0 = v1x;
                }
                else
                {
                    xIsAngled = false;
                }

                if (v2y > v1y)
                {
                    d1 = v1y + 1D;
                }
                else if (v2y < v1y)
                {
                    d1 = v1y;
                }
                else
                {
                    yIsAngled = false;
                }

                if (v2z > v1z)
                {
                    d2 = v1z + 1D;
                }
                else if (v2z < v1z)
                {
                    d2 = v1z;
                }
                else
                {
                    zIsAngled = false;
                }

                double d3 = 999D;
                double d4 = 999D;
                double d5 = 999D;
                double distX = v2.xCoord - v1.xCoord;
                double distY = v2.yCoord - v1.yCoord;
                double distZ = v2.zCoord - v1.zCoord;

                if (xIsAngled) d3 = (d0 - v1.xCoord) / distX;
                if (yIsAngled) d4 = (d1 - v1.yCoord) / distY;
                if (zIsAngled) d5 = (d2 - v1.zCoord) / distZ;

                byte b;

                if (d3 < d4 && d3 < d5)
                {
                    b = v2x > v1x ? (byte) 4 : 5;
                    v1.xCoord = d0;
                    v1.yCoord += distY * d3;
                    v1.zCoord += distZ * d3;
                }
                else if (d4 < d5)
                {
                    b = v2y > v1y ? (byte) 0 : 1;
                    v1.xCoord += distX * d4;
                    v1.yCoord = d1;
                    v1.zCoord += distZ * d4;
                }
                else
                {
                    b = v2z > v1z ? (byte) 2 : 3;
                    v1.xCoord += distX * d5;
                    v1.yCoord += distY * d5;
                    v1.zCoord = d2;
                }

                Vec3 v3 = world.getWorldVec3Pool().getVecFromPool(v1.xCoord, v1.yCoord, v1.zCoord);

                v1x = (int)(v3.xCoord = MathHelper.floor_double(v1.xCoord));
                v1y = (int)(v3.yCoord = MathHelper.floor_double(v1.yCoord));
                v1z = (int)(v3.zCoord = MathHelper.floor_double(v1.zCoord));

                switch (b)
                {
                    case 5:
                        v1x--;
                        v3.xCoord++;
                        break;
                    case 1:
                        v1y--;
                        v3.yCoord++;
                        break;
                    case 3:
                        v1z--;
                        v3.zCoord++;
                }

                if (v1x == t.x && v1y == t.y && v1z == t.z) return true;

                id = world.getBlockId(v1x, v1y, v1z);
                md = world.getBlockMetadata(v1x, v1y, v1z);
                block = Block.blocksList[id];

                if (block != null)
                {
                    MovingObjectPosition mop;

                    if (id > 0 && block.canCollideCheck(md, false))
                    {
                        mop = block.collisionRayTrace(world, v1x, v1y, v1z, v1, v2);

                        if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE)
                        {
                            return mop.blockX == t.x && mop.blockY == t.y && mop.blockZ == t.z;
                        }
                    }
                }
            }
        }

        return false;
    }
}
