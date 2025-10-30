package com.dawnforger;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handles keybindings for the damage tracker
 */
public class KeyBindingHandler {
    private static final String CATEGORY = "Damage Tracker";
    
    public static KeyMapping toggleOverlayKey;
    public static KeyMapping clearDataKey;
    
    public static void register(RegisterKeyMappingsEvent event) {
        toggleOverlayKey = new KeyMapping(
                "key.damagetracker.toggle",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_K, // Default: K key
                CATEGORY
        );
        
        clearDataKey = new KeyMapping(
                "key.damagetracker.clear",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_L, // Default: L key
                CATEGORY
        );
        
        event.register(toggleOverlayKey);
        event.register(clearDataKey);
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Check for toggle overlay key
            if (toggleOverlayKey.consumeClick()) {
                OverlayRenderer.toggleVisibility();
            }
            
            // Check for clear data key
            if (clearDataKey.consumeClick()) {
                DamageTracker.getInstance().clear();
            }
        }
    }
}
