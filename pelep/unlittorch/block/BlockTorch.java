package pelep.unlittorch.block;

import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.WEST;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.entity.TileEntityTorch;
import pelep.unlittorch.handler.IgnitersHandler;
import pelep.unlittorch.handler.PacketSender;
import pelep.unlittorch.proxy.ProxyCommon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTorch extends BlockContainer
{
    private static Icon icon_unlit;
    
    public BlockTorch()
    {
        super(50, Material.circuits);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHardness(0F);
        this.setStepSound(soundWoodFootstep);
        this.setUnlocalizedName("torch");
        this.func_111022_d("torch_on");
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
        return ProxyCommon.RID_TORCH;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister ir)
    {
        this.blockIcon = ir.registerIcon("torch_on");
        icon_unlit = ir.registerIcon("unlittorch:torch_off");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int side, int md)
    {
        return md < 6 ? this.blockIcon : icon_unlit;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand)
    {
        int md = world.getBlockMetadata(x, y, z);
        
        if (md < 6)
        {
            double fx = x + 0.5D;
            double fy = y + 0.7D;
            double fz = z + 0.5D;
            
            double hos = 0.2199999988079071D;
            double dos = 0.27000001072883606D;
            
            switch (md)
            {
            case 1:
                world.spawnParticle("smoke", fx - dos, fy + hos, fz, 0D, 0D, 0D);
                world.spawnParticle("flame", fx - dos, fy + hos, fz, 0D, 0D, 0D);
                break;
            case 2:
                world.spawnParticle("smoke", fx + dos, fy + hos, fz, 0D, 0D, 0D);
                world.spawnParticle("flame", fx + dos, fy + hos, fz, 0D, 0D, 0D);
                break;
            case 3:
                world.spawnParticle("smoke", fx, fy + hos, fz - dos, 0D, 0D, 0D);
                world.spawnParticle("flame", fx, fy + hos, fz - dos, 0D, 0D, 0D);
                break;
            case 4:
                world.spawnParticle("smoke", fx, fy + hos, fz + dos, 0D, 0D, 0D);
                world.spawnParticle("flame", fx, fy + hos, fz + dos, 0D, 0D, 0D);
                break;
            default:
                world.spawnParticle("smoke", fx, fy, fz, 0D, 0D, 0D);
                world.spawnParticle("flame", fx, fy, fz, 0D, 0D, 0D);
            }
        }
    }
    
    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        Block block = blocksList[world.getBlockId(x, y, z)];
        
        if (block != null && block != this)
        {
            return block.getLightValue(world, x, y, z);
        }
        
        return world.getBlockMetadata(x, y, z) < 6 ? 14 : 0;
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
        case 1: case 6:
            this.setBlockBounds(0F, 0.2F, 0.5F - adj, adj * 2F, 0.8F, 0.5F + adj);
            break;
        case 2: case 7:
            this.setBlockBounds(1F - adj * 2F, 0.2F, 0.5F - adj, 1F, 0.8F, 0.5F + adj);
            break;
        case 3: case 8:
            this.setBlockBounds(0.5F - adj, 0.2F, 0F, 0.5F + adj, 0.8F, adj * 2F);
            break;
        case 4: case 9:
            this.setBlockBounds(0.5F - adj, 0.2F, 1F - adj * 2F, 0.5F + adj, 0.8F, 1F);
            break;
        default:
            adj = 0.1F;
            this.setBlockBounds(0.5F - adj, 0F, 0.5F - adj, 0.5F + adj, 0.6F, 0.5F + adj);
        }
    }
    
    
    //--------------------------------container-------------------------------//
    
    
    @Override
    public boolean hasTileEntity(int md)
    {
        return md < 6;
    }
    
    @Override
    public TileEntity createTileEntity(World world, int md)
    {
        return new TileEntityTorch();
    }
    
    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityTorch();
    }
    
    
    //----------------------------------drop----------------------------------//
    
    
    @Override
    public boolean removeBlockByPlayer(World world, EntityPlayer p, int x, int y, int z)
    {
        int md = getTorchMetadata(world, x, y, z);
        int age = md < 6 ? getTileEntityTorch(world, x, y, z).getAge() : 0;
        boolean drop = world.setBlockToAir(x, y, z);
        
        if (drop && !world.isRemote && md < 6 && !ConfigCommon.torchDropsUnlitLit && !p.capabilities.isCreativeMode)
        {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(this.blockID, 1, age));
        }
        
        return drop;
    }
    
    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int md, int fortune)
    {
        ArrayList<ItemStack> stacks = new ArrayList();
        int d;
        
        if (md < 6 && !ConfigCommon.torchDropsUnlitLit)
        {
            TileEntityTorch te = getTileEntityTorch(world, x, y, z);
            
            if (te == null)
            {
                return stacks;
            }
            
            d = te.getAge();
        }
        else if (md > 5 && !ConfigCommon.torchDropsUnlitUnlit)
        {
            d = 1;
        }
        else
        {
            d = 0;
        }
        
        stacks.add(new ItemStack(this.blockID, 1, d));
        
        return stacks;
    }
    
    
    //---------------------------------update---------------------------------//
    
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        super.updateTick(world, x, y, z, rand);
        
        this.onBlockAdded(world, x, y, z);
        getTileEntityTorch(world, x, y, z);
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId)
    {
        if (this.canPlaceTorch(world, x, y, z))
        {
            int md = getTorchMetadata(world, x, y, z);
            boolean keep = true;
            
            switch (md)
            {
            case 1: case 6:
                keep = world.isBlockSolidOnSide(x - 1, y, z, EAST, true);
                break;
            case 2: case 7:
                keep = world.isBlockSolidOnSide(x + 1, y, z, WEST, true);
                break;
            case 3: case 8:
                keep = world.isBlockSolidOnSide(x, y, z - 1, SOUTH, true);
                break;
            case 4: case 9:
                keep = world.isBlockSolidOnSide(x, y, z + 1, NORTH, true);
                break;
            case 5: case 10:
                keep = this.canPlaceTorchOn(world, x, y - 1, z);
            }
            
            if (!keep)
            {
                this.dropBlockAsItem(world, x, y, z, md, 0);
                world.setBlockToAir(x, y, z);
            }
        }
    }
    
    
    //---------------------------------place----------------------------------//
    
    
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return  world.isBlockSolidOnSide(x - 1, y, z, EAST, true) ||
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
            md = d == 0 ? 6 : 1;
        }
        else if (side == 4 && world.isBlockSolidOnSide(x + 1, y, z, WEST, true))
        {
            md = d == 0 ? 7 : 2;
        }
        else if (side == 3 && world.isBlockSolidOnSide(x, y, z - 1, SOUTH, true))
        {
            md = d == 0 ? 8 : 3;
        }
        else if (side == 2 && world.isBlockSolidOnSide(x, y, z + 1, NORTH, true))
        {
            md = d == 0 ? 9 : 4;
        }
        else if (side == 1 && this.canPlaceTorchOn(world, x, y - 1, z))
        {
            md = d == 0 ? 10 : 5;
        }
        else
        {
            if (world.isBlockSolidOnSide(x - 1, y, z, EAST, true))
            {
                md = d == 0 ? 6 : 1;
            }
            else if (world.isBlockSolidOnSide(x + 1, y, z, WEST, true))
            {
                md = d == 0 ? 7 : 2;
            }
            else if (world.isBlockSolidOnSide(x, y, z - 1, SOUTH, true))
            {
                md = d == 0 ? 8 : 3;
            }
            else if (world.isBlockSolidOnSide(x, y, z + 1, NORTH, true))
            {
                md = d == 0 ? 9 : 4;
            }
            else if (this.canPlaceTorchOn(world, x, y - 1, z))
            {
                md = d == 0 ? 10 : 5;
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
        int md = world.getBlockMetadata(x, y, z);
        
        if (md < 1 || 10 < md)
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
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase p, ItemStack ist)
    {
        int d = ist.getItemDamage();
        if (d > 0) setTileEntityAge(d, world, x, y, z, null);
    }
    
    
    //-------------------------------interact--------------------------------//
    
    
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e)
    {
        if (!world.isRemote)
        {
            int md = getTorchMetadata(world, x, y, z);
            
            if (!e.onGround && e instanceof EntityArrow && (Math.abs(e.motionX) > 1 || Math.abs(e.motionY) > 1 || Math.abs(e.motionZ) > 1))
            {
                if (world.rand.nextInt(3) == 0)
                {
                    if (md < 6 && !ConfigCommon.torchDropsUnlitLit)
                    {
                        TileEntityTorch te = getTileEntityTorch(world, x, y, z);
                        md = te != null ? te.getAge() : (ConfigCommon.torchLifespanMax / 3);
                    }
                    else if (md > 5 && !ConfigCommon.torchDropsUnlitUnlit)
                    {
                        md = 1;
                    }
                    else
                    {
                        md = 0;
                    }
                    
                    this.dropBlockAsItem_do(world, x, y, z, new ItemStack(this.blockID, 1, md));
                    world.playAuxSFX(2001, x, y, z, this.blockID + (md << 12));
                    world.setBlockToAir(x, y, z);
                }
                else if (e.isBurning())
                {
                    if (md < 6)
                    {
                        TileEntityTorch te = getTileEntityTorch(world, x, y, z);
                        int age = te != null ? te.getAge() : (ConfigCommon.torchLifespanMax / 3);
                        age -= (ConfigCommon.torchLifespanMax / 3);
                        
                        setTileEntityAge(Math.max(age, 1), world, x, y, z, "fire.fire");
                    }
                    else
                    {
                        igniteBlockTorch((ConfigCommon.torchLifespanMax / 3), world, x, y, z, "fire.fire");
                    }
                }
                else if (md < 6)
                {
                    killBlockTorch(world, x, y, z, "fire.fire", 1F);
                }
            }
            else if (md > 5 && (e.isBurning() || e instanceof EntityBlaze || e instanceof EntityMagmaCube || e instanceof EntityFireball))
            {
                igniteBlockTorch((ConfigCommon.torchLifespanMax / 3), world, x, y, z, "fire.fire");
            }
        }
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int side, float i, float j, float k)
    {
        ItemStack ist = p.inventory.getCurrentItem();
        int md = getTorchMetadata(world, x, y, z);
        
        if (ist == null)
        {
            md = md < 6 ? getTileEntityTorch(world, x, y, z).getAge() : 0;
            p.inventory.setInventorySlotContents(p.inventory.currentItem, new ItemStack(this.blockID, 1, md));
            world.setBlockToAir(x, y, z);
            return true;
        }
        
        int id = ist.itemID;
        int d = ist.getItemDamage();
        
        if (IgnitersHandler.isSetTorchIgniter(id, d) || (id == this.blockID && d == 0 && IgnitersHandler.isHeldTorchIgniter(id, -1)))
        {
            if (md < 6)
            {
                if (id == this.blockID)
                {
                    this.renewTorches(world, p, ist, x, y, z);
                }
                else if (id == ConfigCommon.itemIdTinderboxFS)
                {
                    setTileEntityAge(1, world, x, y, z, "fire.ignite");
                    
                    if (!p.capabilities.isCreativeMode && ist.attemptDamageItem(1, world.rand))
                    {
                        p.inventory.setInventorySlotContents(p.inventory.currentItem, new ItemStack(ConfigCommon.itemIdTinderbox, 1, 0));
                    }
                }
                else if (id == Item.flint.itemID)
                {
                    setTileEntityAge(1, world, x, y, z, "fire.ignite");
                    
                    if (!p.capabilities.isCreativeMode)
                    {
                        p.inventory.decrStackSize(p.inventory.currentItem, 1);
                    }
                }
                else if (id == Item.flintAndSteel.itemID)
                {
                    setTileEntityAge(1, world, x, y, z, "fire.ignite");
                    ist.damageItem(1, p);
                }
                else if (id == Item.gunpowder.itemID)
                {
                    if (!p.capabilities.isCreativeMode)
                    {
                        p.inventory.decrStackSize(p.inventory.currentItem, 5);
                    }
                    
                    int size = ist.stackSize;
                    float strength = size < 5 ? (size * 0.2F) : 1F;
                    
                    world.newExplosion(p, x, y, z, strength, size > 5, true);
                }
                else if (id == Item.bucketLava.itemID)
                {
                    setTileEntityAge(1, world, x, y, z, "fire.fire");
                }
                else
                {
                    setTileEntityAge(1, world, x, y, z, "fire.fire");
                    
                    if (!p.capabilities.isCreativeMode)
                    {
                        if (Item.itemsList[id].isDamageable())
                        {
                            ist.damageItem(1, p);
                        }
                        else
                        {
                            p.inventory.decrStackSize(p.inventory.currentItem, 1);
                        }
                    }
                }
                
                return true;
            }
            else
            {
                if (id == this.blockID)
                {
                    if (d < 1)
                    {
                        return false;
                    }
                    else
                    {
                        igniteBlockTorch(d, world, x, y, z, "fire.fire");
                    }
                }
                else if (id == ConfigCommon.itemIdTinderboxFS)
                {
                    igniteBlockTorch(1, world, x, y, z, "fire.ignite");
                    
                    if (!p.capabilities.isCreativeMode && ist.attemptDamageItem(1, world.rand))
                    {
                        p.inventory.setInventorySlotContents(p.inventory.currentItem, new ItemStack(ConfigCommon.itemIdTinderbox, 1, 0));
                    }
                }
                else if (id == Item.flint.itemID)
                {
                    igniteBlockTorch(1, world, x, y, z, "fire.ignite");
                    
                    if (!p.capabilities.isCreativeMode)
                    {
                        p.inventory.decrStackSize(p.inventory.currentItem, 1);
                    }
                }
                else if (id == Item.flintAndSteel.itemID)
                {
                    igniteBlockTorch(1, world, x, y, z, "fire.ignite");
                    ist.damageItem(1, p);
                }
                else if (id == Item.bucketLava.itemID)
                {
                    igniteBlockTorch(1, world, x, y, z, "fire.fire");
                }
                else if (id == Item.gunpowder.itemID)
                {
                    return false;
                }
                else
                {
                    igniteBlockTorch(1, world, x, y, z, "fire.fire");
                    
                    if (!p.capabilities.isCreativeMode)
                    {
                        if (Item.itemsList[id].isDamageable())
                        {
                            ist.damageItem(1, p);
                        }
                        else
                        {
                            p.inventory.decrStackSize(p.inventory.currentItem, 1);
                        }
                    }
                }
                
                return true;
            }
        }
        else if (md < 6 && IgnitersHandler.isDouser(id, d))
        {
            if (id == Item.bucketMilk.itemID || id == Item.bucketWater.itemID)
            {
                killBlockTorch(world, x, y, z, "random.fizz", 0.5F);
            }
            else
            {
                killBlockTorch(world, x, y, z, "fire.fire", 1F);
                
                if (!p.capabilities.isCreativeMode)
                {
                    p.inventory.decrStackSize(p.inventory.currentItem, 1);
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    
    //----------------------------------mine----------------------------------//
    
    
    private void renewTorches(World world, EntityPlayer p, ItemStack ist, int x, int y, int z)
    {
        String sound = "fire.fire";
        TileEntityTorch te = getTileEntityTorch(world, x, y, z);
        
        int ta = te.getAge();
        int ia = ist.getItemDamage();
        
        if (p.capabilities.isCreativeMode)
        {
            if (ia > 0)
            {
                if (!world.isRemote)
                {
                    PacketSender.sendAgePacket(1, x, y, z, world.provider.dimensionId);
                }
                
                te.setAge(1);
            }
            else
            {
                int size = ist.stackSize;
                
                if (p.isSneaking())
                {
                    size = 1;
                    PacketSender.sendInventoryPacket(p, (byte)0, (byte)0);
                }
                
                p.inventory.addItemStackToInventory(new ItemStack(this.blockID, size, 1));
            }
        }
        else if (ta == ia)
        {
            return;
        }
        else if (!p.isSneaking())
        {
            if (ia == 0 || ta < ia)
            {
                ist.setItemDamage(ta);
            }
            else
            {
                if (!world.isRemote)
                {
                    PacketSender.sendAgePacket(ia, x, y, z, world.provider.dimensionId);
                }
                
                te.setAge(ia);
            }
        }
        else
        {
            if (ia == 0 || ta < ia)
            {
                if (!p.inventory.addItemStackToInventory(new ItemStack(this.blockID, 1, ta)))
                {
                    return;
                }
                
                ist.stackSize--;
            }
            else
            {
                PacketSender.sendAgePacket(ia, x, y, z, world.provider.dimensionId);
                te.setAge(ia);
            }
        }
        
        p.swingItem();
        world.playSoundAtEntity(p, sound, 1F, world.rand.nextFloat() * 0.4F + 0.8F);
    }
    
    private static void setTileEntityAge(int age, World world, int x, int y, int z, String sound)
    {
        TileEntityTorch te = getTileEntityTorch(world, x, y, z);
        if (te == null) return;
        if (sound != null) world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, sound, 1F, world.rand.nextFloat() * 0.4F + 0.8F);
        
        te.setAge(age);
        if (!world.isRemote) PacketSender.sendAgePacket(age, x, y, z, world.provider.dimensionId);
    }
    
    public static void igniteBlockTorch(int age, World world, int x, int y, int z, String sound)
    {
        int md = getTorchMetadata(world, x, y, z);
        if (md > 5) md -= 5;
        
        world.setBlockMetadataWithNotify(x, y, z, md, 3);
        world.updateLightByType(EnumSkyBlock.Block, x, y, z);
        
        setTileEntityAge(age, world, x, y, z, sound);
    }
    
    public static void killBlockTorch(World world, int x, int y, int z, String sound, float volume)
    {
        TileEntityTorch te = getTileEntityTorch(world, x, y, z);
        if (te != null) te.invalidate();
        
        int md = getTorchMetadata(world, x, y, z);
        if (md < 6) md += 5;
        
        world.setBlockMetadataWithNotify(x, y, z, md, 3);
        world.updateLightByType(EnumSkyBlock.Block, x, y, z);
        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, sound, volume, world.rand.nextFloat() * 0.4F + 0.8F);
    }
    
    public static TileEntityTorch getTileEntityTorch(World world, int x, int y, int z)
    {
        if (world.getBlockId(x, y, z) != 50) return null;
        
        TileEntity te = world.getBlockTileEntity(x, y, z);
        
        if (te == null || !(te instanceof TileEntityTorch))
        {
            int md = getTorchMetadata(world, x, y, z);
            if (md > 5) return null;
            
            te = new TileEntityTorch(ConfigCommon.torchLifespanMax / 2);
            world.setBlockTileEntity(x, y, z, te);
        }
        
        return (TileEntityTorch) te;
    }
    
    public static int getTorchMetadata(World world, int x, int y, int z)
    {
        int md = world.getBlockMetadata(x, y, z);
        
        if (md < 1 || 10 < md)
        {
            blocksList[50].onBlockAdded(world, x, y, z);
            md = world.getBlockMetadata(x, y, z);
        }
        
        return md;
    }
}
