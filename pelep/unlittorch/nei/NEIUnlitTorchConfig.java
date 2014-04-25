package pelep.unlittorch.nei;

import static pelep.unlittorch.UnlitTorch.MOD_NAME;
import static pelep.unlittorch.UnlitTorch.MOD_VERSION;

import codechicken.nei.MultiItemRange;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import pelep.pcl.util.item.ItemData;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.IgnitersHandler;

import java.util.ArrayList;

/**
 * @author pelep
 */
public class NEIUnlitTorchConfig implements IConfigureNEI
{
    @Override
    public void loadConfig()
    {
        MultiItemRange torches = new MultiItemRange();
        MultiItemRange ignitersHeld = new MultiItemRange();
        MultiItemRange ignitersSet = new MultiItemRange();
        MultiItemRange extinguishers = new MultiItemRange();

        torches.add(ConfigCommon.blockIdTorchLit);
        torches.add(ConfigCommon.blockIdTorchUnlit);
        ignitersHeld.add(Block.torchWood.blockID);
        ignitersHeld.add(ConfigCommon.blockIdTorchLit);
        ignitersHeld.add(Block.lavaMoving.blockID);
        ignitersHeld.add(Block.lavaStill.blockID);
        ignitersHeld.add(Block.fire.blockID);
        ignitersSet.add(Block.torchWood.blockID);
        ignitersSet.add(ConfigCommon.blockIdTorchLit);
        addRanges(ignitersHeld, IgnitersHandler.getHeldIgniters());
        addRanges(ignitersSet, IgnitersHandler.getSetIgniters());
        extinguishers.add(ConfigCommon.itemIdCloth);
        extinguishers.add(Item.bucketMilk.itemID);
        extinguishers.add(Item.bucketWater.itemID);
        extinguishers.add(Block.cloth.blockID);
        extinguishers.add(Block.carpet.blockID);

        API.addSetRange("Unlit Torches.Torches", torches);
        API.addSetRange("Unlit Torches.Igniters.Held", ignitersHeld);
        API.addSetRange("Unlit Torches.Igniters.Set", ignitersSet);
        API.addSetRange("Unlit Torches.Extinguishers", extinguishers);

        API.registerRecipeHandler(new ShapedRecipeHandler());
        API.registerRecipeHandler(new ShapelessRecipeHandler());
        API.registerUsageHandler(new ShapedRecipeHandler());
        API.registerUsageHandler(new ShapelessRecipeHandler());
    }

    private static void addRanges(MultiItemRange mr, ArrayList<ItemData> items)
    {
        for (ItemData item : items)
        {
            if (item.md.min == -1)
            {
                mr.add(item.id);
                continue;
            }

            mr.add(item.id, item.md.min, item.md.max);
        }
    }

    @Override
    public String getName()
    {
        return MOD_NAME;
    }

    @Override
    public String getVersion()
    {
        return MOD_VERSION;
    }
}
