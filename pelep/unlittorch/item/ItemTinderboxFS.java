package pelep.unlittorch.item;

import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTinderboxFS extends Item
{
    public ItemTinderboxFS()
    {
        super(ConfigCommon.itemIdTinderboxFS - 256);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setMaxDamage(96);
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setUnlocalizedName("ut_tinderboxFS");
        this.func_111206_d("unlittorch:tinderbox_firesteel");
    }
    
    @Override
    public boolean onItemUse(ItemStack ist, EntityPlayer p, World world, int x, int y, int z, int side, float i, float j, float k)
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
        
        if (p.canPlayerEdit(x, y, z, side, ist))
        {
            if (world.isAirBlock(x, y, z))
            {
                world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "fire.ignite", 1F, itemRand.nextFloat() * 0.4F + 0.8F);
                world.setBlock(x, y, z, Block.fire.blockID);
            }
            
            if (ist.attemptDamageItem(1, itemRand))
            {
                p.inventory.setInventorySlotContents(p.inventory.currentItem, new ItemStack(ConfigCommon.itemIdTinderbox, 1, 0));
            }
            
            return true;
        }
        
        return false;
    }
}
