package pelep.unlittorch.handler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pelep.pcl.helper.ItemInfoStorageHelper;
import pelep.unlittorch.config.ConfigCommon;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author pelep
 */
public class IgnitersHandler extends ItemInfoStorageHelper
{
    private static final HashMap<Integer, ArrayList<Integer>> ignitersSet = new HashMap();
    private static final HashMap<Integer, ArrayList<Integer>> ignitersHeld = new HashMap();

    public static boolean canIgniteSetTorch(int id, int md)
    {
        return valid(id, md, ignitersSet);
    }

    public static boolean canIgniteHeldTorch(int id, int md)
    {
        return valid(id, md, ignitersHeld);
    }

    public static void setUpTorchIgniters()
    {
        parseConfigString(ConfigCommon.igniterIdsSet, ignitersSet);
        parseConfigString(ConfigCommon.igniterIdsHeld, ignitersHeld);
        ConfigCommon.igniterIdsSet = getFormattedString(ignitersSet);
        ConfigCommon.igniterIdsHeld = getFormattedString(ignitersHeld);
    }

    @SideOnly(Side.CLIENT)
    public static void syncWithServer(byte type, String string)
    {
        switch (type)
        {
            case 0:
                parseFormattedString(string, ignitersSet);
                break;
            case 1:
                parseFormattedString(string, ignitersHeld);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void desyncFromServer()
    {
        parseFormattedString(ConfigCommon.igniterIdsSet, ignitersSet);
        parseFormattedString(ConfigCommon.igniterIdsHeld, ignitersHeld);
    }

    public static HashMap<Integer, ArrayList<Integer>> getSetIgniters()
    {
        return new HashMap<Integer, ArrayList<Integer>>(ignitersSet);
    }

    public static HashMap<Integer, ArrayList<Integer>> getHeldIgniters()
    {
        return new HashMap<Integer, ArrayList<Integer>>(ignitersHeld);
    }
}
