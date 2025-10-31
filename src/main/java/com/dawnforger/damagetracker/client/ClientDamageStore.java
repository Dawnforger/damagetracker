package com.dawnforger.damagetracker.client;

import java.util.ArrayDeque;
import java.util.Deque;

public final class ClientDamageStore {

    private static final int WINDOW_MS = 10_000; // 10s
    private static final Deque<Entry> ENTRIES = new ArrayDeque<>();

    private ClientDamageStore() {}

    public static void clear() { ENTRIES.clear(); }

    public static void record(double finalAmount, String sourceTag, double critMultiplier, String targetName) {
        if (finalAmount <= 0) return;
        long now = System.currentTimeMillis();
        ENTRIES.addLast(new Entry(now, finalAmount, critMultiplier, targetName == null ? "" : targetName, sourceTag == null ? "chat" : sourceTag));
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

    private static void trim(long now) {
        while (!ENTRIES.isEmpty() && now - ENTRIES.peekFirst().ts > WINDOW_MS) ENTRIES.removeFirst();
    }

    private record Entry(long ts, double amount, double crit, String target, String src) {}
}
