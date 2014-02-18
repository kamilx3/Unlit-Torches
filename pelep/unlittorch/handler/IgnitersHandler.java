package pelep.unlittorch.handler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import pelep.unlittorch.config.ConfigCommon;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author pelep
 */
public class IgnitersHandler
{
    private static final HashMap<Integer, ArrayList<Integer>> torchIgnitersSet = new HashMap();
    private static final HashMap<Integer, ArrayList<Integer>> torchIgnitersHeld = new HashMap();

    public static boolean canIgniteSetTorch(int id, int md)
    {
        return isIgniter(id, md, torchIgnitersSet);
    }

    public static boolean canIgniteHeldTorch(int id, int md)
    {
        return isIgniter(id, md, torchIgnitersHeld);
    }

    private static boolean isIgniter(int id, int md, HashMap<Integer, ArrayList<Integer>> map)
    {
        if (map.containsKey(id))
        {
            ArrayList<Integer> v = map.get(id);
            return v == null || v.contains(md);
        }

        return false;
    }

    public static void setUpSetTorchIgniters()
    {
        setUpIgniters(ConfigCommon.torchIgniterIdsSet, torchIgnitersSet);
        ConfigCommon.torchIgniterIdsSet = format(torchIgnitersSet);
    }

    public static void setUpHeldTorchIgniters()
    {
        setUpIgniters(ConfigCommon.torchIgniterIdsHeld, torchIgnitersHeld);
        ConfigCommon.torchIgniterIdsHeld = format(torchIgnitersHeld);
    }

    private static String format(HashMap<Integer, ArrayList<Integer>> map)
    {
        String igniters = "";

        for (int id : map.keySet())
        {
            igniters += id;
            ArrayList<Integer> v = map.get(id);

            if (v != null && v.size() != 0)
            {
                igniters += ":";
                for (int md : v)
                {
                    igniters += md;
                    igniters += ",";
                }
            }

            igniters += ";";
        }

        if (!igniters.equals(""))
        {
            igniters = igniters.replaceAll(",;", ";");
            igniters = igniters.substring(0, igniters.length() - 1);
        }

        return igniters;
    }

    private static void setUpIgniters(String igniters, HashMap<Integer, ArrayList<Integer>> map)
    {
        if (igniters != null)
        {
            igniters = igniters.replace(" ", "");
            igniters = igniters.replace(",,", ",");

            for (String item : igniters.split(","))
            {
                if (item != null && item.length() > 0)
                {
                    String[] values = item.split(":");

                    try
                    {
                        if (values.length == 1)
                        {
                            int id = Integer.parseInt(values[0]);

                            if (Item.itemsList[id] != null)
                            {
                                if (!addIgniter(id, map))
                                {
                                    LogHandler.info("Ignoring duplicate mapping %s", item);
                                }

                                continue;
                            }
                        }
                        else if (values.length == 2)
                        {
                            int id = Integer.parseInt(values[0]);
                            int md = Integer.parseInt(values[1]);

                            if (Item.itemsList[id] != null)
                            {
                                if (!addIgniter(id, md, map))
                                {
                                    LogHandler.info("Ignoring duplicate mapping %s", item);
                                }

                                continue;
                            }
                        }

                        LogHandler.warning("Igniter ID '%s' is invalid", item);
                    }
                    catch (NumberFormatException e)
                    {
                        LogHandler.warning("Igniter ID '%s' is invalid", item);
                    }
                }
            }
        }
    }

    private static boolean addIgniter(int id, HashMap<Integer, ArrayList<Integer>> server)
    {
        if (server.containsKey(id))
        {
            return false;
        }
        else
        {
            server.put(id, null);
            return true;
        }
    }

    private static boolean addIgniter(int id, int md, HashMap<Integer, ArrayList<Integer>> server)
    {
        if (!server.containsKey(id))
        {
            ArrayList<Integer> v = new ArrayList();
            v.add(md);
            server.put(id, v);
            return true;
        }
        else
        {
            ArrayList<Integer> v = server.get(id);

            if (v == null)
            {
                v = new ArrayList();
                v.add(md);
                server.put(id, v);
                return true;
            }
            else if (!v.contains(md))
            {
                v.add(md);
                return true;
            }

            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    public static void syncIgniters(byte type, String string)
    {
        switch (type)
        {
            case 0:
                sync(string, torchIgnitersSet);
                break;
            case 1:
                sync(string, torchIgnitersHeld);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void sync(String string, HashMap<Integer, ArrayList<Integer>> map)
    {
        map.clear();

        for (String item : string.split(";"))
        {
            String[] v = item.split(":");

            if (v.length == 1)
            {
                map.put(Integer.parseInt(v[0]), null);
            }
            else if (v.length == 2)
            {
                ArrayList<Integer> mds = new ArrayList();

                for (String md : v[1].split(","))
                {
                    mds.add(Integer.parseInt(md));
                }

                if (mds.isEmpty())
                {
                    mds = null;
                }

                map.put(Integer.parseInt(v[0]), mds);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void unsync()
    {
        sync(ConfigCommon.torchIgniterIdsSet, torchIgnitersSet);
        sync(ConfigCommon.torchIgniterIdsHeld, torchIgnitersHeld);
    }
}
