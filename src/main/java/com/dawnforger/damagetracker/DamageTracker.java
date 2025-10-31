package com.dawnforger.damagetracker;

import net.minecraftforge.fml.common.Mod;

/**
 * Minimal Forge entry point. Required so Forge discovers the mod declared in mods.toml.
 * Client-only logic lives under com.dawnforger.damagetracker.client.
 */
@Mod(DamageTracker.MODID)
public final class DamageTracker {
    public static final String MODID = "damagetracker";

    public DamageTracker() {
        // No common setup needed. Client-only subscribers live in the .client package.
    }
}
