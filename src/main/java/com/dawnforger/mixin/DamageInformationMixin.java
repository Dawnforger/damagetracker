package com.damagetracker.mixin;

import com.damagetracker.DamageTracker;
import com.damagetracker.SourceLabelResolver;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Mixin into the DamageInformation inner class used by Mine & Slash.
 * Uses reflection at runtime so no compile-time dependency on M&S is required.
 */
@Mixin(targets = "com.robertx22.mine_and_slash.vanilla_mc.packets.interaction.IParticleSpawnMaterial$DamageInformation", remap = false)
public abstract class DamageInformationMixin {

    @Inject(method = "spawnOnClient(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
    private void damageoverlay$afterSpawn(Entity target, CallbackInfo ci) {
        try {
            Object self = this;
            Class<?> clazz = self.getClass();
            Object dmgmapObj = null;
            for (String f : new String[] {"dmgmap", "dmgByElement", "map"}) {
                try {
                    var fld = clazz.getDeclaredField(f);
                    fld.setAccessible(true);
                    Object o = fld.get(self);
                    if (o instanceof java.util.Map) {
                        dmgmapObj = o;
                        break;
                    }
                } catch (NoSuchFieldException ignored) {}
            }

            float total = 0f;
            Map<String, Float> elements = new HashMap<>();

            if (dmgmapObj instanceof java.util.Map<?, ?> m) {
                for (var ent : m.entrySet()) {
                    Object key = ((java.util.Map.Entry<?, ?>) ent).getKey();
                    Object val = ((java.util.Map.Entry<?, ?>) ent).getValue();
                    if (val instanceof Number n) {
                        float f = n.floatValue();
                        total += f;
                        String elName = key == null ? "Unknown" : key.toString();
                        elements.merge(elName, f, Float::sum);
                    }
                }
            }

            String label = SourceLabelResolver.consumeCurrentSpellOr("Basic/Unknown");
            System.out.println("[DamageTracker] DamageInformationMixin: total=" + total + " target=" + (target!=null?target.getId():"null") + " label=" + label + " elems=" + elements);

            if (total > 0f) {
                DamageTracker.INSTANCE.addDamage(label, total, elements);
            }
        } catch (Throwable t) {
            System.out.println("[DamageTracker] DamageInformationMixin failed: " + t);
        }
    }
}
