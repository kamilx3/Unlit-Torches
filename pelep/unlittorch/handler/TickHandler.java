package pelep.unlittorch.handler;

import java.util.EnumSet;

import pelep.unlittorch.config.ConfigCommon;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler
{
    public static byte updateAge = 2;
    public static long updateBurn = ConfigCommon.lanternBurnDamageInterval;
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return null;
    }

    @Override
    public String getLabel()
    {
        return "UTTimer";
    }
}
