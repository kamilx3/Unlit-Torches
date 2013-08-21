package pelep.unlittorch.handler;

import java.util.EnumSet;

import pelep.unlittorch.config.ConfigCommon;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler
{
    public static byte updateAge = 3;
    public static long updateBurn = ConfigCommon.lanternBurnDamageInterval;
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        if (updateAge++ >= 3)
        {
            updateAge = 0;
        }
        
        if (updateBurn++ >= ConfigCommon.lanternBurnDamageInterval)
        {
            updateBurn = 0;
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.WORLD);
    }

    @Override
    public String getLabel()
    {
        return "UTTimer";
    }
}
