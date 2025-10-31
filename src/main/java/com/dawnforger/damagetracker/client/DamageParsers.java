package com.dawnforger.damagetracker.client;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DamageParsers {

    private static final List<Pattern> PATTERNS = new ArrayList<>();

    static {
        // Exact format from screenshot:
        // "[Name] applied 1419.65 ✦ Lightning Damage with Electrify"
        // "[Name] dealt 1708.44 ✦ Multi-Element Damage with Basic Attack"
        PATTERNS.add(Pattern.compile(
            "^\[[^\]]+\]\s+(?:applied|dealt)\s+([\d,]+(?:\.\d+)?)\s*[\p{S}\p{P}]?\s+[A-Za-z\- ]+\s+Damage\b.*$",
            Pattern.CASE_INSENSITIVE));

        // More forgiving: "... applied 12,345.6 * Element Damage ..."
        PATTERNS.add(Pattern.compile(
            "\b(?:applied|dealt)\s+([\d,]+(?:\.\d+)?)\b.*?\bDamage\b",
            Pattern.CASE_INSENSITIVE));

        // Generic fallbacks kept from v1
        PATTERNS.add(Pattern.compile(
            "\byou dealt\s+([\d,]+(?:\.\d+)?)\s+(?:damage|dmg)\b",
            Pattern.CASE_INSENSITIVE));

        PATTERNS.add(Pattern.compile(
            "\bhit\s+[A-Za-z _-]+\s+for\s+([\d,]+(?:\.\d+)?)",
            Pattern.CASE_INSENSITIVE));

        PATTERNS.add(Pattern.compile(
            "\bdamage:\s*([\d,]+(?:\.\d+)?)",
            Pattern.CASE_INSENSITIVE));

        PATTERNS.add(Pattern.compile(
            "\b([\d,]+(?:\.\d+)?)\s*(?:dmg|damage)\b",
            Pattern.CASE_INSENSITIVE));
    }

    private DamageParsers() {}

    /** Normalize whitespace and odd spaces before parsing. */
    public static String normalize(String s) {
        if (s == null) return "";
        // replace non-breaking space and collapse whitespace
        return s.replace('\u00A0',' ').replaceAll("\\s+", " ").trim();
    }

    /** Try to parse a line and, on success, push into ClientDamageStore. */
    public static void tryRecordFromLine(String line) {
        if (line == null || line.isEmpty()) return;
        for (Pattern p : PATTERNS) {
            Matcher m = p.matcher(line);
            if (!m.find()) continue;

            double amount = extractAmount(m);
            if (amount > 0) {
                ClientDamageStore.record(amount, "chat", 1.0, null);
                return; // stop after first match
            }
        }
    }

    private static double extractAmount(Matcher m) {
        // First numeric capture group is the amount
        for (int i = 1; i <= m.groupCount(); i++) {
            String g = m.group(i);
            if (g != null && g.matches("[\\d,]+(?:\\.\\d+)?")) {
                return Double.parseDouble(g.replace(",", ""));
            }
        }
        // As a last resort, scan the entire matched string for a number
        Matcher any = Pattern.compile("([\\d,]+(?:\\.\\d+)?)").matcher(m.group(0));
        if (any.find()) {
            return Double.parseDouble(any.group(1).replace(",", ""));
        }
        return -1;
    }
}
