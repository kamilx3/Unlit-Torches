package pelep.unlittorch.ai;

import static pelep.unlittorch.ai.EntityAIHandleTorches.canEntitySeeBlock;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import pelep.pcl.util.vec.Coordinates;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.tileentity.TileEntityTorch;

/**
 * @author pelep
 */
public class EntityAIShootTorches extends EntityAIBase
{
    private final EntityLiving el;
    private final World world;
    private Coordinates torch;

    private int delay = 0;
    private int retry = 0;

    public EntityAIShootTorches(EntityLiving el)
    {
        this.el = el;
        world = el.worldObj;
        setMutexBits(1|2);
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
        delay = world.rand.nextInt(30) + 30;
        retry = world.rand.nextInt(2);
        shoot(torch.x + 0.5, torch.y + 0.5, torch.z + 0.5);
    }

    private void shoot(double x, double y, double z)
    {
        EntityArrow ea = new EntityArrow(world);

        double distx = x - el.posX;
        double distz = z - el.posZ;
        double dist = MathHelper.sqrt_double(distx * distx + distz * distz);

        if (dist >= 1.0E-7D)
        {
            ea.shootingEntity = el;
            ea.posY = el.posY + el.getEyeHeight() - 0.10000000149011612D;
            ea.setDamage(1D + world.rand.nextGaussian() * 0.25D + (world.difficultySetting * 0.11F));

            double disty = y - el.posY - 2;
            float yaw = (float)(Math.atan2(distz, distx) * 180D / Math.PI) - 90F;
            float pitch = (float)(-(Math.atan2(disty, dist) * 180D / Math.PI));

            double i = distx / dist;
            double j = (float) dist * 0.2F;
            double k = distz / dist;

            ea.setLocationAndAngles(el.posX + i, ea.posY, el.posZ + k, yaw, pitch);
            ea.yOffset = 0F;
            ea.setThrowableHeading(distx, disty + j, distz, 1.3F, (14 - world.difficultySetting * 4));

            int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, el.getHeldItem());
            int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, el.getHeldItem());

            if (power > 0) ea.setDamage(ea.getDamage() + power * 0.5D + 0.5D);
            if (punch > 0) ea.setKnockbackStrength(punch);
            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, el.getHeldItem()) > 0)
                ea.setFire(100);

            el.getLookHelper().setLookPosition(x, y, z, 30F, 30F);
            world.playSoundAtEntity(el, "random.bow", 1F, 1F / (world.rand.nextFloat() * 0.4F + 0.8F));
            world.spawnEntityInWorld(ea);
        }
    }

    @Override
    public boolean continueExecuting()
    {
        return retry > 0;
    }

    @Override
    public void updateTask()
    {
        if (world.getBlockId(torch.x, torch.y, torch.z) != ConfigCommon.blockIdTorchLit)
        {
            retry = 0;
        }
        else if (delay-- == 0)
        {
            delay = 30 + world.rand.nextInt(30);
            retry--;
            shoot(torch.x + 0.5, torch.y + 0.5, torch.z + 0.5);
        }
    }

    @Override
    public void resetTask()
    {
        torch = null;
        delay = 80;
    }

    private boolean findTorch()
    {
        delay = 40;
        int ex = MathHelper.floor_double(el.posX);
        int ey = MathHelper.floor_double(el.posY);
        int ez = MathHelper.floor_double(el.posZ);
        int r = 16;

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

                        if (te.eternal) continue;

                        Coordinates coord = new Coordinates(x, y, z);

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
