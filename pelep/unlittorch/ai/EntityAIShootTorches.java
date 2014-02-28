package pelep.unlittorch.ai;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import pelep.unlittorch.ai.EntityAIHelper.TorchInfo;
import pelep.unlittorch.ai.EntityAIHelper.TorchSorter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author pelep
 */
public class EntityAIShootTorches extends EntityAIBase
{
    private final EntityLiving el;
    private final World world;
    private TorchInfo torch;

    private int delay = 0;
    private int retry = 0;

    public EntityAIShootTorches(EntityLiving el)
    {
        this.el = el;
        this.world = el.worldObj;
        this.setMutexBits(1|2);
    }

    @Override
    public boolean shouldExecute()
    {
        if (this.delay > 0) this.delay--;
        return this.delay == 0 && this.findTorch();
    }

    @Override
    public void startExecuting()
    {
        this.delay = this.world.rand.nextInt(30) + 30;
        this.retry = this.world.rand.nextInt(2);
        this.shoot(this.torch.x + 0.5, this.torch.y + 0.5, this.torch.z + 0.5);
    }

    private void shoot(double x, double y, double z)
    {
        EntityArrow ea = new EntityArrow(this.world);

        double distx = x - this.el.posX;
        double disty = y - this.el.posY - 2;
        double distz = z - this.el.posZ;
        double dist = MathHelper.sqrt_double(distx * distx + distz * distz);

        if (dist >= 1.0E-7D)
        {
            ea.shootingEntity = this.el;
            ea.posY = this.el.posY + this.el.getEyeHeight() - 0.10000000149011612D;
            ea.setDamage(1D + this.world.rand.nextGaussian() * 0.25D + (this.world.difficultySetting * 0.11F));

            float yaw = (float)(Math.atan2(distz, distx) * 180D / Math.PI) - 90F;
            float pitch = (float)(-(Math.atan2(disty, dist) * 180D / Math.PI));

            double i = distx / dist;
            double j = distz / dist;
            double k = (float)dist * 0.2F;

            ea.setLocationAndAngles(this.el.posX + i, ea.posY, this.el.posZ + j, yaw, pitch);
            ea.yOffset = 0F;
            ea.setThrowableHeading(distx, disty + k, distz, 1.3F, (14 - this.world.difficultySetting * 4));

            int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.el.getHeldItem());
            int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.el.getHeldItem());

            if (power > 0)
            {
                ea.setDamage(ea.getDamage() + power * 0.5D + 0.5D);
            }

            if (punch > 0)
            {
                ea.setKnockbackStrength(punch);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.el.getHeldItem()) > 0)
            {
                ea.setFire(100);
            }

            this.el.getLookHelper().setLookPosition(x, y, z, 30F, 30F);
            this.world.playSoundAtEntity(this.el, "random.bow", 1F, 1F / (this.world.rand.nextFloat() * 0.4F + 0.8F));
            this.world.spawnEntityInWorld(ea);
        }
    }

    @Override
    public boolean continueExecuting()
    {
        return this.retry > 0;
    }

    @Override
    public void updateTask()
    {
        if (this.world.getBlockId(this.torch.x, this.torch.y, this.torch.z) != 50)
        {
            this.retry = 0;
        }
        else if (this.delay-- == 0)
        {
            this.delay = 30 + this.world.rand.nextInt(30);
            this.retry--;
            this.shoot(this.torch.x + 0.5, this.torch.y + 0.5, this.torch.z + 0.5);
        }
    }

    @Override
    public void resetTask()
    {
        this.torch = null;
        this.delay = 80;
    }

    private boolean findTorch()
    {
        int ex = MathHelper.floor_double(this.el.posX);
        int ey = MathHelper.floor_double(this.el.posY);
        int ez = MathHelper.floor_double(this.el.posZ);
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

                    if (this.world.getBlockId(x, y, z) == 50)
                    {
                        TorchInfo torch = new TorchInfo(x, y, z);

                        if (EntityAIHelper.canEntitySeeTorch(this.el, torch, r + 0.5D))
                        {
                            if (this.torch == null)
                            {
                                this.torch = torch;
                            }
                            else
                            {
                                double x1 = this.torch.x + 0.5D;
                                double y1 = this.torch.y + 0.5D;
                                double z1 = this.torch.z + 0.5D;
                                double x2 = torch.x + 0.5D;
                                double y2 = torch.y + 0.5D;
                                double z2 = torch.z + 0.5D;

                                if (this.el.getDistanceSq(x1, y1, z1) > this.el.getDistanceSq(x2, y2, z2))
                                {
                                    this.torch = torch;
                                }
                            }
                        }
                    }
                }
            }
        }

        return this.torch != null;
    }
}
