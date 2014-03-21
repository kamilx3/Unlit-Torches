package pelep.unlittorch.recipe;

import cpw.mods.fml.common.ICraftingHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import pelep.unlittorch.config.ConfigCommon;

/**
 * @author pelep
 */
public class RecipeTorchUnlitB implements IRecipe, ICraftingHandler
{
    private ItemStack torch;

    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        int n = 0;
        int t = -1;
        int c = -1;

        for (int i = 0; i < ic.getSizeInventory(); i++)
        {
            ItemStack ist = ic.getStackInSlot(i);

            if (ist != null)
            {
                if (++n > 2)
                {
                    return false;
                }

                int id = ist.itemID;

                if (t == -1 && id == ConfigCommon.blockIdTorchLit)
                {
                    t = i;
                    continue;
                }
                else if (c == -1 && (id == ConfigCommon.itemIdCloth || id == Block.cloth.blockID || id == Block.carpet.blockID))
                {
                    c = i;
                    continue;
                }

                return false;
            }
        }

        if (n == 2 && t != -1 && c != -1)
        {
            ItemStack ist = ic.getStackInSlot(t);
            this.torch = new ItemStack(ConfigCommon.blockIdTorchUnlit, 1, ist.getItemDamage());
            this.torch.setTagCompound(ist.getTagCompound());
            return true;
        }

        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting ic)
    {
        return this.torch.copy();
    }

    @Override
    public int getRecipeSize()
    {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return this.torch;
    }

    @Override
    public void onCrafting(EntityPlayer p, ItemStack r, IInventory inv)
    {
        if (r.itemID != ConfigCommon.blockIdTorchUnlit) return;

        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack ist = inv.getStackInSlot(i);

            if (ist != null && ist.itemID == ConfigCommon.itemIdCloth && ist.getItemDamage() == 1)
            {
                ist.stackSize++;
                return;
            }
        }
    }

    @Override
    public void onSmelting(EntityPlayer p, ItemStack ist)
    {
    }
}
