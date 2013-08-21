package pelep.unlittorch.handler;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import pelep.unlittorch.config.ConfigClient;
import pelep.unlittorch.config.ConfigCommon;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class LightingHandler
{
    private static ConcurrentLinkedQueue<ILightSource> lights = new ConcurrentLinkedQueue();
    
    public static int lightLevel(World world, int v, int x, int y, int z)
    {
        if (!world.isRemote) return v;
        
        for (ILightSource ls : lights)
        {
            if (!ls.ent().isDead && x == ls.x() && y == ls.y() && z == ls.z())
            {
                return Math.max(v, ls.lv());
            }
        }
        
        return v;
    }
    
    private interface ILightSource
    {
        public Entity ent();
        public void set();
        public void remove();
        public void update();
        public int x();
        public int y();
        public int z();
        public byte lv();
    }

    private static class LightSource implements ILightSource
    {
        private Entity e;
        private byte lv;
        private int x;
        private int y;
        private int z;
        private byte lastlv;
        private int lastx;
        private int lasty;
        private int lastz;
        private boolean added;
        
        private LightSource(Entity e)
        {
            this.e = e;
        }
        
        @Override
        public Entity ent()
        {
            return this.e;
        }

        @Override
        public void update()
        {
            if (this.moved() || this.lv != this.lastlv)
            {
                this.e.worldObj.updateLightByType(EnumSkyBlock.Block, this.x, this.y, this.z);
                this.e.worldObj.updateLightByType(EnumSkyBlock.Block, this.lastx, this.lasty, this.lastz);
                
                this.lastx = this.x;
                this.lasty = this.y;
                this.lastz = this.z;
            }
        }
        
        private boolean moved()
        {
            this.x = MathHelper.floor_double(this.e.posX);
            this.y = MathHelper.floor_double(this.e.posY);
            this.z = MathHelper.floor_double(this.e.posZ);
            
            return this.x != this.lastx || this.y != this.lasty || this.z != this.lastz;
        }
        
        @Override
        public void set()
        {
            ItemStack ist;
            
            if (this.e == null)
            {
                ist = null;
            }
            else if (this.e instanceof EntityPlayer)
            {
                ist = ((EntityPlayer)this.e).getCurrentEquippedItem();
            }
            else if (this.e instanceof EntityLiving)
            {
                ist = ((EntityLiving)this.e).getCurrentItemOrArmor(0);
            }
            else
            {
                ist = ((EntityItem)this.e).getEntityItem();
            }
            
            this.lastlv = this.lv;
            
            if (ist == null)
            {
                this.lv = 0;
            }
            else if (ist.itemID == 50)
            {
                this.lv = ist.getItemDamage() != 0 ? (byte) ConfigClient.lightsStrengthTorch : 0;
            }
            else if (ist.itemID == ConfigCommon.blockIdLanternLit)
            {
                this.lv = (byte) ConfigClient.lightsStrengthLantern;
            }
            else
            {
                this.lv = 0;
            }
            
            if (this.lv > 0 && !this.added)
            {
                this.added = true;
                LightsManager.add(this);
            }
            else if (this.lv < 1 && this.added)
            {
                this.remove();
            }
        }
        
        @Override
        public void remove()
        {
            this.lastlv = this.lv;
            this.lv = 0;
            this.added = false;
            LightsManager.remove(this);
        }
        
        @Override
        public int x()
        {
            return this.x;
        }
        
        @Override
        public int y()
        {
            return this.y;
        }
        
        @Override
        public int z()
        {
            return this.z;
        }
        
        @Override
        public byte lv()
        {
            return this.lv;
        }
    }

    public static class LightsManager implements ITickHandler
    {
        private static World world;
        private static Minecraft mc = Minecraft.getMinecraft();
        
        @Override
        public void tickStart(EnumSet<TickType> type, Object... data)
        {
            if (world != mc.theWorld)
            {
                world = mc.theWorld;
                lights.clear();
            }
        }
        
        @Override
        public void tickEnd(EnumSet<TickType> type, Object... data)
        {
            boolean paused = mc.isSingleplayer() && mc.currentScreen != null && mc.currentScreen.doesGuiPauseGame() && !mc.getIntegratedServer().getPublic();
            
            if (!paused)
            {
                for (ILightSource ls : lights)
                {
                    if (ls.ent() == null || ls.ent().isDead)
                    {
                        ls.remove();
                        continue;
                    }
                    
                    ls.update();
                }
            }
        }
        
        @Override
        public EnumSet<TickType> ticks()
        {
            return EnumSet.of(TickType.CLIENT);
        }
        
        @Override
        public String getLabel()
        {
            return "UTLightsManager";
        }
        
        private static void add(ILightSource add)
        {
            if (add.ent() == null || add.ent().isDead) return;
            
            for (ILightSource old : lights)
            {
                if (old.ent() == add.ent())
                {
                    return;
                }
            }
            
            lights.add(add);
        }
        
        private static void remove(ILightSource remove)
        {
            if (lights.remove(remove))
            {
                world.updateLightByType(EnumSkyBlock.Block, remove.x(), remove.y(), remove.z());
            }
        }
    }
    
    public static class PlayerLight implements ITickHandler
    {
        private LightSource player = new LightSource(LightsManager.mc.thePlayer);
        private int update;
        
        @Override
        public void tickStart(EnumSet<TickType> type, Object... data)
        {
            if (this.player.ent() != LightsManager.mc.thePlayer)
            {
                this.player = new LightSource(LightsManager.mc.thePlayer);
            }
        }

        @Override
        public void tickEnd(EnumSet<TickType> type, Object... data)
        {
            if (this.update++ >= ConfigClient.lightsIntervalPlayer)
            {
                this.update = 0;
                this.player.set();
            }
        }
        
        @Override
        public EnumSet<TickType> ticks()
        {
            return EnumSet.of(TickType.PLAYER);
        }
        
        @Override
        public String getLabel()
        {
            return "UTPlayerLight";
        }
    }

    public static class OtherLight implements ITickHandler
    {
        private World world;
        private Thread updater;
        
        private static ArrayList<LightSource> players = new ArrayList();
        private static ArrayList<LightSource> mobs = new ArrayList();
        private static ArrayList<LightSource> items = new ArrayList();
        private static boolean updating = false;
        private int update;
        
        @Override
        public void tickStart(EnumSet<TickType> type, Object... data)
        {
            if (this.world != LightsManager.world)
            {
                this.update = ConfigClient.lightsIntervalOthers - 10;
                this.world = LightsManager.world;
                players.clear();
                mobs.clear();
                items.clear();
            }
        }
        
        @Override
        public void tickEnd(EnumSet<TickType> type, Object... tickData)
        {
            if (this.update++ >= ConfigClient.lightsIntervalOthers && !updating)
            {
                this.update = 0;
                this.updater = new PlayerMPUpdater();
                this.updater.setPriority(Thread.MIN_PRIORITY);
                this.updater.start();
                updating = true;
            }
        }
        
        @Override
        public EnumSet<TickType> ticks()
        {
            return EnumSet.of(TickType.PLAYER);
        }
        
        @Override
        public String getLabel()
        {
            return "UTOtherLight";
        }
        
        private class PlayerMPUpdater extends Thread
        {
            private PlayerMPUpdater()
            {
                super("UTOtherLight");
            }
            
            private boolean light(Entity e)
            {
                ItemStack ist;
                
                if (e instanceof EntityPlayer)
                {
                    ist = ((EntityPlayer)e).getCurrentEquippedItem();
                }
                else if (e instanceof EntityLiving)
                {
                    ist = ((EntityLiving)e).getCurrentItemOrArmor(0);
                }
                else if (e instanceof EntityItem)
                {
                    ist = ((EntityItem)e).getEntityItem();
                }
                else
                {
                    ist = null;
                }
                
                if (ist != null)
                {
                    int id = ist.itemID;
                    
                    if (id == 50)
                    {
                        return ConfigClient.lightsStrengthTorch > 0 && ist.getItemDamage() != 0;
                    }
                    else if (id == ConfigCommon.blockIdLanternLit)
                    {
                        return ConfigClient.lightsStrengthLantern > 0;
                    }
                }
                
                return false;
            }
            
            @Override
            public void run()
            {
                ArrayList<LightSource> players = new ArrayList();
                ArrayList<LightSource> mobs = new ArrayList();
                ArrayList<LightSource> items = new ArrayList();
                
                for (Object o : LightsManager.world.loadedEntityList.toArray())
                {
                    Entity e = null;
                    ArrayList<LightSource> main = null;
                    ArrayList<LightSource> add = null;
                    
                    if (o instanceof EntityPlayer && ConfigClient.lightsEnablePlayerMP)
                    {
                        e = (Entity) o;
                        main = OtherLight.players;
                        add = players;
                    }
                    else if (o instanceof EntityLiving && ConfigClient.lightsEnableMobs)
                    {
                        e = (Entity) o;
                        main = OtherLight.mobs;
                        add = mobs;
                    }
                    else if (o instanceof EntityItem && ConfigClient.lightsEnableItems)
                    {
                        e = (Entity) o;
                        main = OtherLight.items;
                        add = items;
                    }
                    
                    if (e == null || e == LightsManager.mc.thePlayer || e.isDead || !light(e))
                    {
                        continue;
                    }
                    
                    boolean tracked = false;

                    for (LightSource ls : main)
                    {
                        if (ls.ent() == e)
                        {
                            tracked = true;
                            add.add(ls);
                            ls.set();
                        }
                    }
                    
                    if (!tracked)
                    {
                        LightSource ls = new LightSource(e);
                        add.add(ls);
                        ls.set();
                    }
                }

                OtherLight.players.removeAll(players);
                OtherLight.mobs.removeAll(mobs);
                OtherLight.items.removeAll(items);

                for (LightSource ls : OtherLight.players)
                {
                    ls.set();
                }
                
                for (LightSource ls : OtherLight.mobs)
                {
                    ls.set();
                }
                
                for (LightSource ls : OtherLight.items)
                {
                    ls.set();
                }

                OtherLight.players = players;
                OtherLight.mobs = mobs;
                OtherLight.items = items;
                OtherLight.updating = false;
            }
        }
    }
}
