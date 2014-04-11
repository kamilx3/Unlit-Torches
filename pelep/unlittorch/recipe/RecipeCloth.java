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
public class RecipeCloth implements IRecipe, ICraftingHandler
{
    private final ItemStack cloth = new ItemStack(ConfigCommon.itemIdCloth, 3, 0);

    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        int n = 0;
        int c = 0;
        int s = 0;

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

                if (id == Block.cloth.blockID && ++c == 1)
                {
                    continue;
                }
                else if (id == Item.shears.itemID && ++s == 1)
                {
                    continue;
                }

                return false;
            }
        }

        return n == 2 && c == 1 && s == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting ic)
    {
        return cloth.copy();
    }

    @Override
    public int getRecipeSize()
    {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return cloth;
    }

    @Override
    public void onCrafting(EntityPlayer p, ItemStack r, IInventory inv)
    {
        if (r.itemID != ConfigCommon.itemIdCloth) return;

        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack ist = inv.getStackInSlot(i);

            if (ist != null && ist.itemID == Item.shears.itemID && !ist.attemptDamageItem(1, p.getRNG()))
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
