package pelep.unlittorch.recipe;

import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeLanternC implements IRecipe
{
    private ItemStack lantern;
    
    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        int n = 0;
        int l = -1;
        int h = -1;
        
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
                
                if (h == -1 && id == ConfigCommon.itemIdLanternFuel && ist.getItemDamage() == 3)
                {
                    h = i;
                    continue;
                }
                else if (l == -1 && (id == ConfigCommon.blockIdLanternLit || id == ConfigCommon.blockIdLanternUnlit))
                {
                    if (ist.stackTagCompound == null || !ist.stackTagCompound.getBoolean("handle"))
                    {
                        l = i;
                        continue;
                    }
                }
                
                return false;
            }
        }
        
        if (n == 2 && l != -1 && h != -1)
        {
            this.lantern = ic.getStackInSlot(l).copy();
            
            if (this.lantern.stackTagCompound == null)
            {
                this.lantern.setTagCompound(new NBTTagCompound());
            }
            
            this.lantern.stackTagCompound.setBoolean("handle", true);
            return true;
        }
        
        return false;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting ic)
    {
        return this.lantern.copy();
    }
    
    @Override
    public int getRecipeSize()
    {
        return 2;
    }
    
    @Override
    public ItemStack getRecipeOutput()
    {
        return this.lantern;
    }
}