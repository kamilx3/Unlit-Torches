package pelep.unlittorch.proxy;

import static pelep.unlittorch.UnlitTorch.MOD_ID;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.creativetab.CreativeTabs;
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
import pelep.unlittorch.item.ItemCloth;
import pelep.unlittorch.item.ItemTorchLit;
import pelep.unlittorch.item.ItemTorchUnlit;
import pelep.unlittorch.recipe.*;
import pelep.unlittorch.tileentity.TileEntityTorch;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

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
        Block.blocksList[Block.torchWood.blockID] = null;
        BlockTorch blockTorch = new BlockTorch(Block.torchWood.blockID)
        {
            @Override
            public void getSubBlocks(int id, CreativeTabs tab, List list) {}
        };
        blockTorch.setHardness(0F);
        blockTorch.setLightValue(0.9375F);
        blockTorch.setStepSound(Block.soundWoodFootstep);
        blockTorch.setUnlocalizedName("torch");
        blockTorch.setTextureName("torch_on");
        LogHandler.fine("Block %d replaced!", 50);

        try
        {
            Field torch = ReflectionHelper.findField(Block.class, "torchWood", "field_72069_aq");
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(torch, torch.getModifiers() & ~Modifier.FINAL);
            torch.set(null, blockTorch);
            LogHandler.fine("Block field replaced!");
        }
        catch(Exception e)
        {
            LogHandler.warning("Block field not replaced!");
            e.printStackTrace();
        }

        LogHandler.info("Registering new torches");
        GameRegistry.registerBlock(new BlockTorchLit(), ItemTorchLit.class, "unlittorch:torch_lit", MOD_ID);
        GameRegistry.registerBlock(new BlockTorchUnlit(), ItemTorchUnlit.class, "unlittorch:torch_unlit", MOD_ID);
    }

    public void registerItems()
    {
        LogHandler.info("Registering new items");
        GameRegistry.registerItem(new ItemCloth(), "unlittorch:cloth", MOD_ID);
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
        RecipeTorchUnlitB recipeTorchUnlitB = new RecipeTorchUnlitB();
        RecipeCloth recipeCloth = new RecipeCloth();
        GameRegistry.addRecipe(new RecipeTorch());
        GameRegistry.addRecipe(recipeTorchLitA);
        GameRegistry.addRecipe(recipeTorchLitB);
        GameRegistry.addRecipe(new RecipeTorchUnlitA());
        GameRegistry.addRecipe(recipeTorchUnlitB);
        GameRegistry.addRecipe(recipeCloth);
        GameRegistry.addRecipe(new RecipeStick());
        GameRegistry.addShapelessRecipe(new ItemStack(ConfigCommon.itemIdCloth, 1, 1), Item.bucketWater, new ItemStack(ConfigCommon.itemIdCloth, 1, 0));

        LogHandler.info("Registering crafting handlers");
        GameRegistry.registerCraftingHandler(recipeTorchLitA);
        GameRegistry.registerCraftingHandler(recipeTorchLitB);
        GameRegistry.registerCraftingHandler(recipeTorchUnlitB);
        GameRegistry.registerCraftingHandler(recipeCloth);
    }

    public void registerListeners()
    {
        LogHandler.info("Registering event listeners");
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
