package pelep.unlittorch.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.entity.TileEntityLantern;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockLanternLit extends BlockLantern
{
    public BlockLanternLit()
    {
        super(ConfigCommon.blockIdLanternLit, true);
        this.setLightValue(1F);
        this.setUnlocalizedName("ut_lanternLit");
        this.func_111022_d("unlittorch:lantern_on");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand)
    {
        int r = rand.nextInt(8);
        
        if (r < 5)
        {
            double fx = x + 0.5D;
            double fy = y + 0.2D;
            double fz = z + 0.5D;
            
            double hos = 0.15D;
            double dos = 0.35D;
            
            switch (world.getBlockMetadata(x, y, z))
            {
            case 0:
                fx -= dos;
                fy += hos;
                break;
            case 1:
                fx += dos;
                fy += hos;
                break;
            case 2:
                fy += hos;
                fz -= dos;
                break;
            case 3:
                fy += hos;
                fz += dos;
                break;
            case 4: case 5: case 6: case 7:
                fy += (hos + 0.1D);
                break;
            }
            
            world.spawnParticle("flame", fx, fy, fz, 0D, 0D, 0D);
            if (r == 0) world.spawnParticle("smoke", fx, fy, fz, 0D, 0D, 0D);
        }
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int side, float i, float j, float k)
    {
        if (!this.activateBlock(world, x, y, z, p, side, i, j, k))
        {
            TileEntityLantern te = getTileEntityLantern(world, x, y, z);
            int age = te.getAge();
            boolean handle = te.hasHandle();
            
            world.setBlock(x, y, z, ConfigCommon.blockIdLanternUnlit, world.getBlockMetadata(x, y, z), 3);
            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "fire.fire", 1F, world.rand.nextFloat() * 0.4F + 1.5F);
            
            te = getTileEntityLantern(world, x, y, z);
            te.setAge(age);
            te.setHandle(handle);
        }
        
        return true;
    }
}
