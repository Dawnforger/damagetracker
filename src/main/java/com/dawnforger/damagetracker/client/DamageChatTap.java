package com.dawnforger.damagetracker.client;

import net.minecraft.network.chat.Component;
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

        // Visible at INFO so you can confirm the tap is firing
        LOG.info("[DT] ChatTap saw: {}", raw);

        String norm = DamageParsers.normalize(raw);
        double before = ClientDamageStore.totalInWindowMs(10_000);
        DamageParsers.tryRecordFromLine(norm);
        double after = ClientDamageStore.totalInWindowMs(10_000);
        if (after > before) {
            LOG.debug("[DT] Parsed damage from CHAT: \"{}\" (total10s now: {})", norm, after);
        }
    }
}
