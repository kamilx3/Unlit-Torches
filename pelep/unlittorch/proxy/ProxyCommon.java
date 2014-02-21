package pelep.unlittorch.proxy;

import static pelep.unlittorch.UnlitTorch.MOD_ID;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import pelep.pcl.helper.RecipeHelper;
import pelep.unlittorch.block.BlockTorchLit;
import pelep.unlittorch.block.BlockTorchUnlit;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.EventHandler;
import pelep.unlittorch.handler.IgnitersHandler;
import pelep.unlittorch.handler.LogHandler;
import pelep.unlittorch.item.ItemTorchLit;
import pelep.unlittorch.item.ItemTorchUnlit;
import pelep.unlittorch.recipe.RecipeTorch;
import pelep.unlittorch.recipe.RecipeTorchLitA;
import pelep.unlittorch.recipe.RecipeTorchLitB;
import pelep.unlittorch.recipe.RecipeTorchUnlit;
import pelep.unlittorch.tileentity.TileEntityTorch;

import java.io.File;

/**
 * @author pelep
 */
public class ProxyCommon
{
    public void setUpConfig(File f)
    {
        LogHandler.info("Reading config file");

        Configuration config = new Configuration(f);
        config.load();
        ConfigCommon.loadConfig(config);
        config.save();

        LogHandler.fine("Read!");
    }

    public void setUpIgniters()
    {
        IgnitersHandler.setUpSetTorchIgniters();
        IgnitersHandler.setUpHeldTorchIgniters();
    }

    public void registerRenderers() {}

    public void registerTorches()
    {
        LogHandler.info("Replacing block torch");
        Block.blocksList[50] = null;
        BlockTorchLit blockTorchLit = new BlockTorchLit();
        LogHandler.fine("Block %d replaced!", 50);

        try
        {
            Block.torchWood = blockTorchLit;
            LogHandler.fine("Block field replaced!");
        }
        catch(IllegalAccessError e)
        {
            LogHandler.warning("Block field not replaced! Was Unlit Torch loaded as a coremod?");
        }

        LogHandler.info("Replacing item torch");
        Item.itemsList[50] = null;
        GameRegistry.registerBlock(blockTorchLit, ItemTorchLit.class, "unlittorch:torch_lit", MOD_ID);
        LogHandler.fine("Item %d replaced!", 50);

        LogHandler.info("Registering unlit torch");
        GameRegistry.registerBlock(new BlockTorchUnlit(), ItemTorchUnlit.class, "unlittorch:torch_unlit", MOD_ID);
    }

    public void registerTileEntity()
    {
        LogHandler.info("Registering tile entity");
        GameRegistry.registerTileEntity(TileEntityTorch.class, "UTTileEntityTorch");
    }

    public void registerRecipes()
    {
        LogHandler.info("Modifying crafting recipes");
        RecipeHelper.removeRecipesWithResult(new ItemStack(50, 4, 0));
//        RecipeHelper.replaceRecipeIngredient(new ItemStack(50, 1, 0), new ItemStack(50, 1, 1));

        LogHandler.info("Registering new crafting recipes");

        RecipeTorchLitA recipeTorchLitA = new RecipeTorchLitA();
        RecipeTorchLitB recipeTorchLitB = new RecipeTorchLitB();
        GameRegistry.addRecipe(new RecipeTorch());
        GameRegistry.addRecipe(recipeTorchLitA);
        GameRegistry.addRecipe(recipeTorchLitB);
        GameRegistry.addRecipe(new RecipeTorchUnlit());

        ItemStack torch = new ItemStack(ConfigCommon.blockIdTorchUnlit, 1, 0);
        GameRegistry.addShapelessRecipe(new ItemStack(Item.stick, 1, 0), torch.copy(), torch.copy(), torch.copy(), torch);

        LogHandler.info("Registering crafting handlers");
        GameRegistry.registerCraftingHandler(recipeTorchLitA);
        GameRegistry.registerCraftingHandler(recipeTorchLitB);
    }

    public void registerTrackers()
    {
        LogHandler.info("Registering event handler");
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public void registerLightSources() {}

    public void checkTorch()
    {
        if (!(Block.blocksList[50] instanceof BlockTorchLit))
        {
            LogHandler.severe("Block %d is either still vanilla torches or has been changed by a mod other than Unlit Torch", 50);
            LogHandler.severe("Rectifying. This could result in bugs. Best to single out what mod causes this and take it out");
            Block.blocksList[50] = null;
            new BlockTorchLit();
        }

        if (!(Item.itemsList[50] instanceof ItemTorchLit))
        {
            LogHandler.severe("Item %d is either still vanilla torches or has been changed by a mod other than Unlit Torch", 50);
            LogHandler.severe("Rectifying. This could result in bugs. Best to single out what mod causes this and take it out");
            Item.itemsList[50] = null;
            new ItemTorchLit(50 - 256);
        }
    }
}
