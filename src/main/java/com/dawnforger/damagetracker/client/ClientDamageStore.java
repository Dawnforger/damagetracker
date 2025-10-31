package com.dawnforger.damagetracker.client;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClientDamageStore {

    private static final int WINDOW_MS = 10_000;
    private static final Deque<Entry> ENTRIES = new ArrayDeque<>();

    private ClientDamageStore() {}

    public static void clear() { ENTRIES.clear(); }

    // Backwards-compatible record() used by existing parser:
    public static void record(double finalAmount, String sourceTag, double critMultiplier, String targetName) {
        record(finalAmount, sourceTag, critMultiplier, "", "");
    }

    // Enhanced record with element and skill
    public static void record(double finalAmount, String sourceTag, double critMultiplier, String element, String skill) {
        if (finalAmount <= 0) return;
        long now = System.currentTimeMillis();
        ENTRIES.addLast(new Entry(now, finalAmount, critMultiplier, nz(element), nz(skill), nz(sourceTag)));
        trim(now);
    }

    public static double dps() {
        long now = System.currentTimeMillis();
        trim(now);
        double sum = 0.0;
        for (Entry e : ENTRIES) sum += e.amount;
        return sum * 1000.0 / WINDOW_MS;
    }

    public static double totalInWindowMs(int windowMs) {
        long now = System.currentTimeMillis();
        long cutoff = now - Math.max(1000, windowMs);
        trim(now);
        double sum = 0.0;
        for (Entry e : ENTRIES) if (e.ts >= cutoff) sum += e.amount;
        return sum;
    }

    public static List<Pair> topElements(int windowMs, int max) {
        long now = System.currentTimeMillis();
        long cutoff = now - Math.max(1000, windowMs);
        Map<String, Double> acc = new HashMap<>();
        for (Entry e : ENTRIES) {
            if (e.ts < cutoff) continue;
            if (!e.element.isEmpty()) acc.merge(e.element, e.amount, Double::sum);
        }
        return topK(acc, max);
    }

    public static List<Pair> topSkills(int windowMs, int max) {
        long now = System.currentTimeMillis();
        long cutoff = now - Math.max(1000, windowMs);
        Map<String, Double> acc = new HashMap<>();
        for (Entry e : ENTRIES) {
            if (e.ts < cutoff) continue;
            if (!e.skill.isEmpty()) acc.merge(e.skill, e.amount, Double::sum);
        }
        return topK(acc, max);
    }

    private static List<Pair> topK(Map<String, Double> acc, int max) {
        List<Pair> list = new ArrayList<>();
        for (Map.Entry<String, Double> en : acc.entrySet()) list.add(new Pair(en.getKey(), en.getValue()));
        list.sort((a,b) -> Double.compare(b.value, a.value));
        if (list.size() > max) return new ArrayList<>(list.subList(0, max));
        return list;
    }

    private static void trim(long now) {
        while (!ENTRIES.isEmpty() && now - ENTRIES.peekFirst().ts > WINDOW_MS) ENTRIES.removeFirst();
    }

    private static String nz(String s) { return s == null ? "" : s; }

    public static final class Pair {
        public final String label;
        public final double value;
        public Pair(String l, double v) { this.label = l; this.value = v; }
    }

    private record Entry(long ts, double amount, double crit, String element, String skill, String src) {}
}
