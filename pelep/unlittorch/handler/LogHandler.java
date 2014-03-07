package pelep.unlittorch.handler;

import static pelep.unlittorch.UnlitTorch.MOD_ID;

import cpw.mods.fml.common.FMLLog;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author pelep
 */
public class LogHandler
{
    private static Logger LOGGER;

    static
    {
        LOGGER = Logger.getLogger(MOD_ID);
        LOGGER.setParent(FMLLog.getLogger());
    }

    private static void log(Level lvl, String msg)
    {
        LOGGER.log(lvl, msg);
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
