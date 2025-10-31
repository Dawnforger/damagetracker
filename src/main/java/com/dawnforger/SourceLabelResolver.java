package com.dawnforger;

import net.minecraft.client.Minecraft;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Lightweight runtime cache that remembers the last spell GUID
 * the local player cast and exposes a sticky getter used by mixins.
 *
 * This is intentionally minimal: it does NOT load Mine & Slash types
 * at class load time and only keeps a short sticky window.
 */
public final class SourceLabelResolver {

    private static final AtomicReference<String> LAST_SPELL_GUID = new AtomicReference<>("");
    private static volatile long lastSpellMs = 0L;
    // how long the last spell remains associated with subsequent damage (ms)
    private static final long STICKY_MS = 3000L;

    private SourceLabelResolver() {}

    /**
     * Called by the TellClientEntityIsCastingSpellPacket mixin when a cast
     * packet for an entity is received on the client.
     *
     * @param casterEntityId entity id that is casting the spell
     * @param spellGuid the GUID/ID of the spell (as a string)
     */
    public static void setCurrentSpellIfLocalPlayer(int casterEntityId, String spellGuid) {
        var mc = Minecraft.getInstance();
        if (mc == null || mc.level == null || mc.player == null) return;
        if (mc.level.getEntity(casterEntityId) == mc.player) {
            LAST_SPELL_GUID.set(spellGuid == null ? "" : spellGuid);
            lastSpellMs = System.currentTimeMillis();
        }
    }

    /**
     * Return a label for the current spell, or the provided fallback.
     * The label is sticky for STICKY_MS milliseconds after the last set.
     *
     * @param fallback the fallback label when no recent spell exists
     * @return label to attribute damage to
     */
    public static String consumeCurrentSpellOr(String fallback) {
        String val = LAST_SPELL_GUID.get();
        if (val == null || val.isEmpty()) return fallback;
        if (System.currentTimeMillis() - lastSpellMs > STICKY_MS) return fallback;
        return "Spell:" + val;
    }

    /**
     * Optional: clear current stored spell (if you want non-sticky behavior).
     */
    public static void clear() {
        LAST_SPELL_GUID.set("");
        lastSpellMs = 0L;
    }
}
