package pelep.unlittorch.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import pelep.unlittorch.ai.EntityAIHelper.TorchInfo;
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
    private TorchInfo torch;
    private int delay;

    public EntityAIBreakTorches(EntityLiving el)
    {
        this.el = el;
        this.world = el.worldObj;
        this.setMutexBits(2);
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
        BlockTorchLit.killBlockTorch(this.world, this.torch.x, this.torch.y, this.torch.z, "fire.fire", 1F);
        this.delay = 100;
        this.el.getLookHelper().setLookPosition(this.torch.x + 0.5, this.torch.y + 0.5, this.torch.z + 0.5, 10F, this.el.getVerticalFaceSpeed());
        this.torch = null;
    }

    private boolean findTorch()
    {
        this.delay = 20;
        int ex = MathHelper.floor_double(this.el.posX);
        int ey = MathHelper.floor_double(this.el.posY);
        int ez = MathHelper.floor_double(this.el.posZ);
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

                    if (this.world.getBlockId(x, y, z) == ConfigCommon.blockIdTorchLit)
                    {
                        TileEntityTorch te = (TileEntityTorch) this.world.getBlockTileEntity(x, y, z);

                        if (te.isEternal()) continue;

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
