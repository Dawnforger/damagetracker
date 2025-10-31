package com.dawnforger.damagetracker.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "damagetracker", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Keybinds {

    public static KeyMapping TOGGLE_OVERLAY;
    public static KeyMapping CLEAR_WINDOW;

    private Keybinds() {}

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent e) {
        TOGGLE_OVERLAY = new KeyMapping("key.damagetracker.toggle", GLFW.GLFW_KEY_K, "key.categories.misc");
        CLEAR_WINDOW   = new KeyMapping("key.damagetracker.clear",  GLFW.GLFW_KEY_L, "key.categories.misc");
        e.register(TOGGLE_OVERLAY);
        e.register(CLEAR_WINDOW);
    }

    @Mod.EventBusSubscriber(modid = "damagetracker", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Runtime {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent e) {
            if (e.phase != TickEvent.Phase.END) return;
            // Only handle once per tick on the client
            if (TOGGLE_OVERLAY != null && TOGGLE_OVERLAY.consumeClick()) {
                DamageOverlay.setEnabled(!DamageOverlay.isEnabled());
            }
            if (CLEAR_WINDOW != null && CLEAR_WINDOW.consumeClick()) {
                ClientDamageStore.clear();
            }
        }
    }
}
