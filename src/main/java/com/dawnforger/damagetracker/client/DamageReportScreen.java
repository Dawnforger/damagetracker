package com.dawnforger.damagetracker.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class DamageReportScreen extends Screen {

    private static final Logger LOG = LogManager.getLogger("DamageTracker/Report");
    private int scroll = 0;
    private static final int LINE_HEIGHT = 12;

    public DamageReportScreen() {
        super(Component.literal("Damage Report"));
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        try {
            super.render(gg, mouseX, mouseY, partialTick);
            int x = 20;
            int y = 20;
            var font = Minecraft.getInstance().font;

            double total = ClientDamageStore.totalForDisplay();
            double dps = ClientDamageStore.dpsForDisplay();
            boolean rolling = ClientConfig.ROLLING_WINDOW_ENABLED.get();
            int windowMs = ClientConfig.TIME_WINDOW_MS.get();

            gg.drawString(font, "Damage Report", x, y, 0xFFFFFF00, false); y += LINE_HEIGHT;
            gg.drawString(font, String.format("Mode: %s   Window: %ds   Total: %.0f   DPS: %.1f",
                    rolling ? "Rolling" : "Manual", windowMs/1000, total, dps), x, y, 0xFFFFFFFF, false); y += LINE_HEIGHT;

            gg.drawString(font, "Top Skills (press J to close):", x, y, 0xFFB0D0FF, false); y += LINE_HEIGHT;

            List<ClientDamageStore.Row> rows = ClientDamageStore.topSkillsForDisplay(15);
            int visible = Math.max(1, (this.height - y - 20) / LINE_HEIGHT);
            int end = Math.min(rows.size(), scroll + visible);

            for (int i = scroll; i < end; i++) {
                ClientDamageStore.Row r = rows.get(i);
                int color = ElementColors.colorFor(r.element);
                gg.drawString(font, String.format("%2d. %-24s  total=%.0f", i+1, r.label, r.total), x, y, color, false);
                y += LINE_HEIGHT;
            }

            if (rows.size() > visible) {
                gg.drawString(font, String.format("Scroll: %d/%d", scroll + 1, Math.max(1, (rows.size()+visible-1)/visible)), x, this.height - 16, 0xFFAAAAAA, false);
            }
        } catch (Throwable t) {
            LOG.error("[DT] Report screen render error", t);
            onClose();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int total = ClientDamageStore.topSkillsForDisplay(15).size();
        int visible = Math.max(1, (this.height - 20 - 20 - 12*3) / LINE_HEIGHT);
        if (delta < 0) scroll = Math.min(Math.max(0, total - 1), scroll + 1);
        if (delta > 0) scroll = Math.max(0, scroll - 1);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        try {
            if (Keybinds.OPEN_REPORT != null &&
                Keybinds.OPEN_REPORT.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
                onClose();
                return true;
            }
        } catch (Throwable t) {
            LOG.error("[DT] Report screen key handling error", t);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
