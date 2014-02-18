package pelep.unlittorch.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;

import java.util.List;

/**
 * @author pelep
 */
public class ItemTorchUnlit extends ItemBlock
{
    public ItemTorchUnlit(int id)
    {
        super(id);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setNoRepair();
        this.setUnlocalizedName("unlittorch:torch_unlit");
        this.setTextureName("unlittorch:torch_off");
    }


    //--------------------------------register---------------------------------//


    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(int id, CreativeTabs ct, List list) {}
}
