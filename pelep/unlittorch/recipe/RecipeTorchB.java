package pelep.unlittorch.recipe;

import cpw.mods.fml.common.ICraftingHandler;
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
public class RecipeTorchB implements IRecipe, ICraftingHandler
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
                if (++n > 2)
                {
                    return false;
                }
                else if (ist.itemID == 50)
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
                else if (t2 == -1 && ist.itemID == ConfigCommon.blockIdTorchUnlit)
                {
                    t2 = i;
                    continue;
                }

                return false;
            }
        }

        if (n != 2 || t1 == -1 || t2 == -1) return false;

        ItemStack ist1 = ic.getStackInSlot(t1);
        ItemStack ist2 = ic.getStackInSlot(t2);

        if (ist2.itemID == 50)
        {
            int d1 = ist1.getItemDamage();
            int d2 = ist2.getItemDamage();

            if (d1 != d2)
            {
                this.torch = new ItemStack(50, 2, Math.min(d1, d2));
                return true;
            }

            return false;
        }

        this.torch = new ItemStack(50, 2, ist1.getItemDamage());
        return true;
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
        if (r.itemID != 50) return;

        int n = 0;
        int t1 = -1;
        int t2 = -1;

        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack ist = inv.getStackInSlot(i);

            if (ist != null)
            {
                if (++n > 2)
                {
                    return;
                }
                else if (ist.itemID == 50)
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
                else if (t2 == -1 && ist.itemID == ConfigCommon.blockIdTorchUnlit)
                {
                    t2 = i;
                    continue;
                }

                return;
            }
        }

        if (n != 2 || t1 == -1 || t2 == -1) return;

        ItemStack ist1 = inv.getStackInSlot(t1);
        ItemStack ist2 = inv.getStackInSlot(t2);

        if (ist2.itemID == ConfigCommon.blockIdTorchUnlit)
        {
            ist1.stackSize++;
        }
        else if (ist1.getItemDamage() < ist2.getItemDamage())
        {
            ist1.stackSize++;
        }
        else
        {
            ist2.stackSize++;
        }
    }

    @Override
    public void onSmelting(EntityPlayer p, ItemStack ist)
    {
    }
}
