package pelep.unlittorch.proxy;

import static pelep.unlittorch.UnlitTorchPlugin.MOD_ID;
import static pelep.unlittorch.handler.VillagerHandler.VILLAGER_ID;

import java.io.File;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import pelep.unlittorch.block.BlockLanternHook;
import pelep.unlittorch.block.BlockLanternLit;
import pelep.unlittorch.block.BlockLanternUnlit;
import pelep.unlittorch.block.BlockTorch;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.entity.TileEntityLantern;
import pelep.unlittorch.entity.TileEntityTorch;
import pelep.unlittorch.handler.EventHandler;
import pelep.unlittorch.handler.LogHandler;
import pelep.unlittorch.handler.LootHandler;
import pelep.unlittorch.handler.RecipeHandler;
import pelep.unlittorch.handler.TickHandler;
import pelep.unlittorch.handler.VillagerHandler;
import pelep.unlittorch.item.ItemLanternFuel;
import pelep.unlittorch.item.ItemLanternLit;
import pelep.unlittorch.item.ItemLanternUnlit;
import pelep.unlittorch.item.ItemTinderbox;
import pelep.unlittorch.item.ItemTinderboxFS;
import pelep.unlittorch.item.ItemTorch;
import pelep.unlittorch.recipe.RecipeHandle;
import pelep.unlittorch.recipe.RecipeJackoLantern;
import pelep.unlittorch.recipe.RecipeLanternA;
import pelep.unlittorch.recipe.RecipeLanternB;
import pelep.unlittorch.recipe.RecipeLanternC;
import pelep.unlittorch.recipe.RecipeRefuel;
import pelep.unlittorch.recipe.RecipeTinderboxA;
import pelep.unlittorch.recipe.RecipeTinderboxB;
import pelep.unlittorch.recipe.RecipeTinderboxEmpty;
import pelep.unlittorch.recipe.RecipeTinderboxFS;
import pelep.unlittorch.recipe.RecipeTorchA;
import pelep.unlittorch.recipe.RecipeTorchB;
import pelep.unlittorch.recipe.RecipeTorchC;

public class ProxyCommon
{
    public static final int RID_TORCH = RenderingRegistry.getNextAvailableRenderId();
    public static final int RID_HOOK = RenderingRegistry.getNextAvailableRenderId();
    
    public void setUpConfig(File f)
    {
        LogHandler.info("Reading config file");
        
        Configuration config = new Configuration(f);
        config.load();
        ConfigCommon.loadConfig(config);
        config.save();
        
        LogHandler.fine("Read!");
    }
    
    public void registerRenderers() {}
    
    public void registerTorches()
    {
        LogHandler.info("Replacing torch block");
        
        Block.blocksList[50] = null;
        BlockTorch blockTorchWood = new BlockTorch();
        
        LogHandler.fine("Block %d replaced", 50);
        
        try
        {
            Block.torchWood = blockTorchWood;
            LogHandler.fine("Block field replaced");
        }
        catch(IllegalAccessError e)
        {
            LogHandler.warning("Block field not replaced! Was Unlit Torch loaded as a coremod?");
        }
        
        LogHandler.info("Replacing torch item");
        
        Item.itemsList[50] = null;
        GameRegistry.registerBlock(blockTorchWood, ItemTorch.class, "ut_torch", MOD_ID);
        
        LogHandler.fine("Item %d replaced", 50);
    }
    
    public void registerBlocks()
    {
        LogHandler.info("Registering new blocks");
        
        GameRegistry.registerBlock(new BlockLanternLit(), ItemLanternLit.class, "ut_lanternLit", MOD_ID);
        GameRegistry.registerBlock(new BlockLanternUnlit(), ItemLanternUnlit.class, "ut_lanternUnlit", MOD_ID);
        GameRegistry.registerBlock(new BlockLanternHook(), ItemBlock.class, "ut_hook", MOD_ID);
    }
    
    public void registerItems()
    {
        LogHandler.info("Registering new items");
        
        GameRegistry.registerItem(new ItemLanternFuel(), "ut_lanternFuel", MOD_ID);
        GameRegistry.registerItem(new ItemTinderbox(), "ut_tinderbox", MOD_ID);
        GameRegistry.registerItem(new ItemTinderboxFS(), "ut_tinderboxFS", MOD_ID);
    }
    
    public void registerVillager()
    {
        LogHandler.info("Registering new villager and trades");

        VillagerHandler handler = new VillagerHandler();
        VillagerRegistry.instance().registerVillageTradeHandler(0, handler);
        VillagerRegistry.instance().registerVillageTradeHandler(3, handler);
        VillagerRegistry.instance().registerVillageTradeHandler(VILLAGER_ID, handler);
        
        if (!VillagerRegistry.getRegisteredVillagers().contains(VILLAGER_ID))
        {
            VillagerRegistry.instance().registerVillagerId(VILLAGER_ID);
        }
    }
    
    public void registerLoot()
    {
        LogHandler.info("Registering new chest loot");
        LootHandler.registerChestLoot();
    }
    
    public void registerTileEntities()
    {
        LogHandler.info("Registering tile entities");
        GameRegistry.registerTileEntity(TileEntityTorch.class, "UTTileEntityTorch");
        GameRegistry.registerTileEntity(TileEntityLantern.class, "UTTileEntityLantern");
    }
    
    public void registerRecipes()
    {
        LogHandler.info("Modifying crafting recipes");
        RecipeHandler.removeRecipesWithResult(new ItemStack(50, 4, 0));
        RecipeHandler.removeRecipesWithResult(new ItemStack(91, 1, 0));
        //RecipeHandler.replaceRecipeIngredient(new ItemStack(50, 1, 0), new ItemStack(50, 1, 1));
        
        LogHandler.info("Registering new crafting recipes");
        
        RecipeTorchB recipeTorchB = new RecipeTorchB();
        RecipeTorchC recipeTorchC = new RecipeTorchC();
        
        GameRegistry.addRecipe(new RecipeJackoLantern());
        GameRegistry.addRecipe(new RecipeTorchA());
        GameRegistry.addRecipe(recipeTorchB);
        GameRegistry.addRecipe(recipeTorchC);
        GameRegistry.addRecipe(new RecipeHandle());
        GameRegistry.addRecipe(new RecipeLanternA());
        GameRegistry.addRecipe(new RecipeLanternB());
        GameRegistry.addRecipe(new RecipeLanternC());
        GameRegistry.addRecipe(new RecipeRefuel());
        GameRegistry.addRecipe(new RecipeTinderboxA());
        GameRegistry.addRecipe(new RecipeTinderboxB());
        GameRegistry.addRecipe(new RecipeTinderboxEmpty());
        GameRegistry.addRecipe(new RecipeTinderboxFS());
        
        GameRegistry.addRecipe(new ItemStack(ConfigCommon.blockIdLanternHook, 4, 0), new Object[] {"sp", 's', Item.stick, 'p', Block.planks});
        GameRegistry.addRecipe(new ItemStack(ConfigCommon.itemIdLanternFuel, 1, 0), new Object[] {"m", 'm', Item.porkRaw});
        GameRegistry.addRecipe(new ItemStack(ConfigCommon.itemIdLanternFuel, 1, 0), new Object[] {"m", 'm', Item.porkCooked});
        GameRegistry.addRecipe(new ItemStack(ConfigCommon.itemIdLanternFuel, 1, 0), new Object[] {"m", 'm', Item.beefRaw});
        GameRegistry.addRecipe(new ItemStack(ConfigCommon.itemIdLanternFuel, 1, 0), new Object[] {"m", 'm', Item.beefCooked});
        GameRegistry.addShapelessRecipe(new ItemStack(ConfigCommon.itemIdLanternFuel, 1, 1), new ItemStack(Item.glassBottle, 1), new ItemStack(ConfigCommon.itemIdLanternFuel, 1, 0), new ItemStack(ConfigCommon.itemIdLanternFuel, 1, 0), new ItemStack(ConfigCommon.itemIdLanternFuel, 1, 0));
        GameRegistry.addShapelessRecipe(new ItemStack(Item.stick, 1, 0), new ItemStack(50, 1, 0), new ItemStack(50, 1, 0), new ItemStack(50, 1, 0), new ItemStack(50, 1, 0));
        
        LogHandler.info("Registering new smelting recipe");
        FurnaceRecipes.smelting().addSmelting(ConfigCommon.itemIdLanternFuel, 1, new ItemStack(ConfigCommon.itemIdLanternFuel, 1, 2), 0F);
        
        LogHandler.info("Registering crafting handlers");
        GameRegistry.registerCraftingHandler(recipeTorchB);
        GameRegistry.registerCraftingHandler(recipeTorchC);
    }
    
    public void registerTrackers()
    {
        LogHandler.info("Registering event handler");
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);
    }
    
    public void checkTorches()
    {
        if (!(Block.blocksList[50] instanceof BlockTorch))
        {
            LogHandler.severe("Block %d is either still vanilla torches or has been changed by a mod other than Unlit Torch....probably Fancy Fences. *rolls eyes*", 50);
            LogHandler.severe("Rectifying. This could result in bugs. Best to single out what mod causes this and take it out");
            @SuppressWarnings("unused")
            Block block = new BlockTorch();
        }
        
        if (!(Item.itemsList[50] instanceof ItemTorch))
        {
            LogHandler.severe("Item %d is either still vanilla torches or has been changed by a mod other than Unlit Torch", 50);
            LogHandler.severe("Rectifying. This could result in bugs. Best to single out what mod causes this and take it out");
            @SuppressWarnings("unused")
            Item item = new ItemTorch(50 - 256);
        }
    }
}
