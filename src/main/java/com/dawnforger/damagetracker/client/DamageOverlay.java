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

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE, modid = "damagetracker")
public final class DamageOverlay {

    private static final Logger LOG = LogManager.getLogger("DamageTracker/Overlay");

    private static boolean ENABLED = true;
    private static long lastLogMs = 0L;
    private static final int WINDOW_MS = 10_000;

    private DamageOverlay() {}

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post evt) {
        if (!ENABLED) return;

        GuiGraphics gg = evt.getGuiGraphics();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        double total = ClientDamageStore.totalInWindowMs(WINDOW_MS);
        double dps = total / (WINDOW_MS / 1000.0);

        long now = System.currentTimeMillis();
        if (now - lastLogMs > 1000) {
            lastLogMs = now;
            LOG.info("[DT] Overlay poll: total10s={} dps={}", String.format("%.1f", total), String.format("%.1f", dps));
        }

        Font font = mc.font;
        final int x = 10;
        final int y = 10;
        gg.drawString(font, "Damage Tracker (10s)", x, y, 0xFFFFAA00, false);
        gg.drawString(font, String.format("Total: %.0f   DPS: %.1f", total, dps), x, y + 12, 0xFFFFFFFF, false);
    }

    public static void setEnabled(boolean enabled) { ENABLED = enabled; }
    public static boolean isEnabled() { return ENABLED; }
}
