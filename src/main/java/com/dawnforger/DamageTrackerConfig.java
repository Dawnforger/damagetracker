package com.dawnforger;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Configuration for the Damage Tracker mod
 */
public class DamageTrackerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    
    public static final ForgeConfigSpec.IntValue TIME_WINDOW;
    public static final ForgeConfigSpec.IntValue OVERLAY_X;
    public static final ForgeConfigSpec.IntValue OVERLAY_Y;
    public static final ForgeConfigSpec.IntValue OVERLAY_WIDTH;
    public static final ForgeConfigSpec.IntValue OVERLAY_HEIGHT;
    public static final ForgeConfigSpec.BooleanValue SHOW_DPS;
    public static final ForgeConfigSpec.BooleanValue SHOW_PERCENTAGE;
    public static final ForgeConfigSpec.IntValue MAX_SOURCES_DISPLAYED;
    
    static {
        BUILDER.push("Damage Tracker Configuration");
        
        TIME_WINDOW = BUILDER
                .comment("Time window in seconds for damage tracking (10-60)")
                .defineInRange("timeWindow", 30, 10, 60);
        
        OVERLAY_X = BUILDER
                .comment("X position of overlay")
                .defineInRange("overlayX", 10, 0, 10000);
        
        OVERLAY_Y = BUILDER
                .comment("Y position of overlay")
                .defineInRange("overlayY", 10, 0, 10000);
        
        OVERLAY_WIDTH = BUILDER
                .comment("Width of overlay")
                .defineInRange("overlayWidth", 300, 100, 1000);
        
        OVERLAY_HEIGHT = BUILDER
                .comment("Height of overlay")
                .defineInRange("overlayHeight", 200, 50, 1000);
        
        SHOW_DPS = BUILDER
                .comment("Show DPS (damage per second)")
                .define("showDPS", true);
        
        SHOW_PERCENTAGE = BUILDER
                .comment("Show damage percentage")
                .define("showPercentage", true);
        
        MAX_SOURCES_DISPLAYED = BUILDER
                .comment("Maximum number of damage sources to display")
                .defineInRange("maxSourcesDisplayed", 10, 1, 50);
        
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
