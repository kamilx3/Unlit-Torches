package pelep.unlittorch.recipe;

import pelep.unlittorch.config.ConfigCommon;
import cpw.mods.fml.common.ICraftingHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeTorchC implements IRecipe, ICraftingHandler
{
    private ItemStack torch = new ItemStack(50, 1, 1);
    
    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        int n = 0;
        int t = -1;
        int f = -1;
        
        for (int i = 0; i <ic.getSizeInventory(); i++)
        {
            ItemStack ist = ic.getStackInSlot(i);
            
            if (ist != null)
            {
                if (++n > 2)
                {
                    return false;
                }
                
                int id = ist.itemID;
                
                if (t == -1 && id == 50)
                {
                    t = i;
                    continue;
                }
                else if (f == -1)
                {
                    if (id == Item.flint.itemID || id == Item.flintAndSteel.itemID || id == ConfigCommon.itemIdTinderboxFS)
                    {
                        f = i;
                        continue;
                    }
                }
                
                return false;
            }
        }
        
        return n == 2 && t != -1 && f != -1 && ic.getStackInSlot(t).getItemDamage() != 1;
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
                
                if (t == -1 && id == 50)
                {
                    t = i;
                    continue;
                }
                else if (f == -1 && (id == Item.flintAndSteel.itemID || id == ConfigCommon.itemIdTinderboxFS))
                {
                    f = i;
                    continue;
                }
                
                return;
            }
        }
        
        if (n != 2 || t == -1 || f == -1) return;
        
        ItemStack fs = inv.getStackInSlot(f);
        fs.stackSize++;
        
        if (fs.attemptDamageItem(1, p.getRNG()))
        {
            if (fs.itemID == ConfigCommon.itemIdTinderboxFS)
            {
                inv.setInventorySlotContents(f, new ItemStack(ConfigCommon.itemIdTinderbox, fs.stackSize, 0));
            }
            else
            {
                inv.setInventorySlotContents(f, null);
            }
        }
    }
    
    @Override
    public void onSmelting(EntityPlayer p, ItemStack ist)
    {
    }
}