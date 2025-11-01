package com.dawnforger.damagetracker.client;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = "damagetracker", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DamageChatTap {

    private static final Logger LOG = LogManager.getLogger("DamageTracker/ChatTap");

    private DamageChatTap() {}

    @SubscribeEvent
    public static void onClientChat(ClientChatReceivedEvent event) {
        Component comp = event.getMessage();
        if (comp == null) return;
        String raw = comp.getString();
        if (raw == null || raw.isEmpty()) return;

        LOG.info("[DT] ChatTap saw: {}", raw);

        String norm = DamageParsers.normalize(raw);
        String hover = extractHoverText(comp);
        double before = ClientDamageStore.totalForDisplay();
        DamageParsers.tryRecordFromLine(norm, hover);
        double after = ClientDamageStore.totalForDisplay();
        if (after > before) {
            LOG.debug("[DT] Parsed + recorded damage (total now: {})", after);
        }
    }

    private static String extractHoverText(Component comp) {
        if (comp == null) return null;
        HoverEvent he = comp.getStyle().getHoverEvent();
        if (he != null && he.getAction() == HoverEvent.Action.SHOW_TEXT) {
            Component hov = he.getValue(HoverEvent.Action.SHOW_TEXT);
            if (hov != null) {
                String s = hov.getString();
                if (s != null && !s.isEmpty()) return s;
            }
        }
        for (Component sib : comp.getSiblings()) {
            String s = extractHoverText(sib);
            if (s != null && !s.isEmpty()) return s;
        }
        return null;
    }
}
