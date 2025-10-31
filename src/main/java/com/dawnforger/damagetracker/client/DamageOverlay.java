package com.dawnforger.damagetracker.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE, modid = "damagetracker")
public final class DamageOverlay {

    private static final Logger LOG = LogManager.getLogger("DamageTracker/Overlay");

    private static boolean ENABLED = true; // Will be toggled by keybind; initial state uses config during first render
    private static long lastLogMs = 0L;

    private DamageOverlay() {}

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post evt) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // first-use: sync enabled to config default
        if (lastLogMs == 0L) {
            ENABLED = ClientConfig.OVERLAY_ENABLED_DEFAULT.get();
            lastLogMs = System.currentTimeMillis(); // also initializes the poll timer
        }
        if (!ENABLED) return;

        final int windowMs = ClientConfig.TIME_WINDOW_MS.get();
        final int x = ClientConfig.OVERLAY_X.get();
        int y = ClientConfig.OVERLAY_Y.get();

        double total = ClientDamageStore.totalInWindowMs(windowMs);
        double dps = total / (windowMs / 1000.0);

        long now = System.currentTimeMillis();
        if (now - lastLogMs > 1000) {
            lastLogMs = now;
            LOG.debug("[DT] Overlay poll: windowMs={} total={} dps={}", windowMs, String.format("%.1f", total), String.format("%.1f", dps));
        }

        GuiGraphics gg = evt.getGuiGraphics();
        Font font = mc.font;
        gg.drawString(font, "Damage Tracker (" + (windowMs/1000) + "s)", x, y, 0xFFFFAA00, false);
        y += 12;
        gg.drawString(font, String.format("Total: %.0f   DPS: %.1f", total, dps), x, y, 0xFFFFFFFF, false);
        y += 12;

        // Optional breakdowns if the enhanced store is present
        try {
            List<ClientDamageStore.Pair> elements = ClientDamageStore.topElements(windowMs, 3);
            if (!elements.isEmpty()) {
                gg.drawString(font, "Top Elements:", x, y, 0xFFB0FFC8, false);
                y += 12;
                for (ClientDamageStore.Pair p : elements) {
                    gg.drawString(font, String.format("• %s: %.0f", p.label, p.value), x + 6, y, 0xFFE0FFE0, false);
                    y += 12;
                }
            }

            List<ClientDamageStore.Pair> skills = ClientDamageStore.topSkills(windowMs, 3);
            if (!skills.isEmpty()) {
                gg.drawString(font, "Top Skills:", x, y, 0xFFB0D0FF, false);
                y += 12;
                for (ClientDamageStore.Pair p : skills) {
                    gg.drawString(font, String.format("• %s: %.0f", p.label, p.value), x + 6, y, 0xFFDDE8FF, false);
                    y += 12;
                }
            }
        } catch (Throwable ignored) {
            // If you're on the basic store (no topElements/topSkills), this silently skips breakdowns
        }
    }

    public static void setEnabled(boolean enabled) { ENABLED = enabled; }
    public static boolean isEnabled() { return ENABLED; }
}
