package pelep.unlittorch.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import pelep.unlittorch.ai.EntityAIHelper.TorchInfo;
import pelep.unlittorch.ai.EntityAIHelper.TorchSorter;
import pelep.unlittorch.block.BlockTorchLit;
import pelep.unlittorch.block.BlockTorchUnlit;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.tileentity.TileEntityTorch;

import java.util.PriorityQueue;

/**
 * @author pelep
 */
public class EntityAIHandleTorches extends EntityAIBase
{
    private final EntityLiving el;
    private final World world;
    private final PriorityQueue<TorchInfo> torches;
    private TorchInfo torch;
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
        if (EntityAIHelper.canEntitySeeTorch(this.el, this.torch, 4D))
        {
            int id = this.world.getBlockId(this.torch.x, this.torch.y, this.torch.z);

            if (this.torch.lit && id == ConfigCommon.blockIdTorchLit)
            {
                BlockTorchLit.killBlockTorch(this.world, torch.x, torch.y, torch.z, "fire.fire", 1F);
            }
            else if (!this.torch.lit && id == ConfigCommon.blockIdTorchUnlit)
            {
                TileEntityTorch te = (TileEntityTorch) this.world.getBlockTileEntity(this.torch.x, this.torch.y, this.torch.z);
                BlockTorchUnlit.igniteBlockTorch(te.isEternal(), te.getAge(), this.world, torch.x, torch.y, torch.z, "fire.fire");
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
        this.torch = this.getTorch();
        if (this.torch != null)
        {
            this.el.getNavigator().tryMoveToXYZ(this.torch.x + 0.5D, this.torch.y, this.torch.z + 0.5D, 0.6D);
        }
    }

    private TorchInfo getTorch()
    {
        TorchInfo torch = this.torches.poll();

        if (torch != null)
        {
            int id = this.world.getBlockId(torch.x, torch.y, torch.z);

            if ((id == ConfigCommon.blockIdTorchLit && torch.lit) || (id == ConfigCommon.blockIdTorchUnlit && !torch.lit))
            {
                return torch;
            }

            return this.getTorch();
        }

        return null;
    }

    private boolean findTorches()
    {
        this.delay = 300;
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
                    int id = this.world.getBlockId(x, y, z);

                    if (id == ConfigCommon.blockIdTorchLit && this.world.isDaytime())
                    {
                        TileEntityTorch te = (TileEntityTorch) this.world.getBlockTileEntity(x, y, z);
                        if (te.isEternal()) continue;
                        this.torches.add(new TorchInfo(x, y, z, true));
                    }
                    else if (id == ConfigCommon.blockIdTorchUnlit && !this.world.isDaytime() && (!this.world.isRaining() || !this.world.canLightningStrikeAt(x, y, z)))
                    {
                        this.torches.add(new TorchInfo(x, y, z, false));
                    }
                }
            }
        }

        return !this.torches.isEmpty();
    }
}
