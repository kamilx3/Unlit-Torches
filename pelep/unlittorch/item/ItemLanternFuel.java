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

public class ItemLanternFuel extends Item
{
    private static Icon icon_fat;
    private static Icon icon_fatBottled;
    private static Icon icon_lanternOil;
    private static Icon icon_handle;
    
    public ItemLanternFuel()
    {
        super(ConfigCommon.itemIdLanternFuel - 256);
        this.setCreativeTab(CreativeTabs.tabAllSearch);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setNoRepair();
        this.setUnlocalizedName("ut_lanternFuel");
        this.func_111206_d("unlittorch:fuel");
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
        if (ct == CreativeTabs.tabMaterials)
        {
            list.add(new ItemStack(id, 1, 0));
            list.add(new ItemStack(id, 1, 3));
        }
        else if (ct == CreativeTabs.tabBrewing)
        {
            list.add(new ItemStack(id, 1, 1));
            list.add(new ItemStack(id, 1, 2));
        }
        else if (ct == null)
        {
            list.add(new ItemStack(id, 1, 0));
            list.add(new ItemStack(id, 1, 1));
            list.add(new ItemStack(id, 1, 2));
            list.add(new ItemStack(id, 1, 3));
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public CreativeTabs[] getCreativeTabs()
    {
        return new CreativeTabs[] {CreativeTabs.tabMaterials, CreativeTabs.tabBrewing};
    }
    
    @Override
    public String getUnlocalizedName(ItemStack ist)
    {
        switch(ist.getItemDamage())
        {
        case 0: return "item.ut_fat";
        case 1: return "item.ut_fatBottled";
        case 2: return "item.ut_lanternOil";
        case 3: return "item.ut_lanternHandle";
        default: return "item.ut_lanternFuel";
        }
    }
    
    
    //--------------------------------rendering--------------------------------//
    
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister ir)
    {
        icon_fat = ir.registerIcon("unlittorch:fuel_fat");
        icon_fatBottled = ir.registerIcon("unlittorch:fuel_fatbottled");
        icon_lanternOil = ir.registerIcon("unlittorch:fuel_oil");
        icon_handle = ir.registerIcon("unlittorch:lantern_handle");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIconFromDamage(int d)
    {
        switch (d)
        {
        case 0: return icon_fat;
        case 1: return icon_fatBottled;
        case 2: return icon_lanternOil;
        default: return icon_handle;
        }
    }
}
