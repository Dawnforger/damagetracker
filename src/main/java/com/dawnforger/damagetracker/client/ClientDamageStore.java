package com.dawnforger.damagetracker.client;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClientDamageStore {

    // Ring buffer for rolling mode; manual mode ignores timestamp trimming until clear()
    private static final int DEFAULT_WINDOW_MS = 10_000;
    private static final Deque<Entry> ENTRIES = new ArrayDeque<>();

    private ClientDamageStore() {}

    public static void clear() { ENTRIES.clear(); }

    /** legacy record signature */
    public static void record(double finalAmount, String sourceTag, double critMultiplier, String targetName) {
        record(finalAmount, sourceTag, critMultiplier, "", "");
    }

    /** record with element + skill name */
    public static void record(double finalAmount, String sourceTag, double critMultiplier, String element, String skill) {
        if (finalAmount <= 0) return;
        long now = System.currentTimeMillis();
        ENTRIES.addLast(new Entry(now, finalAmount, critMultiplier, nz(element), nz(skill), nz(sourceTag)));
        trimIfRolling(now);
    }

    private static void trimIfRolling(long now) {
        if (!ClientConfig.ROLLING_WINDOW_ENABLED.get()) return;
        int window = ClientConfig.TIME_WINDOW_MS.get();
        int win = Math.max(1000, window);
        while (!ENTRIES.isEmpty() && now - ENTRIES.peekFirst().ts > win) ENTRIES.removeFirst();
    }

    /** total used by overlay depending on mode */
    public static double totalForDisplay() {
        if (ClientConfig.ROLLING_WINDOW_ENABLED.get()) {
            return totalInWindowMs(ClientConfig.TIME_WINDOW_MS.get());
        } else {
            double sum = 0.0;
            for (Entry e : ENTRIES) sum += e.amount;
            return sum;
        }
    }

    public static double dpsForDisplay() {
        if (ClientConfig.ROLLING_WINDOW_ENABLED.get()) {
            int w = Math.max(1000, ClientConfig.TIME_WINDOW_MS.get());
            return totalInWindowMs(w) * 1000.0 / w;
        } else {
            // manual mode: DPS since last clear = total / elapsed
            if (ENTRIES.isEmpty()) return 0.0;
            long start = ENTRIES.peekFirst().ts;
            long now = System.currentTimeMillis();
            long elapsed = Math.max(1_000L, now - start);
            return totalForDisplay() * 1000.0 / elapsed;
        }
    }

    public static double totalInWindowMs(int windowMs) {
        long now = System.currentTimeMillis();
        long cutoff = now - Math.max(1000, windowMs);
        double sum = 0.0;
        for (Entry e : ENTRIES) if (e.ts >= cutoff) sum += e.amount;
        return sum;
    }

    /** Top skills for overlay. Returns up to topN items, aggregated by skill (label) and with remembered element. */
    public static List<Row> topSkillsForDisplay(int topN) {
        topN = Math.max(1, Math.min(15, topN));
        long now = System.currentTimeMillis();
        boolean rolling = ClientConfig.ROLLING_WINDOW_ENABLED.get();
        long cutoff = rolling ? (now - Math.max(1000, ClientConfig.TIME_WINDOW_MS.get())) : Long.MIN_VALUE;

        Map<String, Agg> agg = new HashMap<>();
        for (Entry e : ENTRIES) {
            if (e.ts < cutoff) continue;
            String key = e.skill.isEmpty() ? "(Unknown)" : e.skill;
            Agg a = agg.getOrDefault(key, new Agg());
            a.total += e.amount;
            a.firstElement = a.firstElement == null || a.firstElement.isEmpty() ? e.element : a.firstElement;
            agg.put(key, a);
        }
        // convert + sort
        List<Row> rows = new ArrayList<>();
        for (Map.Entry<String, Agg> en : agg.entrySet()) {
            rows.add(new Row(en.getKey(), en.getValue().firstElement == null ? "" : en.getValue().firstElement, en.getValue().total));
        }
        rows.sort((a,b) -> Double.compare(b.total, a.total));
        if (rows.size() > topN) return new ArrayList<>(rows.subList(0, topN));
        return rows;
    }

    private static String nz(String s) { return s == null ? "" : s; }

    private static final class Agg { double total = 0; String firstElement = ""; }

    public static final class Row {
        public final String label;   // skill name
        public final String element; // element string for color
        public final double total;   // total damage contributed
        public Row(String label, String element, double total) {
            this.label = label;
            this.element = element;
            this.total = total;
        }
    }

    private record Entry(long ts, double amount, double crit, String element, String skill, String src) {}
}
