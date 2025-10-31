package com.dawnforger.damagetracker.client;

import com.dawnforger.damagetracker.DamageTracker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = DamageTracker.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DamageChatTap {

    private static final Logger LOG = LogManager.getLogger("DamageTracker.ChatTap");

    /**
     * Forge fires this for normal chat (what Mine & Slash uses).
     * We read the plain string, try to parse a damage number, and, if present,
     * add it to the rolling window so the overlay can render totals.
     */
    @SubscribeEvent(receiveCanceled = true)
    public static void onClientChat(ClientChatReceivedEvent event) {
        // Get a raw, color-stripped string
        final String raw = event.getMessage().getString();
        if (raw == null || raw.isEmpty()) {
            return;
        }

        // Quick INFO so you can see the tap is working at default log level
        LOG.info("[DT] ChatTap saw: {}", raw);

        Double dmg = DamageParsers.tryParseDamage(raw);
        if (dmg != null && dmg > 0) {
            double after = DamageService.getInstance().recordDamage("CHAT", dmg);
            // Leave this at DEBUG to avoid spam once you’re confident
            LOG.debug("[DT] Parsed damage from {}: \"{}\" (rollingTotalNow: {})", "CHAT", raw, after);
        }
    }
}
