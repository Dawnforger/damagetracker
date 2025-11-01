package com.dawnforger.damagetracker.client;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientDamageStore {

    private static final Deque<Hit> HITS = new ArrayDeque<>();
    private static final Map<String, Double> BY_LABEL = new HashMap<>();

    // Remember last-seen element per label (label is skill if present, otherwise element)
    private static final Map<String, String> ELEMENT_BY_LABEL = new HashMap<>();

    // Latest hover/details per skill (for tooltip/report)
    private static final Map<String, String> DETAILS_BY_SKILL = new HashMap<>();

    private static final Map<String, Long> RECENT = new ConcurrentHashMap<>();

    private static long lastPruneMs = 0L;
    private static long lastEventRealMs = 0L; // timestamp of last recorded damage

    private ClientDamageStore() {}

    private static final class Hit {
        final long t;         // real wall-clock when recorded
        final double amt;
        final String label;
        Hit(long t, double amt, String label) { this.t = t; this.amt = amt; this.label = label; }
    }

    public static void clearAll() {
        HITS.clear();
        BY_LABEL.clear();
        ELEMENT_BY_LABEL.clear();
        DETAILS_BY_SKILL.clear();
        RECENT.clear();
        lastPruneMs = 0L;
        lastEventRealMs = 0L;
    }

    // Back-compat alias used by Keybinds
    public static void clear() { clearAll(); }

    private static String makeLabel(String element, String skill) {
        if (skill != null && !skill.isEmpty()) return skill;
        if (element != null && !element.isEmpty()) return element;
        return "(Unknown)";
    }

    private static String makeSig(double amount, String element, String skill, String verb) {
        long rounded = Math.round(amount);
        String e = element == null ? "" : element;
        String s = skill == null ? "" : skill;
        String v = verb == null ? "" : verb;
        return v + "|" + e + "|" + s + "|" + rounded;
    }

    /** Effective clock: after idleFreezeMs without new damage, time stops advancing. */
    private static long effectiveNow() {
        final long realNow = System.currentTimeMillis();
        final int idleFreeze = ClientConfig.IDLE_FREEZE_MS.get();
        if (idleFreeze <= 0 || lastEventRealMs == 0L) return realNow;
        long idle = realNow - lastEventRealMs;
        if (idle > idleFreeze) return lastEventRealMs + idleFreeze;
        return realNow;
    }

    private static void pruneOld(long nowMs, int windowMs) {
        if (nowMs - lastPruneMs < 25) return;
        lastPruneMs = nowMs;

        while (!HITS.isEmpty()) {
            Hit h = HITS.peekFirst();
            if (nowMs - h.t > windowMs) {
                HITS.pollFirst();
                BY_LABEL.merge(h.label, -h.amt, Double::sum);
                if (BY_LABEL.getOrDefault(h.label, 0.0) <= 1e-6) BY_LABEL.remove(h.label);
            } else break;
        }

        // expire dedupe signatures
        int dedupeMs = ClientConfig.DEDUPE_MS.get();
        Iterator<Map.Entry<String, Long>> it = RECENT.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> e = it.next();
            if (nowMs - e.getValue() > Math.max(dedupeMs, 500)) it.remove();
        }
    }

    public static void recordWithDetails(double amount, String source, double weight,
                                         String element, String skill, String hover,
                                         String verb) {
        final long realNow = System.currentTimeMillis();
        final int window = ClientConfig.TIME_WINDOW_MS.get();

        lastEventRealMs = realNow;

        // Dedupe within DEDUPE_MS
        String sig = makeSig(amount, element, skill, verb);
        long last = RECENT.getOrDefault(sig, 0L);
        if (realNow - last <= ClientConfig.DEDUPE_MS.get()) {
            if (ClientConfig.PREFER_DEALT_OVER_APPLIED.get() && !"dealt".equals(verb)) return;
        }
        RECENT.put(sig, realNow);

        if (ClientConfig.PREFER_DEALT_OVER_APPLIED.get() && "dealt".equals(verb)) {
            String appliedSig = makeSig(amount, element, skill, "applied");
            RECENT.put(appliedSig, realNow); // mark applied as superseded
        }

        String label = makeLabel(element, skill);
        Hit h = new Hit(realNow, amount * weight, label);
        HITS.addLast(h);
        BY_LABEL.merge(label, h.amt, Double::sum);

        // Remember element and details for UI
        if (element != null && !element.isEmpty()) {
            ELEMENT_BY_LABEL.put(label, element);
        }
        if (hover != null && !hover.isEmpty() && skill != null && !skill.isEmpty()) {
            DETAILS_BY_SKILL.put(skill, hover);
        }

        pruneOld(effectiveNow(), window);
    }

    public static double totalInWindowMs(int windowMs) {
        long now = effectiveNow();
        pruneOld(now, windowMs);
        double sum = 0.0;
        for (Hit h : HITS) sum += h.amt;
        return Math.max(0.0, sum);
    }

    public static List<Pair> topElements(int windowMs, int topN) {
        long now = effectiveNow();
        pruneOld(now, windowMs);
        ArrayList<Pair> out = new ArrayList<>();
        for (Map.Entry<String, Double> e : BY_LABEL.entrySet()) {
            if (e.getValue() > 0) out.add(new Pair(e.getKey(), e.getValue()));
        }
        out.sort((a,b) -> Double.compare(b.value, a.value));
        if (out.size() > topN) return out.subList(0, topN);
        return out;
    }

    public static double dps(int windowMs) {
        long now = effectiveNow();
        pruneOld(now, windowMs);

        if (HITS.isEmpty()) return 0.0;
        long oldest = HITS.peekFirst().t;
        long spanMs = Math.max(1, Math.min(windowMs, now - oldest));
        double total = 0.0;
        for (Hit h : HITS) total += h.amt;
        return total / (spanMs / 1000.0);
    }

    public static final class Pair {
        public final String key;
        public final double value;
        public Pair(String key, double value) { this.key = key; this.value = value; }
    }

    // ------------ UI-facing models/APIs -----------------

    /** Row model the UI expects. */
    public static final class Row {
        public final String label;
        public final double total;
        public final double dps;
        public final String element; // NEW: element name for color mapping

        public Row(String label, double total, double dps, String element) {
            this.label = label;
            this.total = total;
            this.dps = dps;
            this.element = element;
        }
    }

    /** Sum for current configured window; used by DamageOverlay/DamageReportScreen. */
    public static double totalForDisplay() {
        return totalInWindowMs(ClientConfig.TIME_WINDOW_MS.get());
    }

    /** DPS for current configured window; uses active-span denominator with idle-freeze. */
    public static double dpsForDisplay() {
        return dps(ClientConfig.TIME_WINDOW_MS.get());
    }

    /** Top-N skills/elements for current window as Row objects (with per-row DPS + element). */
    public static List<Row> topSkillsForDisplay(int topN) {
        int window = ClientConfig.TIME_WINDOW_MS.get();
        List<Pair> pairs = topElements(window, topN);
        double windowSeconds;
        long now = effectiveNow();
        long oldest = Long.MAX_VALUE;
        if (!HITS.isEmpty()) oldest = HITS.peekFirst().t;
        if (oldest == Long.MAX_VALUE) {
            windowSeconds = window / 1000.0;
        } else {
            long spanMs = Math.max(1, Math.min(window, now - oldest));
            windowSeconds = spanMs / 1000.0;
        }

        ArrayList<Row> rows = new ArrayList<>();
        for (Pair p : pairs) {
            double perDps = windowSeconds > 0 ? (p.value / windowSeconds) : 0.0;
            String element = ELEMENT_BY_LABEL.getOrDefault(p.key, "");
            rows.add(new Row(p.key, p.value, perDps, element));
        }
        return rows;
    }

    /** Latest hover/details blob for a given skill (may be empty). */
    public static String latestDetailsForSkill(String skill) {
        if (skill == null) return "";
        String v = DETAILS_BY_SKILL.get(skill);
        return v == null ? "" : v;
    }
}
