package pelep.unlittorch.item;

import java.util.List;

import pelep.unlittorch.config.ConfigCommon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemTinderbox extends Item
{
    private static Icon icon_empty;
    
    public ItemTinderbox()
    {
        super(ConfigCommon.itemIdTinderbox - 256);
        this.setCreativeTab(CreativeTabs.tabMaterials);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setUnlocalizedName("ut_tinderbox");
        this.func_111206_d("unlittorch:tinderbox");
    }
    
    
    //--------------------------------register---------------------------------//
    
    
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(int id, CreativeTabs ct, List list)
    {
        list.add(new ItemStack(id, 1, 0));
        list.add(new ItemStack(id, 1, 1));
    }
    
    @Override
    public String getUnlocalizedName(ItemStack ist)
    {
        return ist.getItemDamage() == 0 ? "item.ut_tinderbox" : "item.ut_tinderboxEmpty";
    }
    
    
    //--------------------------------rendering--------------------------------//
    
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister ir)
    {
        this.itemIcon = ir.registerIcon("unlittorch:tinderbox");
        icon_empty = ir.registerIcon("unlittorch:tinderbox_empty");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIconFromDamage(int d)
    {
        return d == 0 ? this.itemIcon : icon_empty;
    }
}
