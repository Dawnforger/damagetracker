package com.dawnforger.damagetracker.client;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "damagetracker", bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientConfig {

    public static final ForgeConfigSpec SPEC;

    // Overlay / behavior
    public static final ForgeConfigSpec.IntValue TIME_WINDOW_MS;
    public static final ForgeConfigSpec.BooleanValue ROLLING_WINDOW_ENABLED;
    public static final ForgeConfigSpec.IntValue TOP_N_SOURCES;
    public static final ForgeConfigSpec.IntValue OVERLAY_X;
    public static final ForgeConfigSpec.IntValue OVERLAY_Y;
    public static final ForgeConfigSpec.IntValue OVERLAY_MAX_WIDTH;
    public static final ForgeConfigSpec.IntValue OVERLAY_MAX_HEIGHT;
    public static final ForgeConfigSpec.BooleanValue OVERLAY_ENABLED_DEFAULT;

    // Capture/dedupe behavior
    public static final ForgeConfigSpec.BooleanValue REQUIRE_SELF_ONLY;
    public static final ForgeConfigSpec.IntValue DEDUPE_MS;
    public static final ForgeConfigSpec.BooleanValue PREFER_DEALT_OVER_APPLIED;
    public static final ForgeConfigSpec.IntValue IDLE_FREEZE_MS;

    // Colors
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
        TIME_WINDOW_MS = b.defineInRange("timeWindowMs", 10_000, 1_000, 300_000);
        ROLLING_WINDOW_ENABLED = b.define("rollingWindowEnabled", true);
        TOP_N_SOURCES = b.defineInRange("topNSources", 10, 1, 15);
        OVERLAY_X = b.defineInRange("x", 10, 0, 10_000);
        OVERLAY_Y = b.defineInRange("y", 10, 0, 10_000);
        OVERLAY_MAX_WIDTH = b.defineInRange("maxWidth", 260, 80, 1_000);
        OVERLAY_MAX_HEIGHT = b.defineInRange("maxHeight", 260, 80, 2_000);
        OVERLAY_ENABLED_DEFAULT = b.define("enabledByDefault", true);
        b.pop();

        b.push("capture");
        REQUIRE_SELF_ONLY = b.define("requireSelfOnly", true);
        DEDUPE_MS = b.defineInRange("dedupeMs", 150, 0, 1000);
        PREFER_DEALT_OVER_APPLIED = b.define("preferDealtOverApplied", true);
        IDLE_FREEZE_MS = b.defineInRange("idleFreezeMs", 1500, 0, 60_000);
        b.pop();

        b.push("colors");
        final int MIN = Integer.MIN_VALUE, MAX = Integer.MAX_VALUE;
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
}
