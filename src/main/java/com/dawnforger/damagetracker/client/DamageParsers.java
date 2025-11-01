package com.dawnforger.damagetracker.client;

public final class DamageParsers {

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
            ClientDamageStore.recordWithDetails(p.amount, "chat", 1.0, p.element, p.skill, hover);
        }
    }
}
