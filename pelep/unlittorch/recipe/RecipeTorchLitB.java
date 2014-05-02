package pelep.unlittorch.recipe;

import cpw.mods.fml.common.ICraftingHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import pelep.unlittorch.config.ConfigCommon;

/**
 * @author pelep
 */
public class RecipeTorchLitB implements IRecipe, ICraftingHandler
{
    private ItemStack torch;

    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        int n = 0;
        int t = -1;
        int f = -1;

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

                if (t == -1 && (id == ConfigCommon.blockIdTorchUnlit))
                {
                    t = i;
                    continue;
                }
                else if (f == -1)
                {
                    if (id == ConfigCommon.blockIdTorchLit ||
                        id == Block.torchWood.blockID ||
                        id == Item.flint.itemID ||
                        id == Item.flintAndSteel.itemID ||
                        id == Item.bucketLava.itemID)
                    {
                        f = i;
                        continue;
                    }
                }

                return false;
            }
        }

        if (n != 2 || t == -1 || f == -1) return false;

        torch = new ItemStack(ConfigCommon.blockIdTorchLit, 1, ic.getStackInSlot(t).getItemDamage());
        torch.setTagCompound(ic.getStackInSlot(t).getTagCompound());
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting ic)
    {
        return torch.copy();
    }

    @Override
    public int getRecipeSize()
    {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return torch;
    }

    @Override
    public void onCrafting(EntityPlayer p, ItemStack r, IInventory inv)
    {
        if (r.itemID != ConfigCommon.blockIdTorchLit) return;

        int n = 0;
        int t = -1;
        int f = -1;

        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack ist = inv.getStackInSlot(i);

            if (ist != null)
            {
                if (++n > 2)
                {
                    return;
                }

                int id = ist.itemID;

                if (t == -1 && id == ConfigCommon.blockIdTorchUnlit)
                {
                    t = i;
                    continue;
                }
                else if (f == -1 && id == Item.flintAndSteel.itemID || id == ConfigCommon.blockIdTorchLit || id == Block.torchWood.blockID)
                {
                    f = i;
                    continue;
                }

                return;
            }
        }

        if (n != 2 || t == -1 || f == -1) return;

        ItemStack ign = inv.getStackInSlot(f);
        ign.stackSize++;

        if (ign.itemID == Item.flintAndSteel.itemID && ign.attemptDamageItem(1, p.getRNG()))
            inv.setInventorySlotContents(f, null);
    }

    @Override
    public void onSmelting(EntityPlayer p, ItemStack ist)
    {
    }
}
