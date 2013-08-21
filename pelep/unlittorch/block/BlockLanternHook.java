package pelep.unlittorch.block;

import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.WEST;
import static net.minecraftforge.common.ForgeDirection.DOWN;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.entity.TileEntityLantern;
import pelep.unlittorch.proxy.ProxyCommon;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLanternHook extends Block
{
    public static Icon icon_hook;
    
    public BlockLanternHook()
    {
        super(ConfigCommon.blockIdLanternHook, Material.cake);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHardness(0.5F);
        this.setStepSound(soundWoodFootstep);
        this.setTickRandomly(true);
        this.setUnlocalizedName("ut_hook");
        this.func_111022_d("unlittorch:hook_item");
    }
    
    
    //--------------------------------rendering-------------------------------//
    
    
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    @Override
    public int getRenderType()
    {
        return ProxyCommon.RID_HOOK;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister ir)
    {
        this.blockIcon = ir.registerIcon("unlittorch:hook_item");
        icon_hook = ir.registerIcon("unlittorch:hook_block");
    }
    
    
    //--------------------------------raytrace--------------------------------//
    
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return null;
    }
    
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        int md = world.getBlockMetadata(x, y, z);
        float adj = 0.15F;
        
        switch (md)
        {
        case 0:
            this.setBlockBounds(0F, 0.6F, 0.5F - adj, adj + 0.05F, 0.9F, 0.5F + adj);
            break;
        case 1:
            this.setBlockBounds(1F - adj - 0.05F, 0.6F, 0.5F - adj, 1F, 0.9F, 0.5F + adj);
            break;
        case 2:
            this.setBlockBounds(0.5F - adj, 0.6F, 0F, 0.5F + adj, 0.9F, adj + 0.05F);
            break;
        case 3:
            this.setBlockBounds(0.5F - adj, 0.6F, 1F - adj - 0.05F, 0.5F + adj, 0.9F, 1F);
            break;
        default:
            this.setBlockBounds(0.5F - adj, 0.8F, 0.5F - adj, 0.5F + adj, 1F, 0.5F + adj);
        }
    }
    
    
    //---------------------------------update---------------------------------//
    
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        super.updateTick(world, x, y, z, rand);
        this.dropOrKeepHook(world, x, y, z);
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId)
    {
        this.dropOrKeepHook(world, x, y, z);
    }
    
    
    //---------------------------------place----------------------------------//
    
    
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return  world.isBlockSolidOnSide(x - 1, y, z, EAST, true) ||
                world.isBlockSolidOnSide(x + 1, y, z, WEST, true) ||
                world.isBlockSolidOnSide(x, y, z - 1, SOUTH, true) ||
                world.isBlockSolidOnSide(x, y, z + 1, NORTH, true) ||
                world.isBlockSolidOnSide(x, y + 1, z, DOWN, true) ||
                world.getBlockId(x, y + 1, z) == fence.blockID;
    }
    
    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float i, float j, float k, int d)
    {
        int md = 0;
        
        if (side == 5 && world.isBlockSolidOnSide(x - 1, y, z, EAST, true))
        {
            md = 0;
        }
        else if (side == 4 && world.isBlockSolidOnSide(x + 1, y, z, WEST, true))
        {
            md = 1;
        }
        else if (side == 3 && world.isBlockSolidOnSide(x, y, z - 1, SOUTH, true))
        {
            md = 2;
        }
        else if (side == 2 && world.isBlockSolidOnSide(x, y, z + 1, NORTH, true))
        {
            md = 3;
        }
        else if (side == 0 && (world.isBlockSolidOnSide(x, y + 1, z, DOWN, true) || world.getBlockId(x, y + 1, z) == fence.blockID))
        {
            md = 4;
        }
        else
        {
            if (world.isBlockSolidOnSide(x - 1, y, z, EAST, true))
            {
                md = 0;
            }
            else if (world.isBlockSolidOnSide(x + 1, y, z, WEST, true))
            {
                md = 1;
            }
            else if (world.isBlockSolidOnSide(x, y, z - 1, SOUTH, true))
            {
                md = 2;
            }
            else if (world.isBlockSolidOnSide(x, y, z + 1, NORTH, true))
            {
                md = 3;
            }
            else if (world.isBlockSolidOnSide(x, y + 1, z, DOWN, true) || world.getBlockId(x, y + 1, z) == fence.blockID)
            {
                md = 4;
            }
        }
        
        return md;
    }
    
    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        this.dropOrKeepHook(world, x, y, z);
    }
    
    private void dropOrKeepHook(World world, int x, int y, int z)
    {
        int md = world.getBlockMetadata(x, y, z);
        boolean keep = false;
        
        switch (md)
        {
        case 0:
            keep = world.isBlockSolidOnSide(x - 1, y, z, EAST, true);
            break;
        case 1:
            keep = world.isBlockSolidOnSide(x + 1, y, z, WEST, true);
            break;
        case 2:
            keep = world.isBlockSolidOnSide(x, y, z - 1, SOUTH, true);
            break;
        case 3:
            keep = world.isBlockSolidOnSide(x, y, z + 1, NORTH, true);
            break;
        case 4: case 5: case 6: case 7:
            keep = world.isBlockSolidOnSide(x, y + 1, z, DOWN, true) || world.getBlockId(x, y + 1, z) == fence.blockID;
        }
        
        if (!keep)
        {
            this.dropBlockAsItem(world, x, y, z, md, 0);
            world.setBlockToAir(x, y, z);
        }
    }
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase p, ItemStack ist)
    {
        int md = world.getBlockMetadata(x, y, z);
        
        if (md == 4)
        {
            switch (MathHelper.floor_double((p.rotationYaw * 4F / 360F) + 0.5D) & 3)
            {
            case 0:
                md = 7;
                break;
            case 1:
                md = 4;
                break;
            case 2:
                md = 6;
                break;
            case 3:
                md = 5;
            }
            
            world.setBlockMetadataWithNotify(x, y, z, md, 3);
        }
    }
    
    
    //-------------------------------interact--------------------------------//
    
    
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e)
    {
        if (!world.isRemote && !e.onGround && e instanceof EntityItem && world.rand.nextInt(8) == 0)
        {
            EntityItem ei = (EntityItem) e;
            
            if (ei.delayBeforeCanPickup > 10)
            {
                ItemStack ist = ei.getEntityItem();
                int id = ist.itemID;
                
                if ((id == ConfigCommon.blockIdLanternLit || id == ConfigCommon.blockIdLanternUnlit) && ist.stackTagCompound != null && ist.stackTagCompound.getBoolean("handle"))
                {
                    if (world.rand.nextInt(4) == 0)
                    {
                        EntityItem l = new EntityItem(world);
                        EntityItem h = new EntityItem(world);
                        
                        l.copyDataFrom(ei, true);
                        l.getEntityItem().stackTagCompound.setBoolean("handle", false);
                        l.delayBeforeCanPickup = ei.delayBeforeCanPickup;
                        h.copyDataFrom(ei, true);
                        h.setEntityItemStack(new ItemStack(ConfigCommon.itemIdLanternFuel, 1, 3));
                        h.delayBeforeCanPickup = ei.delayBeforeCanPickup;
                        
                        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.break", 1F, 1.5F);
                        world.spawnEntityInWorld(l);
                        world.spawnEntityInWorld(h);
                    }
                    else
                    {
                        world.setBlock(x, y, z, id, world.getBlockMetadata(x, y, z), 3);
                        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.break", 0.8F, 2F);
                        
                        TileEntityLantern te = this.getTileEntityLantern(world, x, y, z);
                        
                        if (te != null)
                        {
                            te.setAge(ei.getEntityItem().getItemDamage());
                        }
                    }
                    
                    ei.setDead();
                }
            }
        }
    }
    
    
    //----------------------------------mine----------------------------------//
    
    
    private TileEntityLantern getTileEntityLantern(World world, int x, int y, int z)
    {
        int id =  world.getBlockId(x, y, z);
        
        if (id != ConfigCommon.blockIdLanternLit && id != ConfigCommon.blockIdLanternUnlit)
        {
            return null;
        }
        
        TileEntity te = world.getBlockTileEntity(x, y, z);
        
        if (te == null || !(te instanceof TileEntityLantern))
        {
            te = new TileEntityLantern(true, ConfigCommon.lanternLifespanMax / 2);
            world.setBlockTileEntity(x, y, z, te);
        }
        
        return (TileEntityLantern) te;
    }
}
