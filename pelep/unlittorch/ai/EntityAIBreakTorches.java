package pelep.unlittorch.ai;

import java.util.ArrayList;
import java.util.Collections;

import pelep.unlittorch.ai.EntityAIHelper.TorchCoordinates;
import pelep.unlittorch.ai.EntityAIHelper.TorchSorter;
import pelep.unlittorch.block.BlockTorch;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIBreakTorches extends EntityAIBase
{
    private EntityLiving el;
    private World world;
    
    private ArrayList<TorchCoordinates> torches = new ArrayList();
    private TorchSorter sorter;
    
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
        if (this.delay > 0)
        {
            this.delay--;
        }
        
        return this.delay == 0 && this.findTorch();
    }
    
    @Override
    public void startExecuting()
    {
        Collections.sort(this.torches, this.sorter);
        TorchCoordinates torch = this.torches.get(0);
        BlockTorch.killBlockTorch(this.world, torch.x, torch.y, torch.z, "fire.fire", 1F);

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
                    
                    if (this.world.getBlockId(x, y, z) == 50 && this.world.getBlockMetadata(x, y, z) < 6)
                    {
                        TorchCoordinates torch = new TorchCoordinates(x, y, z);
                        
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
