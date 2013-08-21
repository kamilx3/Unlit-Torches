package pelep.unlittorch.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pelep.unlittorch.block.BlockLanternLit;
import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemLantern extends ItemBlock
{
    protected Icon icon_complete;
    
    public ItemLantern(int id)
    {
        super(id);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setMaxDamage(ConfigCommon.lanternLifespanMax);
        this.setMaxStackSize(1);
        this.setNoRepair();
    }
    
    @Override
    public int getMaxDamage()
    {
        return ConfigCommon.lanternLifespanMax;
    }
    
    @Override
    public boolean isItemTool(ItemStack ist)
    {
        return false;
    }
    
    
    //--------------------------------register---------------------------------//
    

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(int id, CreativeTabs ct, List list)
    {
        ItemStack handle = new ItemStack(id, 1, 0);
        NBTTagCompound tag = new NBTTagCompound();
        
        tag.setBoolean("handle", true);
        handle.setTagCompound(tag);
        
        ItemStack nohandle = handle.copy();
        nohandle.stackTagCompound.setBoolean("handle", false);
        
        list.add(handle);
        list.add(nohandle);
    }
    
    
    //--------------------------------rendering--------------------------------//
    
    
    @SideOnly(Side.CLIENT)
    @Override
    public int getSpriteNumber()
    {
        return 1;
    }

    @Override
    public Icon getIconFromDamage(int d)
    {
        return d == 0 ? this.itemIcon : this.icon_complete;
    }
    
    
    //---------------------------------itemuse---------------------------------//
    
    
    @Override
    public boolean hitEntity(ItemStack ist, EntityLivingBase el, EntityLivingBase ep)
    {
        EntityPlayer p = (EntityPlayer) ep;
        
        if (!p.capabilities.isCreativeMode)
        {
            int age = ist.getItemDamage() + (this.getMaxDamage() / 24);
            ist.setItemDamage(Math.min(age, this.getMaxDamage()));
        }
        
        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack ist, World world, int id, int x, int y, int z, EntityLivingBase el)
    {
        int age = ist.getItemDamage() + (this.getMaxDamage() / 24);
        ist.setItemDamage(Math.min(age,  this.getMaxDamage()));
        
        if (!world.isRemote && ist.stackTagCompound != null && ist.stackTagCompound.getBoolean("handle") && world.rand.nextInt(10) == 0)
        {
            ItemStack l = ist.copy();
            ItemStack h = new ItemStack(ConfigCommon.itemIdLanternFuel, 1, 3);
            EntityPlayer p = (EntityPlayer) el;
            
            l.stackTagCompound.setBoolean("handle", false);
            p.inventory.setInventorySlotContents(p.inventory.currentItem, h);
            p.dropPlayerItemWithRandomChoice(l, false);
            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.break", 1F, 1.5F);
        }
        
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack ist, EntityPlayer p, World world, int x, int y, int z, int side, float i, float j, float k)
    {
        int id = world.getBlockId(x, y, z);
        int hook = ConfigCommon.blockIdLanternHook;
        
        if (id == Block.snow.blockID && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            side  = 1;
        }
        else if (id != hook &&
                id != Block.vine.blockID &&
                id != Block.tallGrass.blockID &&
                id != Block.deadBush.blockID &&
                (Block.blocksList[id] == null ||
                !Block.blocksList[id].isBlockReplaceable(world, x, y, z)))
        {
            switch (side)
            {
            case 0:
                y--;
                break;
            case 1:
                y++;
                break;
            case 2:
                z--;
                break;
            case 3:
                z++;
                break;
            case 4:
                x--;
                break;
            case 5:
                x++;
            }
            
            id = world.getBlockId(x, y, z);
        }
        
        if (ist.stackSize == 0)
        {
            return false;
        }
        else if (!p.canPlayerEdit(x, y, z, side, ist))
        {
            return false;
        }
        else if (y == 255 && Block.blocksList[this.itemID].blockMaterial.isSolid())
        {
            return false;
        }
        else if (id == hook || world.canPlaceEntityOnSide(this.itemID, x, y, z, false, side, p, ist))
        {
            if (ist.stackTagCompound == null || !ist.stackTagCompound.getBoolean("handle"))
            {
                if (id == hook || !BlockLanternLit.canPlaceLanternOn(world, x, y - 1, z))
                {
                    return false;
                }
            }
            
            Block block = Block.blocksList[this.itemID];
            int md = block.onBlockPlaced(world, x, y, z, side, i, j, k, ist.getItemDamage());
            
            if (this.placeLanternAt(ist, p, world, x, y, z, md))
            {
                if (md < 4)
                {
                    world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.break", 0.5F, 2F);
                }
                else
                {
                    world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, block.stepSound.getPlaceSound(), (block.stepSound.getVolume() + 1F) / 2F, block.stepSound.getPitch() * 0.8F);
                }
                
                ist.stackSize--;
            }
            
            return true;
        }
        
        return false;
    }
    
    
    //-------------------------------place-block------------------------------//
    
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean canPlaceItemBlockOnSide(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack ist)
    {
        int id = world.getBlockId(x, y, z);
        int hook = ConfigCommon.blockIdLanternHook;
        
        if (id == Block.snow.blockID && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            side  = 1;
        }
        else if (id != hook &&
                id != Block.vine.blockID &&
                id != Block.tallGrass.blockID &&
                id != Block.deadBush.blockID &&
                (Block.blocksList[id] == null ||
                !Block.blocksList[id].isBlockReplaceable(world, x, y, z)))
        {
            switch (side)
            {
            case 0:
                y--;
                break;
            case 1:
                y++;
                break;
            case 2:
                z--;
                break;
            case 3:
                z++;
                break;
            case 4:
                x--;
                break;
            case 5:
                x++;
            }
            
            id = world.getBlockId(x, y, z);
        }
        
        return id == hook || world.canPlaceEntityOnSide(this.itemID, x, y, z, false, side, null, ist);
    }

    private boolean placeLanternAt(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int md)
    {
        if (world.setBlock(x, y, z, this.itemID, md, 3))
        {
            if (world.getBlockId(x, y, z) == this.itemID)
            {
                Block.blocksList[this.itemID].onBlockPlacedBy(world, x, y, z, player, ist);
            }
            
            return true;
        }
        
        return false;
    }
    
    
    //---------------------------------update---------------------------------//
    
    
    @Override
    public int getEntityLifespan(ItemStack ist, World world)
    {
        return 8000;
    }
}
