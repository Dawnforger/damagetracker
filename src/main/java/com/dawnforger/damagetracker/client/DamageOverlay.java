package com.dawnforger.damagetracker.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE, modid = "damagetracker")
public final class DamageOverlay {

    private static boolean ENABLED = true;
    private static boolean INIT = false;

    private DamageOverlay() {}

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post evt) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (!INIT) {
            ENABLED = ClientConfig.OVERLAY_ENABLED_DEFAULT.get();
            INIT = true;
        }
        if (!ENABLED) return;

        final int x = ClientConfig.OVERLAY_X.get();
        int y = ClientConfig.OVERLAY_Y.get();
        final int windowMs = ClientConfig.TIME_WINDOW_MS.get();
        final boolean rolling = ClientConfig.ROLLING_WINDOW_ENABLED.get();
        final int topN = ClientConfig.TOP_N_SOURCES.get();

        double total = ClientDamageStore.totalForDisplay();
        double dps = ClientDamageStore.dpsForDisplay();

        GuiGraphics gg = evt.getGuiGraphics();
        Font font = mc.font;

        String title = "Damage Tracker (" + (rolling ? (windowMs/1000) + "s rolling" : "manual") + ")";
        gg.drawString(font, title, x, y, 0xFFFFAA00, false); y += 12;
        gg.drawString(font, String.format("Total: %.0f   DPS: %.1f", total, dps), x, y, 0xFFFFFFFF, false); y += 12;

        // Top sources
        List<ClientDamageStore.Row> rows = ClientDamageStore.topSkillsForDisplay(topN);
        for (ClientDamageStore.Row r : rows) {
            int color = ElementColors.colorFor(r.element);
            gg.drawString(font, String.format("• %s: %.0f", r.label, r.total), x + 6, y, color, false);
            y += 12;
        }
    }

    public static void setEnabled(boolean enabled) { ENABLED = enabled; }
    public static boolean isEnabled() { return ENABLED; }
}
