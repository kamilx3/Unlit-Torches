package pelep.unlittorch.proxy;

import static pelep.unlittorch.config.ConfigCommon.*;
import static pelep.unlittorch.UnlitTorch.LOGGER;
import static pelep.unlittorch.UnlitTorch.MOD_ID;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import pelep.pcl.util.Overrider;
import pelep.unlittorch.block.BlockTorchLit;
import pelep.unlittorch.block.BlockTorchUnlit;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.EventHandler;
import pelep.unlittorch.handler.IgnitersHandler;
import pelep.unlittorch.item.ItemCloth;
import pelep.unlittorch.item.ItemTorchLit;
import pelep.unlittorch.item.ItemTorchUnlit;
import pelep.unlittorch.multipart.TorchPartFactory;
import pelep.unlittorch.recipe.*;
import pelep.unlittorch.tileentity.TileEntityTorch;

import java.io.File;
import java.util.Random;

/**
 * @author pelep
 */
public class ProxyCommon
{
    public void setUpConfig(File f)
    {
        LOGGER.info("Reading config file");
        new ConfigCommon().load(f);
        LOGGER.fine("Read!");
    }

    public void setUpTools()
    {
        LOGGER.info("Registering igniters");
        IgnitersHandler.setUpTorchIgniters();
    }

    public void registerRenderers() {}

    public void registerTorches()
    {
        LOGGER.info("Modifying block torch");
        Block.blocksList[Block.torchWood.blockID] = null;
        Block blockTorch = new BlockTorch(Block.torchWood.blockID)
        {
            @Override
            public void updateTick(World world, int x, int y, int z, Random rand)
            {
                world.setBlock(x, y, z, blockIdTorchLit, world.getBlockMetadata(x, y, z), 2);
                if (world.getBlockId(x, y, z) == blockIdTorchLit)
                    blocksList[blockIdTorchLit].updateTick(world, x, y, z, rand);
            }
        }
        .setLightValue(0.9375F)
        .setHardness(0F)
        .setStepSound(Block.soundWoodFootstep)
        .setTextureName("torch_on")
        .setUnlocalizedName("torch");
        LOGGER.fine("Block %d modified!", 50);

        Overrider.replaceBlock(blockTorch, "torchWood", "field_72069_aq");

        LOGGER.info("Registering new torches");
        GameRegistry.registerBlock(new BlockTorchLit(), ItemTorchLit.class, "unlittorch:torch_lit", MOD_ID);
        GameRegistry.registerBlock(new BlockTorchUnlit(), ItemTorchUnlit.class, "unlittorch:torch_unlit", MOD_ID);
    }

    public void registerItems()
    {
        LOGGER.info("Registering new items");
        GameRegistry.registerItem(new ItemCloth(), "unlittorch:cloth", MOD_ID);
    }

    public void registerTileEntity()
    {
        LOGGER.info("Registering tile entity");
        GameRegistry.registerTileEntity(TileEntityTorch.class, "UTTileEntityTorch");
    }

    public void registerRecipes()
    {
        LOGGER.info("Modifying crafting recipes");
        Overrider.removeRecipesFor(new ItemStack(Block.torchWood.blockID, 4, 0));

        LOGGER.info("Registering new crafting recipes");
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
        GameRegistry.addRecipe(new RecipeTorchRepair());
        GameRegistry.addRecipe(new ItemStack(Block.pumpkinLantern), "p", "t", 'p', Block.pumpkin, 't', new ItemStack(blockIdTorchLit, 1, 0));
        GameRegistry.addShapelessRecipe(new ItemStack(itemIdCloth, 1, 1), Item.bucketWater, new ItemStack(itemIdCloth, 1, 0));

        LOGGER.info("Registering crafting handlers");
        GameRegistry.registerCraftingHandler(recipeTorchLitA);
        GameRegistry.registerCraftingHandler(recipeTorchLitB);
        GameRegistry.registerCraftingHandler(recipeTorchUnlitB);
        GameRegistry.registerCraftingHandler(recipeCloth);
    }

    public void registerListeners()
    {
        LOGGER.info("Registering event listeners");
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public void registerLightSources() {}

    public void registerTorchParts()
    {
        if (Loader.isModLoaded("ForgeMultipart"))
        {
            LOGGER.info("Registering Forge Multipart torches");
            new TorchPartFactory();
        }
    }
}
