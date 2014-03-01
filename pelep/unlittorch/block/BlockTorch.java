package pelep.unlittorch.block;

import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.WEST;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.packet.Packet03UpdateTile;
import pelep.unlittorch.tileentity.TileEntityTorch;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author pelep
 */
class BlockTorch extends BlockContainer
{
    private final boolean lit;

    public BlockTorch(int id, boolean lit)
    {
        super(id, Material.circuits);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHardness(0F);
        this.setStepSound(soundWoodFootstep);
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

    @Override
    public int getRenderType()
    {
        return 2;
    }


    //--------------------------------container-------------------------------//


    @Override
    public TileEntity createTileEntity(World world, int md)
    {
        return new TileEntityTorch(this.lit, 0);
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityTorch(this.lit, 0);
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
                this.setBlockBounds(0F, 0.2F, 0.5F - adj, adj * 2F, 0.8F, 0.5F + adj);
                break;
            case 2:
                this.setBlockBounds(1F - adj * 2F, 0.2F, 0.5F - adj, 1F, 0.8F, 0.5F + adj);
                break;
            case 3:
                this.setBlockBounds(0.5F - adj, 0.2F, 0F, 0.5F + adj, 0.8F, adj * 2F);
                break;
            case 4:
                this.setBlockBounds(0.5F - adj, 0.2F, 1F - adj * 2F, 0.5F + adj, 0.8F, 1F);
                break;
            default:
                adj = 0.1F;
                this.setBlockBounds(0.5F - adj, 0F, 0.5F - adj, 0.5F + adj, 0.6F, 0.5F + adj);
        }
    }


    //---------------------------------update---------------------------------//


    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        super.updateTick(world, x, y, z, rand);
        this.onBlockAdded(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId)
    {
        if (this.canPlaceTorch(world, x, y, z))
        {
            int md = world.getBlockMetadata(x, y, z);
            boolean keep = true;

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
                    keep = this.canPlaceTorchOn(world, x, y - 1, z);
            }

            if (!keep)
            {
                this.dropBlockAsItem(world, x, y, z, md, 0);
                world.setBlockToAir(x, y, z);
            }
        }
    }


    //----------------------------------place---------------------------------//


    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase p, ItemStack ist)
    {
        setTileEntityAge(ist.getItemDamage(), world, x, y, z, null);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return world.isBlockSolidOnSide(x - 1, y, z, EAST, true) ||
                world.isBlockSolidOnSide(x + 1, y, z, WEST, true) ||
                world.isBlockSolidOnSide(x, y, z - 1, SOUTH, true) ||
                world.isBlockSolidOnSide(x, y, z + 1, NORTH, true) ||
                this.canPlaceTorchOn(world, x, y - 1, z);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float i, float j, float k, int d)
    {
        int md = 5;

        if (side == 5 && world.isBlockSolidOnSide(x - 1, y, z, EAST, true))
        {
            md = 1;
        }
        else if (side == 4 && world.isBlockSolidOnSide(x + 1, y, z, WEST, true))
        {
            md = 2;
        }
        else if (side == 3 && world.isBlockSolidOnSide(x, y, z - 1, SOUTH, true))
        {
            md = 3;
        }
        else if (side == 2 && world.isBlockSolidOnSide(x, y, z + 1, NORTH, true))
        {
            md = 4;
        }
        else if (side == 1 && this.canPlaceTorchOn(world, x, y - 1, z))
        {
            md = 5;
        }
        else
        {
            if (world.isBlockSolidOnSide(x - 1, y, z, EAST, true))
            {
                md = 1;
            }
            else if (world.isBlockSolidOnSide(x + 1, y, z, WEST, true))
            {
                md = 2;
            }
            else if (world.isBlockSolidOnSide(x, y, z - 1, SOUTH, true))
            {
                md = 3;
            }
            else if (world.isBlockSolidOnSide(x, y, z + 1, NORTH, true))
            {
                md = 4;
            }
            else if (this.canPlaceTorchOn(world, x, y - 1, z))
            {
                md = 5;
            }
        }

        return md;
    }

    private boolean canPlaceTorchOn(World world, int x, int y, int z)
    {
        if (world.doesBlockHaveSolidTopSurface(x, y, z))
        {
            return true;
        }
        else
        {
            int id = world.getBlockId(x, y, z);
            return blocksList[id] != null && blocksList[id].canPlaceTorchOnTop(world, x, y, z);
        }
    }

    private boolean canPlaceTorch(World world, int x, int y, int z)
    {
        if (!this.canPlaceBlockAt(world, x, y, z))
        {
            if (world.getBlockId(x, y, z) == this.blockID)
            {
                this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
                world.setBlockToAir(x, y, z);
            }

            return false;
        }

        return true;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        if (world.getBlockMetadata(x, y, z) < 1)
        {
            if (world.isBlockSolidOnSide(x - 1, y, z, EAST, true))
            {
                world.setBlockMetadataWithNotify(x, y, z, 1, 3);
            }
            else if (world.isBlockSolidOnSide(x + 1, y, z, WEST, true))
            {
                world.setBlockMetadataWithNotify(x, y, z, 2, 3);
            }
            else if (world.isBlockSolidOnSide(x, y, z - 1, SOUTH, true))
            {
                world.setBlockMetadataWithNotify(x, y, z, 3, 3);
            }
            else if (world.isBlockSolidOnSide(x, y, z + 1, NORTH, true))
            {
                world.setBlockMetadataWithNotify(x, y, z, 4, 3);
            }
            else if (this.canPlaceTorchOn(world, x, y - 1, z))
            {
                world.setBlockMetadataWithNotify(x, y, z, 5, 3);
            }
        }

        this.canPlaceTorch(world, x, y, z);
    }


    //----------------------------------drop----------------------------------//


    @Override
    public boolean removeBlockByPlayer(World world, EntityPlayer p, int x, int y, int z)
    {
        int age = ((TileEntityTorch)world.getBlockTileEntity(x, y, z)).getAge();
        boolean drop = world.setBlockToAir(x, y, z);

        //TODO figure out when p can be null and fix if needed
        if (drop && !world.isRemote && (p == null || !p.capabilities.isCreativeMode))
        {
            int id = ConfigCommon.torchDropsUnlit ? ConfigCommon.blockIdTorchUnlit : this.blockID;
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(id, 1, age));
        }

        return drop;
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int md, int fortune)
    {
        ArrayList<ItemStack> stacks = new ArrayList();

        TileEntity te = world.getBlockTileEntity(x, y, z);

        if (te != null)
        {
            int id = ConfigCommon.torchDropsUnlit ? ConfigCommon.blockIdTorchUnlit : this.blockID;
            stacks.add(new ItemStack(id, 1, ((TileEntityTorch)te).getAge()));
        }

        return stacks;
    }


    //----------------------------------mine----------------------------------//


    protected static void setTileEntityAge(int age, World world, int x, int y, int z, String sound)
    {
        setTileEntityAge(age, world, x, y , z, sound, 1F);
    }

    protected static void setTileEntityAge(int age, World world, int x, int y, int z, String sound, float volume)
    {
        TileEntityTorch te = (TileEntityTorch) world.getBlockTileEntity(x, y, z);
        te.setAge(age);

        if (sound != null)
        {
            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, sound, volume, world.rand.nextFloat() * 0.4F + 0.8F);
        }

        if (!world.isRemote)
        {
            int dim = world.provider.dimensionId;
            PacketDispatcher.sendPacketToAllInDimension(new Packet03UpdateTile(x, y, z, dim, age).create(), dim);
        }
    }
}
