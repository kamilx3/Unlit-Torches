package pelep.unlittorch.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import pelep.unlittorch.ai.EntityAIHelper.TorchInfo;
import pelep.unlittorch.ai.EntityAIHelper.TorchSorter;
import pelep.unlittorch.block.BlockTorchLit;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author pelep
 */
public class EntityAIBreakTorches extends EntityAIBase
{
    private EntityLiving el;
    private World world;
    private TorchSorter sorter;
    private ArrayList<TorchInfo> torches = new ArrayList();
    private int delay;

    public EntityAIBreakTorches(EntityLiving el)
    {
        this.el = el;
        this.world = el.worldObj;
        this.sorter = new TorchSorter(el);
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
        Collections.sort(this.torches, this.sorter);
        TorchInfo torch = this.torches.get(0);
        BlockTorchLit.killBlockTorch(this.world, torch.x, torch.y, torch.z, "fire.fire", 1F);

        this.torches.clear();
        this.delay = 100;
        this.el.getLookHelper().setLookPosition(torch.x + 0.5, torch.y + 0.5, torch.z + 0.5, 10F, this.el.getVerticalFaceSpeed());
    }

    private boolean findTorch()
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

                    if (this.world.getBlockId(x, y, z) == 50)
                    {
                        TorchInfo torch = new TorchInfo(x, y, z);

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
