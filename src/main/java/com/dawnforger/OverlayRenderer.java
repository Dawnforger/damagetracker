package com.dawnforger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

/**
 * Renders the damage tracker overlay
 */
public class OverlayRenderer {
    private static boolean visible = true;
    
    public static void toggleVisibility() {
        visible = !visible;
    }
    
    public static boolean isVisible() {
        return visible;
    }
    
    @SubscribeEvent
    public void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!visible) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        GuiGraphics guiGraphics = event.getGuiGraphics();
        Font font = mc.font;
        
        int x = DamageTrackerConfig.OVERLAY_X.get();
        int y = DamageTrackerConfig.OVERLAY_Y.get();
        int width = DamageTrackerConfig.OVERLAY_WIDTH.get();
        int height = DamageTrackerConfig.OVERLAY_HEIGHT.get();
        
        // Draw background
        guiGraphics.fill(x, y, x + width, y + height, 0x80000000); // Semi-transparent black
        
        // Draw border
        guiGraphics.fill(x, y, x + width, y + 1, 0xFF00AAFF); // Top
        guiGraphics.fill(x, y + height - 1, x + width, y + height, 0xFF00AAFF); // Bottom
        guiGraphics.fill(x, y, x + 1, y + height, 0xFF00AAFF); // Left
        guiGraphics.fill(x + width - 1, y, x + width, y + height, 0xFF00AAFF); // Right
        
        // Draw title
        String title = "Damage Tracker (" + DamageTrackerConfig.TIME_WINDOW.get() + "s)";
        guiGraphics.drawString(font, title, x + 5, y + 5, 0xFFFFFF);
        
        // Get damage statistics
        DamageTracker tracker = DamageTracker.getInstance();
        List<DamageStats> stats = tracker.getAggregatedStats();
        double totalDamage = tracker.getTotalDamage();
        
        if (totalDamage == 0) {
            guiGraphics.drawString(font, "No damage recorded", x + 5, y + 20, 0xAAAAAA);
            return;
        }
        
        // Draw total damage
        String totalText = String.format("Total: %.0f", totalDamage);
        guiGraphics.drawString(font, totalText, x + 5, y + 20, 0xFFFF00);
        
        // Draw damage bars
        int barStartY = y + 35;
        int barHeight = 16;
        int barSpacing = 2;
        int maxBars = Math.min(stats.size(), (height - 40) / (barHeight + barSpacing));
        
        for (int i = 0; i < maxBars && i < stats.size(); i++) {
            DamageStats stat = stats.get(i);
            int barY = barStartY + i * (barHeight + barSpacing);
            
            // Calculate bar width based on percentage of total damage
            double percentage = (stat.getTotalDamage() / totalDamage) * 100.0;
            int barWidth = (int) ((width - 10) * (stat.getTotalDamage() / totalDamage));
            
            // Draw bar background
            guiGraphics.fill(x + 5, barY, x + width - 5, barY + barHeight, 0x40FFFFFF);
            
            // Draw colored bar (different colors for top sources)
            int barColor = getBarColor(i);
            guiGraphics.fill(x + 5, barY, x + 5 + barWidth, barY + barHeight, barColor);
            
            // Draw text
            String sourceName = truncateString(stat.getSource(), 15);
            String damageText = String.format("%.0f", stat.getTotalDamage());
            
            if (DamageTrackerConfig.SHOW_PERCENTAGE.get()) {
                damageText += String.format(" (%.1f%%)", percentage);
            }
            
            if (DamageTrackerConfig.SHOW_DPS.get()) {
                damageText += String.format(" - %.1f DPS", stat.getDps());
            }
            
            // Draw source name
            guiGraphics.drawString(font, sourceName, x + 8, barY + 4, 0xFFFFFF);
            
            // Draw damage amount (right-aligned)
            int textWidth = font.width(damageText);
            guiGraphics.drawString(font, damageText, x + width - textWidth - 8, barY + 4, 0xFFFFFF);
        }
    }
    
    private int getBarColor(int index) {
        int[] colors = {
            0xFFFF4444, // Red
            0xFFFF8844, // Orange
            0xFFFFDD44, // Yellow
            0xFF44FF44, // Green
            0xFF44DDFF, // Cyan
            0xFF4488FF, // Blue
            0xFFDD44FF, // Purple
            0xFFFF44DD  // Pink
        };
        return colors[index % colors.length];
    }
    
    private String truncateString(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}
