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

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author pelep
 */
public class EntityAIHandleTorches extends EntityAIBase
{
    private final EntityLiving el;
    private final World world;
    private final ArrayList<TorchInfo> torches = new ArrayList();
    private final TorchSorter sorter;
    private int delay;

    public EntityAIHandleTorches(EntityLiving el)
    {
        this.el = el;
        this.world = el.worldObj;
        this.sorter = new TorchSorter(el);
        this.setMutexBits(1|2|5);
    }

    @Override
    public boolean shouldExecute()
    {
        if (this.delay > 0) this.delay--;
        return this.delay == 0 && this.findTorches();
    }

    @Override
    public void startExecuting()
    {
        TorchInfo torch;
        Collections.sort(this.torches, this.sorter);
        torch = this.torches.get(0);

        if (torch.lit)
        {
            BlockTorchLit.killBlockTorch(this.world, torch.x, torch.y, torch.z, "fire.fire", 1F);
        }
        else
        {
            BlockTorchUnlit.igniteBlockTorch(0, this.world, torch.x, torch.y, torch.z, "fire.fire");
        }

        this.delay = 20;
        this.el.getLookHelper().setLookPosition(torch.x + 0.5, torch.y + 0.5, torch.z + 0.5, 10F, this.el.getVerticalFaceSpeed());
        this.torches.clear();
    }

    private boolean findTorches()
    {
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
                    int id = this.world.getBlockId(x, y, z);

                    if ((id == 50 && this.world.isDaytime()) ||
                        (id == ConfigCommon.blockIdTorchUnlit && !this.world.isDaytime()))
                    {
                        TorchInfo torch = new TorchInfo(x, y, z, id == 50);

                        if (EntityAIHelper.canEntitySeeTorch(this.el, torch, r))
                        {
                            this.torches.add(torch);
                        }
                    }
                }
            }
        }

        return !this.torches.isEmpty();
    }
}
