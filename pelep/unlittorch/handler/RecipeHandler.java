package pelep.unlittorch.handler;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeHandler
{
    public static void removeRecipesWithResult(ItemStack ur)
    {
        ArrayList rl = (ArrayList) CraftingManager.getInstance().getRecipeList();
        int removed = 0;
        
        for (int i = 0; i < rl.size(); i++)
        {
            ItemStack r = ((IRecipe)rl.get(i)).getRecipeOutput();
            
            if (ItemStack.areItemStacksEqual(ur, r))
            {
                removed++;
                rl.remove(i--);
                LogHandler.fine("Removed a recipe for %s", r);
            }
        }
        
        if (removed == 0)
        {
            LogHandler.warning("No recipe for %s was found", ur);
        }
    }
    
    public static void replaceRecipeIngredient(ItemStack ui, ItemStack replace)
    {
        ArrayList rl = (ArrayList) CraftingManager.getInstance().getRecipeList();

        for (Object o : rl)
        {
            replaceRecipeIngredient((IRecipe) o, ui, replace);
        }
    }
    
    public static void replaceRecipeIngredient(ItemStack result, ItemStack ui, ItemStack replace)
    {
        ArrayList rl = (ArrayList) CraftingManager.getInstance().getRecipeList();

        for (Object o : rl)
        {
            IRecipe ir = (IRecipe) o;

            if (ItemStack.areItemStacksEqual(result, ir.getRecipeOutput()))
            {
                replaceRecipeIngredient(ir, ui, replace);
            }
        }
    }
    
    private static void replaceRecipeIngredient(IRecipe ir, ItemStack ui, ItemStack replace)
    {
        boolean replaced = false;
        
        if (ir instanceof ShapedRecipes)
        {
            ShapedRecipes sr = (ShapedRecipes) ir;
            
            for (int i = 0; i < sr.recipeItems.length; i++)
            {
                ItemStack ing = sr.recipeItems[i];
                
                if (ItemStack.areItemStacksEqual(ing, ui))
                {
                    replaced = true;
                    sr.recipeItems[i] = replace.copy();
                }
            }
        }
        else if (ir instanceof ShapelessRecipes)
        {
            ShapelessRecipes sr = (ShapelessRecipes) ir;
            
            for (int i = 0; i < sr.recipeItems.size(); i++)
            {
                ItemStack ing = (ItemStack) sr.recipeItems.get(i);
                
                if (ItemStack.areItemStacksEqual(ing, ui))
                {
                    replaced = true;
                    sr.recipeItems.set(i, replace.copy());
                }
            }
        }
        else if (ir instanceof ShapedOreRecipe)
        {
            ShapedOreRecipe sr = (ShapedOreRecipe) ir;
            Object[] ings = sr.getInput();
            
            for (int i = 0; i < sr.getInput().length; i++)
            {
                Object o = ings[i];
                
                if (o instanceof ItemStack)
                {
                    if (areStacksEqual((ItemStack) o, ui))
                    {
                        replaced = true;
                        sr.getInput()[i] = replace.copy();
                    }
                }
                else if (o instanceof ArrayList)
                {
                    ArrayList<ItemStack> al = (ArrayList) o;
                    
                    for (int j = 0; j < al.size(); j++)
                    {
                        ItemStack ing = al.get(j);
                        
                        if (areStacksEqual(ing, ui))
                        {
                            replaced = true;
                            al.set(j, replace.copy());
                        }
                    }
                }
            }
        }
        else if (ir instanceof ShapelessOreRecipe)
        {
            ShapelessOreRecipe sr = (ShapelessOreRecipe) ir;
            ArrayList ings = sr.getInput();
            
            for (int i = 0; i < ings.size(); i++)
            {
                Object o = ings.get(i);
                
                if (o instanceof ItemStack)
                {
                    if (areStacksEqual((ItemStack) o, ui))
                    {
                        replaced = true;
                        ings.set(i, replace.copy());
                    }
                }
                else if (o instanceof ArrayList)
                {
                    ArrayList<ItemStack> al = (ArrayList) o;
                    
                    for (int j = 0; j < al.size(); j++)
                    {
                        ItemStack ing = al.get(j);
                        
                        if (areStacksEqual(ing, ui))
                        {
                            replaced = true;
                            al.set(j, replace.copy());
                        }
                    }
                }
            }
        }

        if (replaced)
        {
            LogHandler.fine("Replaced ingredients in a recipe for %s", ir.getRecipeOutput());
        }
    }

    private static boolean areStacksEqual(ItemStack target, ItemStack input)
    {
        if (input == null && target == null)
        {
            return true;
        }
        else if (input == null ^ target == null)
        {
            return false;
        }
        else if (target.itemID != input.itemID)
        {
            return false;
        }
        else if (target.getItemDamage() == OreDictionary.WILDCARD_VALUE)
        {
            return true;
        }
        else
        {
            return target.getItemDamage() == input.getItemDamage();
        }
    }
}
