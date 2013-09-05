package pelep.unlittorch.proxy;

import static pelep.unlittorch.handler.VillagerHandler.VILLAGER_ID;

import java.io.File;
import java.util.EnumSet;

import cpw.mods.fml.common.TickType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
import pelep.unlittorch.config.ConfigClient;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.entity.TileEntityLantern;
import pelep.unlittorch.entity.TileEntityTorch;
import pelep.unlittorch.handler.LightingHandler;
import pelep.unlittorch.handler.LogHandler;
import pelep.unlittorch.handler.TickHandler;
import pelep.unlittorch.render.RenderBlockHook;
import pelep.unlittorch.render.RenderBlockLantern;
import pelep.unlittorch.render.RenderBlockTorch;
import pelep.unlittorch.render.RenderItemLantern;
import pelep.unlittorch.render.RenderItemTorch;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ProxyClient extends ProxyCommon
{
    @Override
    public void setUpConfig(File f)
    {
        LogHandler.info("Reading config file");
        
        Configuration config = new Configuration(f);
        config.load();
        ConfigClient.loadConfig(config);
        config.save();
        
        LogHandler.fine("Read!");
    }
    
    @Override
    public void registerRenderers()
    {
        LogHandler.info("Registering renderers");
        
        RenderItemLantern renderItemLantern = new RenderItemLantern();
        RenderBlockTorch renderBlockTorch = new RenderBlockTorch();
        
        MinecraftForgeClient.registerItemRenderer(50, new RenderItemTorch());
        MinecraftForgeClient.registerItemRenderer(ConfigCommon.blockIdLanternLit, renderItemLantern);
        MinecraftForgeClient.registerItemRenderer(ConfigCommon.blockIdLanternUnlit, renderItemLantern);
        RenderingRegistry.registerBlockHandler(RID_TORCH, renderBlockTorch);
        RenderingRegistry.registerBlockHandler(RID_HOOK, new RenderBlockHook());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTorch.class, renderBlockTorch);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLantern.class, new RenderBlockLantern());
    }
    
    @Override
    public void registerVillager()
    {
        super.registerVillager();
        VillagerRegistry.instance().registerVillagerSkin(VILLAGER_ID, new ResourceLocation("textures/entity/villager/farmer.png"));
    }
    
    @Override
    public void registerTrackers()
    {
        super.registerTrackers();

        TickHandler th = new TickHandler()
        {
            @Override
            public void tickStart(EnumSet<TickType> type, Object... tickData)
            {
                if (Minecraft.getMinecraft().thePlayer == tickData[0])
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
                return EnumSet.of(TickType.PLAYER);
            }
        };

        TickRegistry.registerTickHandler(th, Side.CLIENT);
        
        if (ConfigClient.lightsIntervalPlayer < 0)
        {
            if (ConfigClient.lightsIntervalOthers < 0)
            {
                return;
            }
            
            if (!ConfigClient.lightsEnablePlayerMP && !ConfigClient.lightsEnableMobs && !ConfigClient.lightsEnableItems)
            {
                return;
            }
        }
        
        if (ConfigClient.lightsStrengthTorch < 1 && ConfigClient.lightsStrengthLantern < 1)
        {
            return;
        }

        LogHandler.info("Registering tick handlers");
		
        TickRegistry.registerTickHandler(new LightingHandler.LightsManager(), Side.CLIENT);

        if (ConfigClient.lightsIntervalPlayer >= 0)
        {
            TickRegistry.registerTickHandler(new LightingHandler.PlayerLight(), Side.CLIENT);
        }

        if (ConfigClient.lightsIntervalOthers >= 0)
        {
            TickRegistry.registerTickHandler(new LightingHandler.OtherLight(), Side.CLIENT);
        }
    }
}
