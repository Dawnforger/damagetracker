package com.dawnforger.damagetracker.client;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Heuristic chat parser for Mine & Slash lines that may contain icons or odd spacing.
 * ONLY processes real damage lines (must contain " Damage").
 */
public final class HeuristicChatParser {

    public static final class Parsed {
        public final double amount;
        public final String element;
        public final String skill;
        Parsed(double amount, String element, String skill) {
            this.amount = amount;
            this.element = element == null ? "" : element;
            this.skill = skill == null ? "" : skill;
        }
    }

    private static final Pattern NUM = Pattern.compile("([\\d,]+(?:\\.\\d+)?)");
    private static final String[] ELEMENTS = new String[]{
            "Multi-Element","Lightning","Fire","Cold","Nature","Shadow","Holy","Physical","Chaos"
    };

    private HeuristicChatParser() {}

    public static Parsed parse(String line) {
        if (line == null || line.isEmpty()) return null;

        // --------- HARD FILTERS (skip non-damage spam like target dummy DPS) ----------
        String lower = line.toLowerCase(Locale.ROOT);
        // Require the literal word " damage" to be present; this excludes: "Target Dummy: 12345 DPS", exp, loot, etc.
        if (!lower.contains(" damage")) return null;
        // Optional extra guard: ignore known noisy prefixes
        if (lower.startsWith("target dummy:")) return null;

        String norm = DamageParsers.normalize(line);

        // 1) amount = first number
        Double amount = null;
        Matcher m = NUM.matcher(norm);
        if (m.find()) {
            try { amount = Double.parseDouble(m.group(1).replace(",", "")); } catch (Exception ignored) {}
        }
        if (amount == null || amount <= 0) return null;

        // 2) skill (after " with ")
        String skill = "";
        int withIdx = norm.toLowerCase(Locale.ROOT).indexOf(" with ");
        if (withIdx >= 0) {
            skill = norm.substring(withIdx + 6).trim();
            // strip trailing punctuation
            while (!skill.isEmpty() && ".!,;:".indexOf(skill.charAt(skill.length()-1)) >= 0) {
                skill = skill.substring(0, skill.length()-1).trim();
            }
        }

        // 3) element: take word(s) immediately before " Damage", else first known token in line
        int dmgIdx = norm.toLowerCase(Locale.ROOT).indexOf(" damage");
        String element = "";
        if (dmgIdx > 0) {
            int start = dmgIdx - 1;
            while (start >= 0 && isWordish(norm.charAt(start))) start--;
            String candidate = norm.substring(start + 1, dmgIdx).trim().replaceAll("\\s+", " ");
            String best = matchKnownElement(candidate);
            if (!best.isEmpty()) element = best;
        }
        if (element.isEmpty()) {
            element = firstKnownElementIn(norm);
        }

        return new Parsed(amount, element, skill);
    }

    private static boolean isWordish(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '-' || c == ' ';
    }

    private static String firstKnownElementIn(String s) {
        String lower = s.toLowerCase(Locale.ROOT);
        int bestPos = Integer.MAX_VALUE;
        String best = "";
        for (String el : ELEMENTS) {
            int pos = lower.indexOf(el.toLowerCase(Locale.ROOT));
            if (pos >= 0 && pos < bestPos) { bestPos = pos; best = el; }
        }
        return best;
    }

    private static String matchKnownElement(String s) {
        String lower = s.toLowerCase(Locale.ROOT);
        for (String el : ELEMENTS) {
            if (lower.equals(el.toLowerCase(Locale.ROOT))) return el;
        }
        for (String el : ELEMENTS) {
            if (lower.contains(el.toLowerCase(Locale.ROOT))) return el;
        }
        String[] parts = s.split("\\s+");
        if (parts.length > 0) {
            String last = parts[parts.length - 1];
            for (String el : ELEMENTS) if (last.equalsIgnoreCase(el)) return el;
        }
        return "";
    }
}
