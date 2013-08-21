package pelep.unlittorch.handler;

import java.util.ArrayList;
import java.util.HashMap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class IgnitersHandler
{
    private static final HashMap<Integer, ArrayList<Integer>> torchIgnitersSet = new HashMap();
    private static final HashMap<Integer, ArrayList<Integer>> torchIgnitersHeld = new HashMap();
    private static final HashMap<Integer, ArrayList<Integer>> torchDousers = new HashMap();
    private static final HashMap<Integer, ArrayList<Integer>> lanternIgniters = new HashMap();
    private static final HashMap<Integer, ArrayList<Integer>> lanternTinder = new HashMap();

    static
    {
        torchDousers.put(Item.bucketMilk.itemID, null);
        torchDousers.put(Item.bucketWater.itemID, null);
        torchDousers.put(Block.cloth.blockID, null);
    }

    public static boolean isDouser(int id, int md)
    {
        if (torchDousers.containsKey(id))
        {
            ArrayList<Integer> v = torchDousers.get(id);
            return v == null || v.contains(md);
        }
        
        return false;
    }
    
    public static boolean isSetTorchIgniter(int id, int md)
    {
        return isIgniterOrTinder(id, md, torchIgnitersSet);
    }

    public static boolean isHeldTorchIgniter(int id, int md)
    {
        return isIgniterOrTinder(id, md, torchIgnitersHeld);
    }
    
    public static boolean isLanternIgniter(int id, int md)
    {
        return isIgniterOrTinder(id, md, lanternIgniters);
    }
    
    public static boolean isLanternTinder(int id, int md)
    {
        return isIgniterOrTinder(id, md, lanternTinder);
    }
    
    private static boolean isIgniterOrTinder(int id, int md, HashMap<Integer, ArrayList<Integer>> map)
    {
        if (id == 50 && md == 0)
        {
            return false;
        }
        else if (map.containsKey(id) && md == -1)
        {
            return true;
        }
        else if (map.containsKey(id))
        {
            ArrayList<Integer> v = map.get(id);
            return v == null || v.contains(md);
        }
        
        return false;
    }

    public static void setSetTorchIgniters()
    {
        setIgnitersOrTinder(ConfigCommon.torchIgniterIdsSet, torchIgnitersSet);
        ConfigCommon.torchIgniterIdsSet = format(torchIgnitersSet);
    }
    
    public static void setHeldTorchIgniters()
    {
        setIgnitersOrTinder(ConfigCommon.torchIgniterIdsHeld, torchIgnitersHeld);
        ConfigCommon.torchIgniterIdsHeld = format(torchIgnitersHeld);
    }
    
    public static void setLanternIgniters()
    {
        setIgnitersOrTinder(ConfigCommon.lanternIgniterIds, lanternIgniters);
        ConfigCommon.lanternIgniterIds = format(lanternIgniters);
    }
    
    public static void setLanternTinder()
    {
        setIgnitersOrTinder(ConfigCommon.lanternTinderIds, lanternTinder);
        ConfigCommon.lanternTinderIds = format(lanternTinder);
    }
    
    private static String format(HashMap<Integer, ArrayList<Integer>> server)
    {
        String igniters = "";
        
        for (int id : server.keySet())
        {
            igniters += id;
            ArrayList<Integer> v = server.get(id);
            
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
    
    private static void setIgnitersOrTinder(String igniters, HashMap<Integer, ArrayList<Integer>> server)
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
                                if (!addIgniter(id, server))
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
                                if (!addIgniter(id, md, server))
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
    public static void syncIgnitersOrTinder(byte type, String string)
    {
        switch (type)
        {
        case 1:
            sync(string, torchIgnitersSet);
            break;
        case 2:
            sync(string, torchIgnitersHeld);
            break;
        case 3:
            sync(string, lanternIgniters);
            break;
        case 4:
            sync(string, lanternTinder);
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
        sync(ConfigCommon.lanternIgniterIds, lanternIgniters);
        sync(ConfigCommon.lanternTinderIds, lanternTinder);
    }
}
