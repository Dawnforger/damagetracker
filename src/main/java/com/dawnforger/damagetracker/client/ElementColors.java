package com.dawnforger.damagetracker.client;

public final class ElementColors {

    private ElementColors() {}

    public static int colorFor(String elementName) {
        if (elementName == null || elementName.isEmpty()) return ClientConfig.COLOR_MULTI.get();
        String e = elementName.trim().toLowerCase();
        if (e.contains("lightning")) return ClientConfig.COLOR_LIGHTNING.get();
        if (e.contains("fire"))      return ClientConfig.COLOR_FIRE.get();
        if (e.contains("cold") || e.contains("ice")) return ClientConfig.COLOR_COLD.get();
        if (e.contains("nature") || e.contains("poison")) return ClientConfig.COLOR_NATURE.get();
        if (e.contains("shadow") || e.contains("dark")) return ClientConfig.COLOR_SHADOW.get();
        if (e.contains("holy") || e.contains("radiant")) return ClientConfig.COLOR_HOLY.get();
        if (e.contains("phys")) return ClientConfig.COLOR_PHYSICAL.get();
        if (e.contains("chaos")) return 0xFFAA66FF; // visible fallback for Chaos if not configured
        if (e.contains("multi")) return ClientConfig.COLOR_MULTI.get();
        return ClientConfig.COLOR_MULTI.get();
    }
}
