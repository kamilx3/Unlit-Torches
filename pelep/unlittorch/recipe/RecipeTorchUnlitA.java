package pelep.unlittorch.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import pelep.unlittorch.config.ConfigCommon;

/**
 * @author pelep
 */
public class RecipeTorchUnlitA implements IRecipe
{
    private ItemStack torch;

    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        int n = 0;
        int t1 = -1;
        int t2 = -1;

        for (int i = 0; i < ic.getSizeInventory(); i++)
        {
            ItemStack ist = ic.getStackInSlot(i);

            if (ist != null)
            {
                if (++n > 2 || ist.getItemDamage() == 0)
                {
                    return false;
                }

                if (ist.itemID == ConfigCommon.blockIdTorchUnlit)
                {
                    if (t1 == -1)
                    {
                        t1 = i;
                        continue;
                    }
                    else if (t2 == -1)
                    {
                        t2 = i;
                        continue;
                    }
                }

                return false;
            }
        }

        if (n == 2 && t1 != -1 && t2 != -1)
        {
            ItemStack ist1 = ic.getStackInSlot(t1);
            ItemStack ist2 = ic.getStackInSlot(t2);
            int d1 = ist1.getItemDamage();
            int d2 = ist2.getItemDamage();
            int max = ConfigCommon.torchLifespanMax;
            d1 = max - d1;
            d2 = max - d2;
            int d = d1 + d2 + max * 5 / 100;
            this.torch = new ItemStack(ConfigCommon.blockIdTorchUnlit, 1, Math.max(0, max - d));
            if (ist1.stackTagCompound != null || ist2.stackTagCompound != null)
                this.torch.setTagCompound(new NBTTagCompound());
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
}
