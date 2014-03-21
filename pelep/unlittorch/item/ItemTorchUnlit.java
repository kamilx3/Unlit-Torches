package pelep.unlittorch.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
        this.setUnlocalizedName("unlittorch:torch_unlit");
        this.setTextureName("unlittorch:torch_off");
    }


    //--------------------------------register---------------------------------//


    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(int id, CreativeTabs ct, List list) {}



    //---------------------------------itemuse---------------------------------//


    @Override
    public boolean onItemUse(ItemStack ist, EntityPlayer p, World world, int x, int y, int z, int side, float i, float j, float k)
    {
        if (!p.isSneaking())
        {
            Material m = rayTraceFromPlayer(world, p);
            if (m == Material.lava || m == Material.fire) return false;

            int id = world.getBlockId(x, y, z);

            if (id != ConfigCommon.blockIdTorchLit && IgnitersHandler.canIgniteHeldTorch(id, world.getBlockMetadata(x, y, z)))
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
            Material m = rayTraceFromPlayer(world, p);

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
