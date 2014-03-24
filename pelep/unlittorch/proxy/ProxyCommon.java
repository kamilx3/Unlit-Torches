package pelep.unlittorch.proxy;

import static pelep.unlittorch.UnlitTorch.MOD_ID;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
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
import pelep.unlittorch.multipart.TorchPartFactory;
import pelep.unlittorch.recipe.*;
import pelep.unlittorch.tileentity.TileEntityTorch;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Random;

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

    public void setUpTools()
    {
        LogHandler.info("Registering igniters");
        IgnitersHandler.setUpTorchIgniters();
    }

    public void registerRenderers() {}

    public void registerTorches()
    {
        LogHandler.info("Modifying block torch");
        Block.blocksList[Block.torchWood.blockID] = null;
        //make vanilla torches turn into modded ones. technically, without actually replacing it
        BlockTorch blockTorch = new BlockTorch(Block.torchWood.blockID)
        {
            @Override
            public void updateTick(World world, int x, int y, int z, Random rand)
            {
                int md = world.getBlockMetadata(x, y, z);
                world.setBlock(x, y, z, ConfigCommon.blockIdTorchLit, md, 1|2);
            }

            @Override
            public int idDropped(int md, Random rand, int fortune)
            {
                return ConfigCommon.torchDropsUnlit ? ConfigCommon.blockIdTorchUnlit : ConfigCommon.blockIdTorchLit;
            }

            @SideOnly(Side.CLIENT)
            @Override
            public int idPicked(World world, int x, int y, int z)
            {
                return ConfigCommon.blockIdTorchLit;
            }
        };
        blockTorch.setHardness(0F);
        blockTorch.setLightValue(0.9375F);
        blockTorch.setStepSound(Block.soundWoodFootstep);
        blockTorch.setUnlocalizedName("torch");
        blockTorch.setTextureName("torch_on");
        LogHandler.fine("Block %d modified!", 50);

        try
        {
            Field torch = ReflectionHelper.findField(Block.class, "torchWood", "field_72069_aq");
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(torch, torch.getModifiers() & ~Modifier.FINAL);
            torch.set(null, blockTorch);
            LogHandler.fine("Block field modified!");
        }
        catch(Exception e)
        {
            LogHandler.warning("Block field not modified!");
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

    public void registerTorchParts()
    {
        if (Loader.isModLoaded("ForgeMultipart"))
        {
            LogHandler.info("Registering Forge Multipart torches");
            new TorchPartFactory();
        }
    }
}
