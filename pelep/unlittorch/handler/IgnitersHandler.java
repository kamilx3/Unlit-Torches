package pelep.unlittorch.handler;

import static pelep.unlittorch.UnlitTorch.LOGGER;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pelep.pcl.helper.ItemInfoStorageHelper;
import pelep.pcl.util.item.ItemData;
import pelep.unlittorch.config.ConfigCommon;

import java.util.ArrayList;

/**
 * @author pelep
 */
public class IgnitersHandler extends ItemInfoStorageHelper
{
    private static final ArrayList<ItemData> ignitersSet = new ArrayList<ItemData>();
    private static final ArrayList<ItemData> ignitersHeld = new ArrayList<ItemData>();

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
        parseConfig(ConfigCommon.igniterIdsHeld, ignitersHeld);
        parseConfig(ConfigCommon.igniterIdsSet, ignitersSet);
        ConfigCommon.igniterIdsHeld = getFormatted(ignitersHeld);
        ConfigCommon.igniterIdsSet = getFormatted(ignitersSet);
        LOGGER.info("IgnitersHeld:[%s]", ConfigCommon.igniterIdsHeld);
        LOGGER.info("IgnitersSet:[%s]", ConfigCommon.igniterIdsSet);
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

    public static ArrayList<ItemData> getSetIgniters()
    {
        return new ArrayList<ItemData>(ignitersSet);
    }

    public static ArrayList<ItemData> getHeldIgniters()
    {
        return new ArrayList<ItemData>(ignitersHeld);
    }
}
