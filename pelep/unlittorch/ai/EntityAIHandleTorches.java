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

public class EntityAIHandleTorches extends EntityAIBase
{
    private EntityLiving el;
    private World world;

    private ArrayList<TorchCoordinates> lit = new ArrayList();
    private ArrayList<TorchCoordinates> unlit = new ArrayList();
    private TorchSorter sorter;
    
    public EntityAIHandleTorches(EntityLiving el)
    {
        this.el = el;
        this.world = el.worldObj;
        this.sorter = new TorchSorter(el);
    }
    
    @Override
    public boolean shouldExecute()
    {
        this.findTorches();
        
        if (this.world.isDaytime())
        {
            return !this.lit.isEmpty();
        }
        else
        {
            return !this.unlit.isEmpty();
        }
    }
    
    @Override
    public void startExecuting()
    {
        TorchCoordinates torch;
        
        if (this.world.isDaytime())
        {
            Collections.sort(this.lit, this.sorter);
            torch = this.lit.get(0);
            BlockTorch.killBlockTorch(this.world, torch.x, torch.y, torch.z, "fire.fire", 1F);
        }
        else
        {
            Collections.sort(this.unlit, this.sorter);
            torch = this.unlit.get(0);
            BlockTorch.igniteBlockTorch(1, this.world, torch.x, torch.y, torch.z, "fire.fire");
        }
        
        this.el.getLookHelper().setLookPosition(torch.x + 0.5, torch.y + 0.5, torch.z + 0.5, 10F, this.el.getVerticalFaceSpeed());
        
        this.lit.clear();
        this.unlit.clear();
    }
    
    private void findTorches()
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
                        TorchCoordinates torch = new TorchCoordinates(x, y, z);
                        
                        if (EntityAIHelper.canEntitySeeTorch(this.el, torch, r))
                        {
                            if (this.world.getBlockMetadata(x, y, z) < 6)
                            {
                                this.lit.add(torch);
                            }
                            else
                            {
                                this.unlit.add(torch);
                            }
                        }
                    }
                }
            }
        }
    }
}
