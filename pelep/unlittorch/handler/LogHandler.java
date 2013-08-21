package pelep.unlittorch.handler;

import static pelep.unlittorch.UnlitTorchPlugin.MOD_ID;
import static pelep.unlittorch.UnlitTorchPlugin.MOD_VERSION;

import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLLog;

public class LogHandler
{
    private static Logger logger = Logger.getLogger(MOD_ID + MOD_VERSION);

    public static void init()
    {
        logger.setParent(FMLLog.getLogger());
    }
    
    private static void log(Level lvl, String msg)
    {
        logger.log(lvl, msg);
    }
    
    public static void info(String msg, Object... args)
    {
        log(Level.INFO, String.format(msg, args));
    }
    
    public static void fine(String msg, Object... args)
    {
        log(Level.FINE, String.format(msg, args));
    }
    
    public static void finer(String msg, Object... args)
    {
        log(Level.FINER, String.format(msg, args));
    }
    
    public static void finest(String msg, Object... args)
    {
        log(Level.FINEST, String.format(msg, args));
    }
    
    public static void warning(String msg, Object... args)
    {
        log(Level.WARNING, String.format(msg, args));
    }
    
    public static void severe(String msg, Object... args)
    {
        log(Level.SEVERE, String.format(msg, args));
    }
}