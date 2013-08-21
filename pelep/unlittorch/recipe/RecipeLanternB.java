package pelep.unlittorch.recipe;

import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeLanternB implements IRecipe
{
    private ItemStack lantern;
    
    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        int n = 0;
        
        for (int i = 0; i < ic.getSizeInventory(); i++)
        {
            ItemStack ist = ic.getStackInSlot(i);
            n++;
            
            if (ist != null)
            {
                int id = ist.itemID;
                
                if ((i == 1 || i == 3 || i == 5) && id == Item.ingotIron.itemID)
                {
                    continue;
                }
                else if (i == 7 && (id == ConfigCommon.blockIdLanternLit || id == ConfigCommon.blockIdLanternUnlit))
                {
                    if (ist.stackTagCompound == null || !ist.stackTagCompound.getBoolean("handle"))
                    {
                        continue;
                    }
                }
            }
            else if (i % 2 == 0)
            {
                continue;
            }
            
            return false;
        }
        
        if (n == 9)
        {
            this.lantern = ic.getStackInSlot(7).copy();
            
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
        return 9;
    }
    
    @Override
    public ItemStack getRecipeOutput()
    {
        return this.lantern;
    }
}
