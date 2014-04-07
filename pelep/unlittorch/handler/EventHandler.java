package pelep.unlittorch.handler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import pelep.unlittorch.ai.EntityAIBreakTorches;
import pelep.unlittorch.ai.EntityAIHandleTorches;
import pelep.unlittorch.ai.EntityAIShootTorches;
import pelep.unlittorch.config.ConfigCommon;

/**
 * @author pelep
 */
public class EventHandler
{
    private static int countZombie;
    private static int countSkeleton;

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

                if (es.getSkeletonType() == 0 && countSkeleton++ == ConfigCommon.mobSkeletonTorch)
                {
                    es.tasks.addTask(5, new EntityAIShootTorches(es));
                    countSkeleton = 0;
                }
            }
            else if (ConfigCommon.mobZombieTorch > 0 && e.entity instanceof EntityZombie)
            {
                if (countZombie++ == ConfigCommon.mobZombieTorch)
                {
                    EntityZombie ez = (EntityZombie) e.entity;
                    ez.tasks.addTask(5, new EntityAIBreakTorches(ez));
                    countZombie = 0;
                }
            }
        }
    }

    @ForgeSubscribe
    public void replaceTorch(HarvestDropsEvent e)
    {
        for (ItemStack ist : e.drops)
        {
            if (ist != null && ist.itemID == Block.torchWood.blockID)
                ist.itemID = ConfigCommon.torchDropsUnlit ? ConfigCommon.blockIdTorchUnlit : ConfigCommon.blockIdTorchLit;
        }
    }

    @ForgeSubscribe
    public void replaceTorch(ItemTossEvent e)
    {
        ItemStack ist = e.entityItem.getEntityItem();
        if (ist != null && ist.itemID == Block.torchWood.blockID)
            ist.itemID = ConfigCommon.torchDropsUnlit ? ConfigCommon.blockIdTorchUnlit : ConfigCommon.blockIdTorchLit;
    }

    @SideOnly(Side.CLIENT)
    @ForgeSubscribe
    public void renderTooltip(ItemTooltipEvent e)
    {
        if (e.itemStack.itemID == Block.torchWood.blockID) e.toolTip.add("Vanilla");
    }
}
