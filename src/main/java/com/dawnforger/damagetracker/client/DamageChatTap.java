package com.dawnforger.damagetracker.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = "damagetracker", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DamageChatTap {
    private static final Logger LOG = LogManager.getLogger("DamageTracker/ChatTap");

    private DamageChatTap() {}

    @SubscribeEvent
    public static void onChat(ClientChatReceivedEvent event) {
        if (event == null || event.getMessage() == null) return;
        String raw = event.getMessage().getString();
        if (raw == null || raw.isEmpty()) return;

        LOG.info("[DT] ChatTap saw: {}", raw);

        String lower = raw.toLowerCase();
        if (lower.startsWith("target dummy:")) return;

        if (ClientConfig.REQUIRE_SELF_ONLY.get()) {
            String you = "[you]";
            String player = null;
            try {
                if (Minecraft.getInstance().player != null) {
                    player = "[" + Minecraft.getInstance().player.getName().getString() + "]";
                }
            } catch (Throwable ignored) {}
            String rawLower = raw.toLowerCase();
            boolean isSelf = (player != null && rawLower.contains(player.toLowerCase())) || rawLower.contains(you);
            if (!isSelf) return;
        }

        String verb = lower.contains("dealt") ? "dealt" : (lower.contains("applied") ? "applied" : "");
        DamageParsers.tryRecordFromLineWithVerb(raw, verb);
    }
}
