package com.dawnforger.damagetracker.client;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientSystemChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = "damagetracker", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DamageChatTap {

    private static final Logger LOG = LogManager.getLogger("DamageTracker/ChatTap");

    private DamageChatTap() {}

    // Player/most messages
    @SubscribeEvent
    public static void onClientChat(ClientChatReceivedEvent event) {
        handle(event.getMessage(), "ClientChatReceivedEvent");
    }

    // System messages (some servers/mods route here)
    @SubscribeEvent
    public static void onSystemChat(ClientSystemChatEvent event) {
        handle(event.getMessage(), "ClientSystemChatEvent");
    }

    private static void handle(Component comp, String src) {
        if (comp == null) return;
        String raw = comp.getString();
        String norm = DamageParsers.normalize(raw);
        double before = ClientDamageStore.totalInWindowMs(10_000);
        DamageParsers.tryRecordFromLine(norm);
        double after = ClientDamageStore.totalInWindowMs(10_000);
        if (after > before) {
            LOG.debug("[DT] Parsed damage from {}: "{}" (total10s now: {})", src, norm, after);
        }
    }
}
