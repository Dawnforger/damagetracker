package com.dawnforger;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main mod class for Damage Tracker
 */
@Mod("damagetracker")
public class DamageTrackerMod {
    public static final String MOD_ID = "damagetracker";
    public static final Logger LOGGER = LogManager.getLogger();
    
    public DamageTrackerMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register config
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DamageTrackerConfig.SPEC);
        
        // Register client setup
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::clientSetup);
            modEventBus.addListener(this::registerKeyMappings);
        }
        
        LOGGER.info("Damage Tracker initialized");
    }
    
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Setting up Damage Tracker client");
        
        // Register event handlers
        MinecraftForge.EVENT_BUS.register(new MineAndSlashIntegration());
        MinecraftForge.EVENT_BUS.register(new OverlayRenderer());
        MinecraftForge.EVENT_BUS.register(new KeyBindingHandler());
        
        LOGGER.info("Damage Tracker client setup complete");
    }
    
    private void registerKeyMappings(final RegisterKeyMappingsEvent event) {
        KeyBindingHandler.register(event);
        LOGGER.info("Registered key mappings");
    }
}
