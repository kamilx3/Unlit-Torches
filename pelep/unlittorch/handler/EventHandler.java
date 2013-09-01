package pelep.unlittorch.handler;

import static pelep.unlittorch.handler.VillagerHandler.VILLAGER_ID;
import static pelep.unlittorch.render.RenderBlockLantern.TEXTURE_LANTERN_OFF;
import static pelep.unlittorch.render.RenderBlockLantern.TEXTURE_LANTERN_ON;

import pelep.unlittorch.ai.EntityAIBreakTorches;
import pelep.unlittorch.ai.EntityAIHandleLanterns;
import pelep.unlittorch.ai.EntityAIHandleTorches;
import pelep.unlittorch.ai.EntityAIShootTorches;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.render.RenderItemLantern;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import org.lwjgl.opengl.GL11;

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

    @ForgeSubscribe
    public void renderVillagerLantern(RenderLivingEvent.Post e)
    {
        if (e.entity instanceof EntityVillager)
        {
            EntityVillager ev = (EntityVillager) e.entity;
            ItemStack ist = ev.getHeldItem();

            if (!ev.isChild() && ist != null && (ist.itemID == ConfigCommon.blockIdLanternLit || ist.itemID == ConfigCommon.blockIdLanternUnlit))
            {
                Minecraft mc = Minecraft.getMinecraft();
                EntityPlayer p = mc.thePlayer;

                float ptick = mc.timer.renderPartialTicks;
                double x = (ev.prevPosX + (ev.posX - ev.prevPosX) * ptick) - (p.prevPosX + (p.posX - p.prevPosX) * ptick);
                double y = (ev.prevPosY + (ev.posY - ev.prevPosY) * ptick) - (p.prevPosY + (p.posY - p.prevPosY) * ptick);
                double z = (ev.prevPosZ + (ev.posZ - ev.prevPosZ) * ptick) - (p.prevPosZ + (p.posZ - p.prevPosZ) * ptick);

                float yaw = interpolateYaw(ev.prevRenderYawOffset, ev.renderYawOffset, ptick);
                float yawHead = interpolateYaw(ev.prevRotationYawHead, ev.rotationYawHead, ptick);

                if (ev.isRiding() && ev.ridingEntity instanceof EntityLivingBase)
                {
                    EntityLivingBase el = (EntityLivingBase) ev.ridingEntity;
                    yaw = interpolateYaw(el.prevRenderYawOffset, el.renderYawOffset, ptick);
                    float yawWrap = MathHelper.wrapAngleTo180_float(yawHead - yaw);

                    if (yawWrap < -85F)
                    {
                        yawWrap = -85F;
                    }
                    else if (yawWrap >= 85F)
                    {
                        yawWrap = 85F;
                    }

                    yaw = yawHead - yawWrap;

                    if (yawWrap * yawWrap > 2500F)
                    {
                        yaw += yawWrap * 0.2F;
                    }
                }

                ResourceLocation rl = ist.itemID == ConfigCommon.blockIdLanternLit ? TEXTURE_LANTERN_ON : TEXTURE_LANTERN_OFF;
                mc.renderEngine.func_110577_a(rl);

                GL11.glPushMatrix();
                GL11.glTranslated(x, y, z);
                rotateEntity(ev, yaw, ptick);
                GL11.glTranslatef(0F, 0.875F, -0.375F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);

                RenderItemLantern.equipped.renderAsEquipped(true);

                GL11.glPopMatrix();
            }
        }
    }

    private static float interpolateYaw(float pYaw, float cYaw, float ptick)
    {
        float yaw = cYaw - pYaw;

        while (yaw < -180F)
        {
            yaw += 360F;
        }

        while (yaw >= 180F)
        {
            yaw -= 360F;
        }

        return pYaw + ptick * yaw;
    }

    private static void rotateEntity(EntityLivingBase el, float yaw, float ptick)
    {
        GL11.glRotatef(180F - yaw, 0F, 1F, 0F);

        if (el.deathTime > 0)
        {
            float f = ((float)el.deathTime + ptick - 1F) / 20F * 1.6F;
            f = MathHelper.sqrt_float(f);

            if (f > 1F)
            {
                f = 1F;
            }

            GL11.glRotatef(f * 90F, 0F, 0F, 1F);
        }
        else if ((el.getEntityName().equals("Dinnerbone") || el.getEntityName().equals("Grumm")) && (!(el instanceof EntityPlayer) || !((EntityPlayer)el).getHideCape()))
        {
            GL11.glTranslatef(0F, el.height + 0.1F, 0F);
            GL11.glRotatef(180F, 0F, 0F, 1F);
        }
    }
}
