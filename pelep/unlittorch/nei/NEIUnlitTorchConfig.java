package pelep.unlittorch.nei;

import static pelep.unlittorch.UnlitTorch.MOD_NAME;
import static pelep.unlittorch.UnlitTorch.MOD_VERSION;

import codechicken.nei.MultiItemRange;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.IgnitersHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        ignitersHeld.add(Block.lavaMoving.blockID);
        ignitersHeld.add(Block.lavaStill.blockID);
        ignitersHeld.add(Block.fire.blockID);
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

    private static void addRanges(MultiItemRange mr, HashMap<Integer, ArrayList<Integer>> map)
    {
        for (Map.Entry<Integer, ArrayList<Integer>> e : map.entrySet())
        {
            int id = e.getKey();
            ArrayList<Integer> mds = e.getValue();

            if (mds == null)
            {
                mr.add(id);
                continue;
            }

            for (int md : mds) mr.add(id, md, md);
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
