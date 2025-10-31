package com.dawnforger.damagetracker.client;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "damagetracker", bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue TIME_WINDOW_MS;
    public static final ForgeConfigSpec.BooleanValue ROLLING_WINDOW_ENABLED;
    public static final ForgeConfigSpec.IntValue TOP_N_SOURCES; // 1..15
    public static final ForgeConfigSpec.IntValue OVERLAY_X;
    public static final ForgeConfigSpec.IntValue OVERLAY_Y;
    public static final ForgeConfigSpec.IntValue OVERLAY_MAX_WIDTH;
    public static final ForgeConfigSpec.IntValue OVERLAY_MAX_HEIGHT;
    public static final ForgeConfigSpec.BooleanValue OVERLAY_ENABLED_DEFAULT;

    // Element colors (ARGB) — stored as signed ints, so allow full int range
    public static final ForgeConfigSpec.IntValue COLOR_PHYSICAL;
    public static final ForgeConfigSpec.IntValue COLOR_FIRE;
    public static final ForgeConfigSpec.IntValue COLOR_LIGHTNING;
    public static final ForgeConfigSpec.IntValue COLOR_COLD;
    public static final ForgeConfigSpec.IntValue COLOR_NATURE;
    public static final ForgeConfigSpec.IntValue COLOR_SHADOW;
    public static final ForgeConfigSpec.IntValue COLOR_HOLY;
    public static final ForgeConfigSpec.IntValue COLOR_MULTI;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("overlay");
        TIME_WINDOW_MS = b.comment("Rolling DPS window in milliseconds (used when rollingWindowEnabled=true)")
                .defineInRange("timeWindowMs", 10_000, 1000, 300_000);
        ROLLING_WINDOW_ENABLED = b.comment("If true, use a rolling window; if false, accumulate until cleared by keybind")
                .define("rollingWindowEnabled", true);
        TOP_N_SOURCES = b.comment("How many top sources (skills/abilities) to list on overlay (1-15)")
                .defineInRange("topNSources", 5, 1, 15);
        OVERLAY_X = b.defineInRange("x", 10, 0, 10000);
        OVERLAY_Y = b.defineInRange("y", 10, 0, 10000);
        OVERLAY_MAX_WIDTH = b.defineInRange("maxWidth", 260, 80, 1000);
        OVERLAY_MAX_HEIGHT = b.defineInRange("maxHeight", 260, 80, 2000);
        OVERLAY_ENABLED_DEFAULT = b.define("enabledByDefault", true);
        b.pop();

        b.push("colors");
        // IMPORTANT: ARGB ints like 0xFFxxxxxx are NEGATIVE in Java int.
        // Use the *full* signed 32-bit range for min/max to avoid range errors.
        final int MIN = Integer.MIN_VALUE;
        final int MAX = Integer.MAX_VALUE;
        COLOR_PHYSICAL  = b.defineInRange("physical",  0xFFCCCCCC, MIN, MAX);
        COLOR_FIRE      = b.defineInRange("fire",      0xFFFF6B5A, MIN, MAX);
        COLOR_LIGHTNING = b.defineInRange("lightning", 0xFFFFE066, MIN, MAX);
        COLOR_COLD      = b.defineInRange("cold",      0xFF66CCFF, MIN, MAX);
        COLOR_NATURE    = b.defineInRange("nature",    0xFF6EDC6E, MIN, MAX);
        COLOR_SHADOW    = b.defineInRange("shadow",    0xFFB266FF, MIN, MAX);
        COLOR_HOLY      = b.defineInRange("holy",      0xFFFFF3B0, MIN, MAX);
        COLOR_MULTI     = b.defineInRange("multi",     0xFFB0B0B0, MIN, MAX);
        b.pop();

        SPEC = b.build();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SPEC, "damagetracker-client.toml");
    }

    private ClientConfig() {}

    @SubscribeEvent
    public static void onReload(net.minecraftforge.fml.event.config.ModConfigEvent.Reloading e) {}
}
