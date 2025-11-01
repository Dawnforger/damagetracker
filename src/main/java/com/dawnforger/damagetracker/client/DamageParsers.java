package com.dawnforger.damagetracker.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DamageParsers {

    private static final Logger LOG = LogManager.getLogger("DamageTracker/Parser");

    private DamageParsers() {}

    public static String normalize(String s) {
        if (s == null) return "";
        return s.replace('\u00A0',' ').replaceAll("\\s+", " ").trim();
    }

    public static void tryRecordFromLine(String line) { tryRecordFromLineWithVerb(line, ""); }

    public static void tryRecordFromLineWithVerb(String line, String verb) {
        if (line == null || line.isEmpty()) return;

        RegexProfile.Parsed p = RegexProfile.tryParse(line);
        if (p == null || p.amount <= 0 || (p.element.isEmpty() && p.skill.isEmpty())) {
            HeuristicChatParser.Parsed h = HeuristicChatParser.parse(line);
            if (h != null) {
                LOG.info("[DT] HEUR parsed amount={} element='{}' skill='{}' verb='{}'",
                        String.format("%.2f", h.amount), h.element, h.skill, verb);
                ClientDamageStore.recordWithDetails(h.amount, "chat", 1.0, h.element, h.skill, null, verb);
                return;
            }
        }

        if (p != null && p.amount > 0) {
            LOG.info("[DT] parsed amount={} element='{}' skill='{}' verb='{}'",
                    String.format("%.2f", p.amount), p.element, p.skill, verb);
            ClientDamageStore.recordWithDetails(p.amount, "chat", 1.0, p.element, p.skill, null, verb);
        }
    }
}
