package com.dawnforger.damagetracker.client;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "damagetracker", bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue TIME_WINDOW_MS;
    public static final ForgeConfigSpec.IntValue OVERLAY_X;
    public static final ForgeConfigSpec.IntValue OVERLAY_Y;
    public static final ForgeConfigSpec.IntValue OVERLAY_MAX_WIDTH;
    public static final ForgeConfigSpec.IntValue OVERLAY_MAX_HEIGHT;
    public static final ForgeConfigSpec.BooleanValue OVERLAY_ENABLED_DEFAULT;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("overlay");
        TIME_WINDOW_MS = b.comment("Rolling DPS window in milliseconds")
                .defineInRange("timeWindowMs", 10_000, 1000, 120_000);
        OVERLAY_X = b.comment("Overlay X position (pixels from left)")
                .defineInRange("x", 10, 0, 10000);
        OVERLAY_Y = b.comment("Overlay Y position (pixels from top)")
                .defineInRange("y", 10, 0, 10000);
        OVERLAY_MAX_WIDTH = b.comment("Overlay max width (text/bars may wrap later)")
                .defineInRange("maxWidth", 260, 80, 1000);
        OVERLAY_MAX_HEIGHT = b.comment("Overlay max height (not strictly enforced)")
                .defineInRange("maxHeight", 200, 40, 2000);
        OVERLAY_ENABLED_DEFAULT = b.comment("Overlay enabled on startup")
                .define("enabledByDefault", true);
        b.pop();
        SPEC = b.build();

        // Register the CLIENT config
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SPEC, "damagetracker-client.toml");
    }

    private ClientConfig() {}

    // (Optional) react to config reloads if needed
    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading e) {}
}
