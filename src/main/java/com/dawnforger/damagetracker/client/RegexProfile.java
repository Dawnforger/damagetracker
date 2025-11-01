package com.dawnforger.damagetracker.client;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexProfile {

    public static final class Parsed {
        public final double amount;
        public final String element;
        public final String skill;
        public Parsed(double amount, String element, String skill) {
            this.amount = amount;
            this.element = element == null ? "" : element;
            this.skill = skill == null ? "" : skill;
        }
    }

    private static final List<Pattern> PATS = new ArrayList<>();

    static {
        // Variant A: optional [Name] prefix
        PATS.add(Pattern.compile(
                "^(?:\\[[^\\]]+\\]\\s+)?(?:applied|dealt)\\s+([\\d,]+(?:\\.\\d+)?)\\s+\\S+\\s+([A-Za-z\\- ]+?)\\s+Damage(?:\\s+with\\s+(.+))?$",
                Pattern.CASE_INSENSITIVE));
        // Variant B: loose fallback with optional 'with <skill>'
        PATS.add(Pattern.compile(
                "(?:applied|dealt)\\s+([\\d,]+(?:\\.\\d+)?)\\b.*?([A-Za-z\\- ]+?)\\s+Damage(?:\\s+with\\s+(.+))?",
                Pattern.CASE_INSENSITIVE));
        // Variant C: last resort - capture just the number
        PATS.add(Pattern.compile(
                "(?:applied|dealt)\\s+([\\d,]+(?:\\.\\d+)?)\\b.*?Damage",
                Pattern.CASE_INSENSITIVE));
    }

    private RegexProfile() {}

    public static Parsed tryParse(String line) {
        if (line == null || line.isEmpty()) return null;
        for (Pattern p : PATS) {
            Matcher m = p.matcher(line);
            if (!m.find()) continue;

            Double amount = null;
            String element = "";
            String skill = "";
            if (m.groupCount() >= 1) {
                String g1 = m.group(1);
                if (g1 != null && !g1.isEmpty()) {
                    try { amount = Double.parseDouble(g1.replace(",", "")); } catch (Exception ignored) {}
                }
            }
            if (amount == null) {
                Matcher any = Pattern.compile("([\\d,]+(?:\\.\\d+)?)").matcher(m.group(0));
                if (any.find()) {
                    try { amount = Double.parseDouble(any.group(1).replace(",", "")); } catch (Exception ignored) {}
                }
            }
            if (amount == null || amount <= 0) continue;

            if (m.groupCount() >= 2 && m.group(2) != null) element = m.group(2).trim();
            if (m.groupCount() >= 3 && m.group(3) != null) skill = m.group(3).trim();
            if (skill.length() > 60) skill = skill.substring(0, 60);
            return new Parsed(amount, element, skill);
        }
        return null;
    }
}
