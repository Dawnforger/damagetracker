package com.dawnforger.damagetracker.client;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public final class ClientDamageStore {

    private static final Deque<Entry> ENTRIES = new ArrayDeque<>();

    private ClientDamageStore() {}

    public static void clear() { ENTRIES.clear(); }

    public static void record(double finalAmount, String sourceTag, double critMultiplier, String targetName) {
        recordWithDetails(finalAmount, sourceTag, critMultiplier, "", "", null);
    }

    public static void recordWithDetails(double finalAmount, String sourceTag, double critMultiplier, String element, String skill, String details) {
        if (finalAmount <= 0) return;
        long now = System.currentTimeMillis();
        ENTRIES.addLast(new Entry(now, finalAmount, critMultiplier, nz(element), nz(skill), nz(sourceTag), details == null ? "" : details));
        trimIfRolling(now);
    }

    private static void trimIfRolling(long now) {
        if (!ClientConfig.ROLLING_WINDOW_ENABLED.get()) return;
        int window = ClientConfig.TIME_WINDOW_MS.get();
        int win = Math.max(1000, window);
        while (!ENTRIES.isEmpty() && now - ENTRIES.peekFirst().ts > win) ENTRIES.removeFirst();
    }

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

    public static String latestDetailsForSkill(String skillOrElement) {
        if (skillOrElement == null || skillOrElement.isEmpty()) return null;
        for (var it = ENTRIES.descendingIterator(); it.hasNext(); ) {
            Entry e = it.next();
            String key = labelFor(e);
            if (skillOrElement.equals(key) && e.details != null && !e.details.isEmpty()) return e.details;
        }
        return null;
    }

    public static List<Row> topSkillsForDisplay(int topN) {
        topN = Math.max(1, Math.min(15, topN));
        long now = System.currentTimeMillis();
        boolean rolling = ClientConfig.ROLLING_WINDOW_ENABLED.get();
        long cutoff = rolling ? (now - Math.max(1000, ClientConfig.TIME_WINDOW_MS.get())) : Long.MIN_VALUE;

        Map<String, Agg> agg = new HashMap<>();
        for (Entry e : ENTRIES) {
            if (e.ts < cutoff) continue;
            String key = labelFor(e); // prefer skill, else element, else "(Unknown)"
            Agg a = agg.getOrDefault(key, new Agg());
            a.total += e.amount;
            a.element = a.element.isEmpty() ? e.element : a.element; // retain first seen element for color
            agg.put(key, a);
        }
        List<Row> rows = new ArrayList<>();
        for (Map.Entry<String, Agg> en : agg.entrySet()) {
            rows.add(new Row(en.getKey(), en.getValue().element, en.getValue().total));
        }
        rows.sort((a,b) -> Double.compare(b.total, a.total));
        if (rows.size() > topN) return new ArrayList<>(rows.subList(0, topN));
        return rows;
    }

    private static String labelFor(Entry e) {
        if (e.skill != null && !e.skill.isEmpty()) return e.skill;
        if (e.element != null && !e.element.isEmpty()) return e.element;
        return "(Unknown)";
    }

    private static String nz(String s) { return s == null ? "" : s; }
    private static final class Agg { double total = 0; String element = ""; }

    public static final class Row {
        public final String label;
        public final String element;
        public final double total;
        public Row(String label, String element, double total) { this.label = label; this.element = element; this.total = total; }
    }

    private record Entry(long ts, double amount, double crit, String element, String skill, String src, String details) {}
}
