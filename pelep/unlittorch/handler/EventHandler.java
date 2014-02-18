package pelep.unlittorch.handler;

import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import pelep.unlittorch.ai.EntityAIBreakTorches;
import pelep.unlittorch.ai.EntityAIHandleTorches;
import pelep.unlittorch.ai.EntityAIShootTorches;
import pelep.unlittorch.config.ConfigCommon;

/**
 * @author pelep
 */
public class EventHandler
{
    private static int zombie;
    private static int skeleton;

    @ForgeSubscribe
    public void addTask(EntityJoinWorldEvent e)
    {
        if (!e.world.isRemote)
        {
            if (ConfigCommon.mobVillagerTorch && e.entity instanceof EntityVillager)
            {
                EntityVillager ev = (EntityVillager) e.entity;
                ev.tasks.addTask(1, new EntityAIHandleTorches(ev));
            }
            else if (ConfigCommon.mobSkeletonTorch > 0 && e.entity instanceof EntitySkeleton)
            {
                EntitySkeleton es = (EntitySkeleton) e.entity;

                if (es.getSkeletonType() == 0 && skeleton++ == ConfigCommon.mobSkeletonTorch)
                {
                    es.tasks.addTask(5, new EntityAIShootTorches(es));
                    skeleton = 0;
                }
            }
            else if (ConfigCommon.mobZombieTorch > 0 && e.entity instanceof EntityZombie)
            {
                if (zombie++ == ConfigCommon.mobZombieTorch)
                {
                    EntityZombie ez = (EntityZombie) e.entity;
                    ez.tasks.addTask(5, new EntityAIBreakTorches(ez));
                    zombie = 0;
                }
            }
        }
    }
}
