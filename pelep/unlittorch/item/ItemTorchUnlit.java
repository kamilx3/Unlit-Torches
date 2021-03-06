package pelep.unlittorch.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import pelep.unlittorch.block.BlockTorchLit;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.IgnitersHandler;

import java.util.List;

/**
 * @author pelep
 */
public class ItemTorchUnlit extends ItemTorch
{
    public ItemTorchUnlit(int id)
    {
        super(id);
        setUnlocalizedName("unlittorch:torch_unlit");
        setTextureName("unlittorch:torch_off");
    }


    //--------------------------------register---------------------------------//


    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(int id, CreativeTabs ct, List list) {}

    @Override
    public String getUnlocalizedName(ItemStack ist)
    {
        String name = ist.getItemDamage() >= ConfigCommon.torchLifespanMax ? "used" : "unlit";
        return "tile.unlittorch:torch_" + name;
    }


    //---------------------------------render----------------------------------//


    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIconFromDamage(int d)
    {
        d = d >= ConfigCommon.torchLifespanMax ? 1 : 0;
        return Block.blocksList[itemID].getIcon(d, d);
    }


    //---------------------------------itemuse---------------------------------//


    @Override
    public boolean canPlaceItemBlockOnSide(World world, int x, int y, int z, int side, EntityPlayer ep, ItemStack ist)
    {
        int id = world.getBlockId(x, y, z);

        if (!ep.isSneaking() && (id == Block.torchWood.blockID || IgnitersHandler.canIgniteHeldTorch(id, world.getBlockMetadata(x, y, z))))
            return true;

        return super.canPlaceItemBlockOnSide(world, x, y, z, side, ep, ist);
    }

    @Override
    public boolean onItemUse(ItemStack ist, EntityPlayer p, World world, int x, int y, int z, int side, float i, float j, float k)
    {
        if (!p.isSneaking())
        {
            Material m = getMaterialClicked(world, p);
            if (m == Material.lava || m == Material.fire) return false;

            int id = world.getBlockId(x, y, z);

            if (id == Block.torchWood.blockID || (id != ConfigCommon.blockIdTorchLit &&
                    IgnitersHandler.canIgniteHeldTorch(id, world.getBlockMetadata(x, y, z))))
            {
                p.swingItem();
                BlockTorchLit.igniteHeldTorch(world, ist, p);
                return true;
            }
        }

        return super.onItemUse(ist, p, world, x, y, z, side, i, j, k);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer p)
    {
        if (!p.isSneaking())
        {
            Material m = getMaterialClicked(world, p);

            if (m == Material.lava || m == Material.fire)
            {
                p.swingItem();
                BlockTorchLit.igniteHeldTorch(world, ist, p);
                return ist;
            }
        }

        return ist;
    }
}
