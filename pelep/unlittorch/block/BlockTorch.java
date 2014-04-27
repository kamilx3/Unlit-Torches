package pelep.unlittorch.block;

import static net.minecraftforge.common.ForgeDirection.*;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import pelep.pcl.util.vec.Coordinates;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.tileentity.TileEntityTorch;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author pelep
 */
abstract class BlockTorch extends BlockContainer
{
    private final boolean lit;

    public BlockTorch(int id, boolean lit)
    {
        super(id, Material.circuits);
        setTickRandomly(true);
        setCreativeTab(CreativeTabs.tabDecorations);
        setHardness(0F);
        setStepSound(soundWoodFootstep);
        this.lit = lit;
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


    //--------------------------------container-------------------------------//


    @Override
    public TileEntity createTileEntity(World world, int md)
    {
        return new TileEntityTorch(lit);
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityTorch(lit);
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
        float adj = 0.15F;

        switch (world.getBlockMetadata(x, y, z))
        {
            case 1:
                setBlockBounds(0F, 0.2F, 0.5F - adj, adj * 2F, 0.8F, 0.5F + adj);
                break;
            case 2:
                setBlockBounds(1F - adj * 2F, 0.2F, 0.5F - adj, 1F, 0.8F, 0.5F + adj);
                break;
            case 3:
                setBlockBounds(0.5F - adj, 0.2F, 0F, 0.5F + adj, 0.8F, adj * 2F);
                break;
            case 4:
                setBlockBounds(0.5F - adj, 0.2F, 1F - adj * 2F, 0.5F + adj, 0.8F, 1F);
                break;
            default:
                adj = 0.1F;
                setBlockBounds(0.5F - adj, 0F, 0.5F - adj, 0.5F + adj, 0.6F, 0.5F + adj);
        }
    }


    //---------------------------------update---------------------------------//


    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        onBlockAdded(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int oldid)
    {
        if (world.getBlockId(x, y, z) != blockID) return;

        int md = world.getBlockMetadata(x, y, z);
        boolean keep;

        switch (md)
        {
            case 1:
                keep = world.isBlockSolidOnSide(x - 1, y, z, EAST, true);
                break;
            case 2:
                keep = world.isBlockSolidOnSide(x + 1, y, z, WEST, true);
                break;
            case 3:
                keep = world.isBlockSolidOnSide(x, y, z - 1, SOUTH, true);
                break;
            case 4:
                keep = world.isBlockSolidOnSide(x, y, z + 1, NORTH, true);
                break;
            case 5:
                keep = canPlaceTorchOn(world, x, y - 1, z);
                break;
            default:
                md = getPossibleMetadata(world, x, y, z);
                if (keep = md != 0) world.setBlockMetadataWithNotify(x, y, z, md, 1|2);
        }

        if (!keep)
        {
            dropBlockAsItem(world, x, y, z, md, 0);
            world.setBlockToAir(x, y, z);
        }
    }


    //----------------------------------place---------------------------------//


    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase p, ItemStack ist)
    {
        TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
        te.age = ist.getItemDamage();
        te.eternal = ist.stackTagCompound != null;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return getPossibleMetadata(world, x, y, z) != 0;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float i, float j, float k, int d)
    {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        Coordinates offset = new Coordinates(x, y, z).offset(dir.getOpposite());

        if (side == 1 && canPlaceTorchOn(world, offset.x, offset.y, offset.z))
        {
            return 5;
        }
        else if (side < 2 || !world.isBlockSolidOnSide(offset.x, offset.y, offset.z, dir, true))
        {
            return getPossibleMetadata(world, x, y, z);
        }

        switch (dir)
        {
            case EAST: return 1;
            case WEST: return 2;
            case SOUTH: return 3;
            case NORTH: return 4;
            default: return 0;
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        int md = getPossibleMetadata(world, x, y, z);

        if (md != 0 && world.getBlockMetadata(x, y, z) < 1)
        {
            world.setBlockMetadataWithNotify(x, y, z, md, 1|2);
        }
        else if (md == 0 && world.getBlockId(x, y, z) == blockID)
        {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }


    //----------------------------------drop----------------------------------//


    @Override
    public boolean removeBlockByPlayer(World world, EntityPlayer p, int x, int y, int z)
    {
        TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
        int age = te.age;
        boolean eternal = te.eternal;
        boolean drop = world.setBlockToAir(x, y, z);

        if (drop && !world.isRemote && (p == null || !p.capabilities.isCreativeMode))
        {
            int id = ConfigCommon.torchDropsUnlit ? ConfigCommon.blockIdTorchUnlit : blockID;
            ItemStack ist = new ItemStack(id, 1, age);
            ist.setTagCompound(eternal ? new NBTTagCompound() : null);
            dropBlockAsItem_do(world, x, y, z, ist);
        }

        return drop;
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int md, int fortune)
    {
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        TileEntity te = world.getBlockTileEntity(x, y, z);

        if (te != null)
        {
            TileEntityTorch tt = (TileEntityTorch) te;
            int id = ConfigCommon.torchDropsUnlit ? ConfigCommon.blockIdTorchUnlit : blockID;
            ItemStack ist = new ItemStack(id, 1, tt.age);
            ist.setTagCompound(tt.eternal ? new NBTTagCompound() : null);
            stacks.add(ist);
        }

        return stacks;
    }


    //----------------------------------util----------------------------------//


    private static int getPossibleMetadata(World world, int x, int y, int z)
    {
        if (canPlaceTorchOn(world, x, y - 1, z)) return 5;
        if (world.isBlockSolidOnSide(x, y, z + 1, NORTH, true)) return 4;
        if (world.isBlockSolidOnSide(x, y, z - 1, SOUTH, true)) return 3;
        if (world.isBlockSolidOnSide(x + 1, y, z, WEST, true)) return 2;
        if (world.isBlockSolidOnSide(x - 1, y, z, EAST, true)) return 1;
        return 0;
    }

    private static boolean canPlaceTorchOn(World world, int x, int y, int z)
    {
        if (world.doesBlockHaveSolidTopSurface(x, y, z)) return true;
        int id = world.getBlockId(x, y, z);
        return blocksList[id] != null && blocksList[id].canPlaceTorchOnTop(world, x, y, z);
    }

    protected void grabBlock(World world, int x, int y, int z, EntityPlayer ep)
    {
        if (world.isRemote) return;
        TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
        ItemStack torch = new ItemStack(blockID, 1, te.age);
        torch.setTagCompound(te.eternal ? new NBTTagCompound() : null);
        ep.inventory.setInventorySlotContents(ep.inventory.currentItem, torch);
        world.setBlockToAir(x, y, z);
    }

    public static void consumeItem(int slot, EntityPlayer ep, int amnt)
    {
        if (!ep.worldObj.isRemote && !ep.capabilities.isCreativeMode)
            ep.inventory.decrStackSize(slot, amnt);
    }
}
