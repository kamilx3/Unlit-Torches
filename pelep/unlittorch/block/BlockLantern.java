package pelep.unlittorch.block;

import static net.minecraftforge.common.ForgeDirection.DOWN;
import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.WEST;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.entity.TileEntityLantern;
import pelep.unlittorch.handler.PacketSender;
import pelep.unlittorch.proxy.ProxyCommon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLantern extends BlockContainer
{
    protected boolean lit;
    
    protected BlockLantern(int id, boolean lit)
    {
        super(id, Material.cake);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHardness(0F);
        this.setStepSound(soundMetalFootstep);
        this.setTickRandomly(true);
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
        return ProxyCommon.RID_HOOK;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public String getItemIconName()
    {
        return this.field_111026_f;
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
        float adj = 0.3F;
        
        switch (md)
        {
        case 0:
            this.setBlockBounds(0F, 0.1F, adj, 0.95F - adj * 2F, 0.85F, 1F - adj);
            break;
        case 1:
            this.setBlockBounds(0.05F + adj * 2F, 0.1F, adj, 1F, 0.85F, 1F - adj);
            break;
        case 2:
            this.setBlockBounds(adj, 0.1F, 0F, 1F - adj, 0.85F, 0.95F - adj * 2F);
            break;
        case 3:
            this.setBlockBounds(adj, 0.1F, 0.05F + adj * 2F, 1F - adj, 0.85F, 1F);
            break;
        case 4: case 5: case 6: case 7:
            this.setBlockBounds(adj, 0.25F, adj, 1F - adj, 1F, 1F - adj);
            break;
        default:
            this.setBlockBounds(adj, 0F, adj, 1F - adj, 0.5F, 1F - adj);
        }
    }
    
    
    //--------------------------------container-------------------------------//
    
    
    @Override
    public TileEntity createTileEntity(World world, int md)
    {
        return new TileEntityLantern(this.lit, 0);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityLantern(this.lit, 0);
    }
    
    
    //----------------------------------drop----------------------------------//
    
    
    @Override
    public boolean removeBlockByPlayer(World world, EntityPlayer p, int x, int y, int z)
    {
        TileEntityLantern te = getTileEntityLantern(world, x, y, z);
        int age = te.getAge();
        boolean handle = te.hasHandle();
        boolean drop = world.setBlockToAir(x, y, z);
        
        if (drop && !world.isRemote && !p.capabilities.isCreativeMode)
        {
            NBTTagCompound tag = new NBTTagCompound();
            ItemStack lantern = new ItemStack(this.blockID, 1, age);
            
            tag.setBoolean("handle", handle);
            lantern.setTagCompound(tag);
            
            this.dropBlockAsItem_do(world, x, y, z, lantern);
        }
        
        return drop;
    }
    
    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int md)
    {
        if (md < 8) world.setBlock(x, y, z, ConfigCommon.blockIdLanternHook, md, 3);
    }
    
    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int md, int fortune)
    {
        ArrayList<ItemStack> stacks = new ArrayList();
        TileEntityLantern te = getTileEntityLantern(world, x, y, z);
        
        if (te == null)
        {
            return stacks;
        }
        
        NBTTagCompound tag = new NBTTagCompound();
        ItemStack lantern = new ItemStack(this.blockID, 1, te.getAge());
        
        tag.setBoolean("handle", te.hasHandle());
        lantern.setTagCompound(tag);
        
        stacks.add(lantern);
        if (md < 8) stacks.add(new ItemStack(ConfigCommon.blockIdLanternHook, 1, 0));
        
        return stacks;
    }
    
    
    //---------------------------------update---------------------------------//
    
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        super.updateTick(world, x, y, z, rand);
        this.keepOrDropLantern(world, x, y, z);
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId)
    {
        this.keepOrDropLantern(world, x, y, z);
    }
    
    
    //---------------------------------place----------------------------------//
    
    
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return world.getBlockId(x, y, z) == ConfigCommon.blockIdLanternHook || canPlaceLanternOn(world, x, y - 1, z);
    }
    
    public static boolean canPlaceLanternOn(World world, int x, int y, int z)
    {
        if (world.doesBlockHaveSolidTopSurface(x, y, z))
        {
            return true;
        }
        else
        {
            int id = world.getBlockId(x, y, z);
            return Block.blocksList[id] != null && Block.blocksList[id].canPlaceTorchOnTop(world, x, y, z);
        }
    }
    
    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float i, float j, float k, int d)
    {
        int md = 8;
        
        if (world.getBlockId(x, y, z) == ConfigCommon.blockIdLanternHook)
        {
            md = world.getBlockMetadata(x, y, z);
        }
        
        return md;
    }
    
    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        this.keepOrDropLantern(world, x, y, z);
    }
    
    private void keepOrDropLantern(World world, int x, int y, int z)
    {
        int md = world.getBlockMetadata(x, y, z);
        boolean keep;
        
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
            break;
        default:
            keep = canPlaceLanternOn(world, x, y - 1, z);
        }
        
        if (!keep && world.getBlockId(x, y, z) == this.blockID)
        {
            this.dropBlockAsItem(world, x, y, z, md, 0);
            world.setBlockToAir(x, y, z);
        }
    }
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase p, ItemStack ist)
    {
        TileEntityLantern te = getTileEntityLantern(world, x, y, z);
        
        if (te != null)
        {
            te.setAge(ist.getItemDamage());
            te.setHandle(ist.stackTagCompound != null && ist.stackTagCompound.getBoolean("handle"));
        }
        
        int md = world.getBlockMetadata(x, y, z);
        
        if (md == 8)
        {
            switch (MathHelper.floor_double((p.rotationYaw * 4F / 360F) + 0.5D) & 3)
            {
            case 0:
                md = 11;
                break;
            case 1:
                md = 8;
                break;
            case 2:
                md = 10;
                break;
            case 3:
                md = 9;
            }
            
            world.setBlockMetadataWithNotify(x, y, z, md, 3);
        }
    }
    
    
    //----------------------------------mine----------------------------------//
    
    
    protected static TileEntityLantern getTileEntityLantern(World world, int x, int y, int z)
    {
        int id =  world.getBlockId(x, y, z);
        
        if (id == ConfigCommon.blockIdLanternLit || id == ConfigCommon.blockIdLanternUnlit)
        {
            TileEntity te = world.getBlockTileEntity(x, y, z);
            
            if (te == null || !(te instanceof TileEntityLantern))
            {
                te = new TileEntityLantern(true, ConfigCommon.lanternLifespanMax / 2);
                world.setBlockTileEntity(x, y, z, te);
            }
            
            return (TileEntityLantern) te;
        }
        
        return null;
    }

    protected boolean activateBlock(World world, int x, int y, int z, EntityPlayer p, int side, float i, float j, float k)
    {
        ItemStack ist = p.getCurrentEquippedItem();
        TileEntityLantern te = getTileEntityLantern(world, x, y, z);
        
        if (ist == null && p.isSneaking())
        {
            int md = world.getBlockMetadata(x, y, z);
            
            if (md < 8)
            {
                world.setBlock(x, y, z, ConfigCommon.blockIdLanternHook, md, 3);
            }
            else
            {
                world.setBlockToAir(x, y, z);
            }
            
            NBTTagCompound tag = new NBTTagCompound();
            ItemStack lantern = new ItemStack(this.blockID, 1, te.getAge());
            
            tag.setBoolean("handle", te.hasHandle());
            lantern.setTagCompound(tag);
            
            p.inventory.setInventorySlotContents(p.inventory.currentItem, lantern);
            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.break", 0.5F, 2F);
            
            return true;
        }
        
        if (ist != null && ist.itemID == ConfigCommon.itemIdLanternFuel)
        {
            int d = ist.getItemDamage();
            
            if (d == 3 && !te.hasHandle())
            {
                te.setHandle(true);
                world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.break", 0.5F, 3F);
                
                if (!p.capabilities.isCreativeMode)
                {
                    p.inventory.decrStackSize(p.inventory.currentItem, 1);
                }
                
                return true;
            }
            else if (te.getAge() > 0)
            {
                if (d == 0)
                {
                    te.setAge(te.getAge() - ConfigCommon.lanternFuelFat);
                    
                    if (!p.capabilities.isCreativeMode)
                    {
                        p.inventory.decrStackSize(p.inventory.currentItem, 1);
                    }
                }
                else if (d == 1 || d == 2)
                {
                    if (d == 1)
                    {
                        te.setAge(te.getAge() - (ConfigCommon.lanternFuelFat * 3));
                    }
                    else
                    {
                        te.setAge(te.getAge() - ConfigCommon.lanternFuelOil);
                    }
                    
                    if (!p.capabilities.isCreativeMode)
                    {
                        ItemStack bottle = new ItemStack(Item.glassBottle, 1, 0);
                        
                        if (--ist.stackSize <= 0)
                        {
                            p.inventory.setInventorySlotContents(p.inventory.currentItem, bottle);
                            PacketSender.sendInventoryPacket(p, (byte)1, (byte)p.inventory.currentItem);
                        }
                        else if (p.inventory.addItemStackToInventory(bottle))
                        {
                            world.playSoundAtEntity(p, "random.pop", 1F, world.rand.nextFloat() * 0.4F + 1.5F);
                            PacketSender.sendInventoryPacket(p, (byte)1, (byte)-1);
                        }
                        else
                        {
                            p.dropPlayerItemWithRandomChoice(bottle, true).delayBeforeCanPickup = 10;
                        }
                    }
                }
                
                return true;
            }
        }
        
        return false;
    }
}
