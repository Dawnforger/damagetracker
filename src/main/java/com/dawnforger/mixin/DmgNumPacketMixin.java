package com.damagetracker.mixin;

import com.damagetracker.DamageTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hook into DmgNumPacket as a fallback if needed.
 */
@Mixin(targets = "com.robertx22.mine_and_slash.vanilla_mc.packets.DmgNumPacket", remap = false)
public abstract class DmgNumPacketMixin {
    @Inject(method = "handleClient(Lcom/robertx22/library_of_exile/packets/ExilePacketContext;)V", at = @At("TAIL"))
    private void damageoverlay$onHandleClient(Object ctx, CallbackInfo ci) {
        try {
            Object pkt = this;
            float dmg = 0f;
            try {
                var f = pkt.getClass().getDeclaredField("dmg");
                f.setAccessible(true);
                Object v = f.get(pkt);
                if (v instanceof Number) dmg = ((Number)v).floatValue();
            } catch (Throwable ignored) {}

            System.out.println("[DamageTracker] DmgNumPacket received dmg=" + dmg);
            if (dmg > 0f) {
                DamageTracker.INSTANCE.addDamage("DmgNumPacket", dmg);
            }
        } catch (Throwable t) {
            System.out.println("[DamageTracker] DmgNumPacketMixin failed: " + t);
        }
    }
}
