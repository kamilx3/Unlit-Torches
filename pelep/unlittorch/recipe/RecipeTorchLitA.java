package pelep.unlittorch.recipe;

import cpw.mods.fml.common.ICraftingHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import pelep.unlittorch.config.ConfigCommon;

/**
 * @author pelep
 */
public class RecipeTorchLitA implements IRecipe, ICraftingHandler
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
                else if (ist.itemID == ConfigCommon.blockIdTorchLit)
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

        if (n != 2 || t1 == -1 || t2 == -1) return false;

        ItemStack ist1 = ic.getStackInSlot(t1);
        ItemStack ist2 = ic.getStackInSlot(t2);
        int d1 = ist1.getItemDamage();
        int d2 = ist2.getItemDamage();

        if (d1 != d2)
        {
            double d = (d1 + d2) / 2;
            this.torch = new ItemStack(ConfigCommon.blockIdTorchLit, 1, MathHelper.ceiling_double_int(d));
            this.torch.setTagCompound(d1 < d2 ? ist2.getTagCompound() : ist1.getTagCompound());
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
        if (r.itemID != ConfigCommon.blockIdTorchLit) return;

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
                else if (ist.itemID == ConfigCommon.blockIdTorchLit)
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

                return;
            }
        }

        if (n != 2 || t1 == -1 || t2 == -1) return;

        ItemStack ist1 = inv.getStackInSlot(t1);
        ItemStack ist2 = inv.getStackInSlot(t2);

        double d = (ist1.getItemDamage() + ist2.getItemDamage()) / 2;

        if (ist1.getItemDamage() < ist2.getItemDamage())
        {
            ist1.stackSize++;
            ist1.setItemDamage(MathHelper.ceiling_double_int(d));
        }
        else
        {
            ist2.stackSize++;
            ist2.setItemDamage(MathHelper.ceiling_double_int(d));
        }
    }

    @Override
    public void onSmelting(EntityPlayer p, ItemStack ist)
    {
    }
}
