package pelep.unlittorch.recipe;

import cpw.mods.fml.common.ICraftingHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

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
                
                if (ist.itemID == 50)
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
        
        int d1 = ic.getStackInSlot(t1).getItemDamage();
        int d2 = ic.getStackInSlot(t2).getItemDamage();
        
        if (d1 != d2)
        {
            if (d1 == 0) d1 = d2;
            if (d2 == 0) d2 = d1;
            
            this.torch = new ItemStack(50, 1, Math.min(d1, d2));
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
                
                return;
            }
        }
        
        if (n != 2 || t1 == -1 || t2 == -1) return;
        
        int d1 = inv.getStackInSlot(t1).getItemDamage();
        int d2 = inv.getStackInSlot(t2).getItemDamage();
        
        if (d1 == 0 || d2 == 0)
        {
            int i = d1 > d2 ? t1 : t2;
            inv.getStackInSlot(i).stackSize++;
        }
        else if (d1 > d2)
        {
            inv.getStackInSlot(t2).stackSize++;
        }
        else
        {
            inv.getStackInSlot(t1).stackSize++;
        }
    }
    
    @Override
    public void onSmelting(EntityPlayer p, ItemStack ist)
    {
    }
}