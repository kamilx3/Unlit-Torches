package pelep.unlittorch.ai;

import java.util.Comparator;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIHelper
{
    public static class TorchSorter implements Comparator<TorchCoordinates>
    {
        private EntityLivingBase el;
        
        public TorchSorter(EntityLivingBase el)
        {
            this.el = el;
        }
        
        @Override
        public int compare(TorchCoordinates t1, TorchCoordinates t2)
        {
            double d1 = this.el.getDistanceSq(t1.x + 0.5, t1.y + 0.5, t1.z + 0.5);
            double d2 = this.el.getDistanceSq(t2.x + 0.5, t2.y + 0.5, t2.z + 0.5);
            return d1 < d2 ? -1 : (d1 > d2 ? 1 : 0);
        }
    }
    
    public static class TorchCoordinates
    {
        public final int x;
        public final int y;
        public final int z;
        
        public TorchCoordinates(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    public static boolean canEntitySeeTorch(EntityLivingBase el, TorchCoordinates t, int r)
    {
        Vec3 v1 = el.worldObj.getWorldVec3Pool().getVecFromPool(el.posX, el.posY + el.getEyeHeight(), el.posZ);
        Vec3 v2 = el.worldObj.getWorldVec3Pool().getVecFromPool(t.x + 0.5, t.y + 0.5, t.z + 0.5);
        World world = el.worldObj;
        
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
            
            if (v1x == t.x && v1y == t.y && v1z == t.z)
            {
                return true;
            }
            
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
            
            while (r-- >= 0)
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
                double xAdjust = 999D;
                double yAdjust = 999D;
                double zAdjust = 999D;
                
                if (v2x > v1x)
                {
                    xAdjust = v1x + 1D;
                }
                else if (v2x < v1x)
                {
                    xAdjust = v1x;
                }
                else
                {
                    xIsAngled = false;
                }
                
                if (v2y > v1y)
                {
                    yAdjust = v1y + 1D;
                }
                else if (v2y < v1y)
                {
                    yAdjust = v1y;
                }
                else
                {
                    yIsAngled = false;
                }

                if (v2z > v1z)
                {
                    zAdjust = v1z + 1D;
                }
                else if (v2z < v1z)
                {
                    zAdjust = v1z;
                }
                else
                {
                    zIsAngled = false;
                }
                
                double xaf = 999D;
                double yaf = 999D;
                double zaf = 999D;
                double xLength = v2.xCoord - v1.xCoord;
                double yLength = v2.yCoord - v1.yCoord;
                double zLength = v2.zCoord - v1.zCoord;

                if (xIsAngled)
                {
                    xaf = (xAdjust - v1.xCoord) / xLength;
                }

                if (yIsAngled)
                {
                    yaf = (yAdjust - v1.yCoord) / yLength;
                }

                if (zIsAngled)
                {
                    zaf = (zAdjust - v1.zCoord) / zLength;
                }
                
                byte b;
                
                if (xaf < yaf && xaf < zaf)
                {
                    if (v2x > v1x)
                    {
                        b = 4;
                    }
                    else
                    {
                        b = 5;
                    }
                    
                    v1.xCoord = xAdjust;
                    v1.yCoord += yLength * xaf;
                    v1.zCoord += zLength * xaf;
                }
                else if (yaf < zaf)
                {
                    if (v2y > v1y)
                    {
                        b = 0;
                    }
                    else
                    {
                        b = 1;
                    }
                    
                    v1.xCoord += xLength * yaf;
                    v1.yCoord = yAdjust;
                    v1.zCoord += zLength * yaf;
                }
                else
                {
                    if (v2z > v1z)
                    {
                        b = 2;
                    }
                    else
                    {
                        b = 3;
                    }
                    
                    v1.xCoord += xLength * zaf;
                    v1.yCoord += yLength * zaf;
                    v1.zCoord = zAdjust;
                }
                
                Vec3 v3 = world.getWorldVec3Pool().getVecFromPool(v1.xCoord, v1.yCoord, v1.zCoord);

                v1x = (int)(v3.xCoord = MathHelper.floor_double(v1.xCoord));
                v1y = (int)(v3.yCoord = MathHelper.floor_double(v1.yCoord));
                v1z = (int)(v3.zCoord = MathHelper.floor_double(v1.zCoord));
                
                if (b == 5)
                {
                    v1x--;
                    v3.xCoord++;
                }
                
                if (b == 1)
                {
                    v1y--;
                    v3.yCoord++;
                }
                
                if (b == 3)
                {
                    v1z--;
                    v3.zCoord++;
                }
                
                if (v1x == t.x && v1y == t.y && v1z == t.z)
                {
                    return true;
                }
                
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
