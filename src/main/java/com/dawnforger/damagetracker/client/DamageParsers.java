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

    public static void tryRecordFromLine(String line) {
        tryRecordFromLine(line, null);
    }

    public static void tryRecordFromLine(String line, String hover) {
        if (line == null || line.isEmpty()) return;
        RegexProfile.Parsed p = RegexProfile.tryParse(line);
        if (p != null && p.amount > 0) {
            LOG.info("[DT] parsed amount={} element='{}' skill='{}'", String.format("%.2f", p.amount), p.element, p.skill);
            ClientDamageStore.recordWithDetails(p.amount, "chat", 1.0, p.element, p.skill, hover);
        } else {
            LOG.debug("[DT] no match for: {}", line);
        }
    }
}
