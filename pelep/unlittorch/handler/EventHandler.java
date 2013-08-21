package pelep.unlittorch.handler;

import static pelep.unlittorch.handler.VillagerHandler.VILLAGER_ID;

import pelep.unlittorch.ai.EntityAIBreakTorches;
import pelep.unlittorch.ai.EntityAIHandleLanterns;
import pelep.unlittorch.ai.EntityAIHandleTorches;
import pelep.unlittorch.ai.EntityAIShootTorches;
import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

public class EventHandler
{
    private static int zombie;
    private static int skeleton;
    
    @ForgeSubscribe
    public void addTask(EntityJoinWorldEvent e)
    {
        if (!e.world.isRemote)
        {
            if (e.entity instanceof EntityVillager)
            {
                EntityVillager ev = (EntityVillager) e.entity;
                
                if (ConfigCommon.mobVillagerTorch)
                {
                    ev.tasks.addTask(1, new EntityAIHandleTorches(ev));
                }
                
                if (ConfigCommon.mobVillagerLantern)
                {
                    int p = ev.getProfession();
                    if (p == 1 || p == 2 || p == VILLAGER_ID)
                    {
                        ev.setCurrentItemOrArmor(0, new ItemStack(ConfigCommon.blockIdLanternUnlit, 1, 0));
                        ev.tasks.addTask(1, new EntityAIHandleLanterns(ev));
                    }
                }
            }
            else if (e.entity instanceof EntityZombie && ConfigCommon.mobZombieTorch > 0)
            {
                if (zombie++ == ConfigCommon.mobZombieTorch)
                {
                    EntityZombie ez = (EntityZombie) e.entity;
                    ez.tasks.addTask(5, new EntityAIBreakTorches(ez));
                    zombie = 0;
                }
            }
            else if (e.entity instanceof EntitySkeleton && ConfigCommon.mobSkeletonTorch > 0)
            {
                EntitySkeleton es = (EntitySkeleton) e.entity;
                
                if (es.getSkeletonType() == 0 && skeleton++ == ConfigCommon.mobSkeletonTorch)
                {
                    es.tasks.addTask(5, new EntityAIShootTorches(es));
                    skeleton = 0;
                }
            }
        }
    }
    
    @ForgeSubscribe
    public void dropFat(LivingDropsEvent e)
    {
        World world = e.entityLiving.worldObj;
        String mob = e.entityLiving.getEntityName();
        
        if (!world.isRemote && ConfigCommon.mobDropsFat.containsKey(mob))
        {
            EntityLivingBase el = e.entityLiving;
            
            if (!el.isChild() && world.rand.nextInt(100) < ConfigCommon.mobDropsFat.get(mob))
            {
                EntityItem ei = new EntityItem(world, el.posX, el.posY, el.posZ);
                ei.setEntityItemStack(new ItemStack(ConfigCommon.itemIdLanternFuel, 1, 0));
                e.drops.add(ei);
            }
        }
    }
}
