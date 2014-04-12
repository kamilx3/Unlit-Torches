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
    private static final HashMap<Integer, ArrayList<Range>> ignitersSet = new HashMap<Integer, ArrayList<Range>>();
    private static final HashMap<Integer, ArrayList<Range>> ignitersHeld = new HashMap<Integer, ArrayList<Range>>();

    public static boolean canIgniteSetTorch(int id, int md)
    {
        return isItemValid(id, md, ignitersSet);
    }

    public static boolean canIgniteHeldTorch(int id, int md)
    {
        return isItemValid(id, md, ignitersHeld);
    }

    public static void setUpTorchIgniters()
    {
        parseConfig(ConfigCommon.igniterIdsSet, ignitersSet);
        parseConfig(ConfigCommon.igniterIdsHeld, ignitersHeld);
        ConfigCommon.igniterIdsSet = getFormatted(ignitersSet);
        ConfigCommon.igniterIdsHeld = getFormatted(ignitersHeld);
    }

    @SideOnly(Side.CLIENT)
    public static void syncWithServer(byte type, String string)
    {
        switch (type)
        {
            case 0:
                parseFormatted(string, ignitersSet);
                break;
            case 1:
                parseFormatted(string, ignitersHeld);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void desyncFromServer()
    {
        parseFormatted(ConfigCommon.igniterIdsSet, ignitersSet);
        parseFormatted(ConfigCommon.igniterIdsHeld, ignitersHeld);
    }

    public static HashMap<Integer, ArrayList<Range>> getSetIgniters()
    {
        return new HashMap<Integer, ArrayList<Range>>(ignitersSet);
    }

    public static HashMap<Integer, ArrayList<Range>> getHeldIgniters()
    {
        return new HashMap<Integer, ArrayList<Range>>(ignitersHeld);
    }
}
