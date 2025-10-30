package com.damagetracker.mixin;

import com.damagetracker.SourceLabelResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Capture the spell GUID when the client is told an entity is casting a spell.
 */
@Mixin(targets = "com.robertx22.mine_and_slash.vanilla_mc.packets.spells.TellClientEntityIsCastingSpellPacket", remap = false)
public abstract class TellClientEntityIsCastingSpellPacketMixin {
    @Inject(method = "onReceived(Lcom/robertx22/library_of_exile/packets/ExilePacketContext;)V",
            at = @At("TAIL"))
    private void damageoverlay$onClientReceived(Object ctx, CallbackInfo ci) {
        try {
            Object pkt = this;
            var spellField = pkt.getClass().getDeclaredField("spellid");
            spellField.setAccessible(true);
            String spellGuid = (String) spellField.get(pkt);

            var enidField = pkt.getClass().getDeclaredField("enid");
            enidField.setAccessible(true);
            int casterEntityId = (int) enidField.get(pkt);

            SourceLabelResolver.setCurrentSpellIfLocalPlayer(casterEntityId, spellGuid);
            System.out.println("[DamageTracker] Captured casting packet spellGuid=" + spellGuid + " caster=" + casterEntityId);
        } catch (Throwable t) {
            System.out.println("[DamageTracker] TellClientEntityIsCastingSpellPacketMixin failed: " + t);
        }
    }
}
