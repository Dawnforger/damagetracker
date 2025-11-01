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
        // Primary:
        //   <verb> <amount> <icons/nonletters> <Element> Damage with <Skill>
        // Nonletters between amount and element are skipped with [^A-Za-z\\r\\n]* so icons like "?" never break the match.
        PATS.add(Pattern.compile(
                "(?i)^.*?(?:applied|dealt)\\s+([\\d,]+(?:\\.\\d+)?)\\s+[^A-Za-z\\r\\n]*([A-Za-z][A-Za-z\\- ]+?)\\s+Damage\\s+with\\s+([A-Za-z'\\- ]+)\\s*$"
        ));

        // Secondary: capture Element only (no skill present)
        PATS.add(Pattern.compile(
                "(?i)^.*?(?:applied|dealt)\\s+([\\d,]+(?:\\.\\d+)?)\\s+[^A-Za-z\\r\\n]*([A-Za-z][A-Za-z\\- ]+?)\\s+Damage\\s*$"
        ));

        // Fallback: amount only so we never drop a line
        PATS.add(Pattern.compile(
                "(?i)(?:applied|dealt)\\s+([\\d,]+(?:\\.\\d+)?)\\b.*?Damage"
        ));
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

            if (m.groupCount() >= 2 && m.group(2) != null) element = tidy(m.group(2));
            if (m.groupCount() >= 3 && m.group(3) != null) skill = tidy(m.group(3));
            return new Parsed(amount, element, skill);
        }
        return null;
    }

    private static String tidy(String s) {
        if (s == null) return "";
        s = s.trim();
        while (!s.isEmpty() && ".!,;:".indexOf(s.charAt(s.length()-1)) >= 0) {
            s = s.substring(0, s.length()-1).trim();
        }
        return s;
    }
}
