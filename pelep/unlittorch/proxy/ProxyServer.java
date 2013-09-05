package pelep.unlittorch.proxy;

import java.util.EnumSet;

import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.TickHandler;

import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;

@SideOnly(Side.SERVER)
public class ProxyServer extends ProxyCommon
{
    @Override
    public void registerTrackers()
    {
        super.registerTrackers();

        TickHandler th = new TickHandler()
        {
            @Override
            public void tickStart(EnumSet<TickType> type, Object... tickData)
            {
                if (((World)tickData[0]).provider.dimensionId == 0)
                {
                    if (updateAge++ >= 2)
                    {
                        updateAge = 0;
                    }

                    if (updateBurn++ >= ConfigCommon.lanternBurnDamageInterval)
                    {
                        updateBurn = 0;
                    }
                }
            }

            @Override
            public EnumSet<TickType> ticks()
            {
                return EnumSet.of(TickType.WORLD);
            }
        };

        TickRegistry.registerTickHandler(th, Side.SERVER);
    }
}
