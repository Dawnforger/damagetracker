package com.dawnforger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Core damage tracking system
 */
public class DamageTracker {
    private static final DamageTracker INSTANCE = new DamageTracker();
    
    private final Queue<DamageEntry> damageHistory = new ConcurrentLinkedQueue<>();
    private final Map<String, DamageStats> aggregatedStats = new HashMap<>();
    private double totalDamage = 0;
    
    private DamageTracker() {}
    
    public static DamageTracker getInstance() {
        return INSTANCE;
    }
    
    /**
     * Record a damage event
     */
    public void recordDamage(String source, double amount) {
        long timestamp = System.currentTimeMillis();
        damageHistory.offer(new DamageEntry(source, amount, timestamp));
    }
    
    /**
     * Get aggregated damage statistics within the configured time window
     */
    public List<DamageStats> getAggregatedStats() {
        long currentTime = System.currentTimeMillis();
        long windowMillis = DamageTrackerConfig.TIME_WINDOW.get() * 1000L;
        
        // Clear old data
        damageHistory.removeIf(entry -> !entry.isWithinWindow(currentTime, windowMillis));
        
        // Aggregate damage by source
        aggregatedStats.clear();
        totalDamage = 0;
        
        for (DamageEntry entry : damageHistory) {
            if (entry.isWithinWindow(currentTime, windowMillis)) {
                String source = entry.getSource();
                aggregatedStats.computeIfAbsent(source, DamageStats::new)
                        .addDamage(entry.getAmount());
                totalDamage += entry.getAmount();
            }
        }
        
        // Calculate DPS for each source
        long windowSeconds = DamageTrackerConfig.TIME_WINDOW.get();
        for (DamageStats stats : aggregatedStats.values()) {
            stats.calculateDPS(windowSeconds);
        }
        
        // Sort by total damage (descending) and limit to max displayed
        return aggregatedStats.values().stream()
                .sorted()
                .limit(DamageTrackerConfig.MAX_SOURCES_DISPLAYED.get())
                .collect(Collectors.toList());
    }
    
    /**
     * Get total damage in the current window
     */
    public double getTotalDamage() {
        return totalDamage;
    }
    
    /**
     * Clear all damage history
     */
    public void clear() {
        damageHistory.clear();
        aggregatedStats.clear();
        totalDamage = 0;
    }
    
    /**
     * Get the number of damage entries in history
     */
    public int getHistorySize() {
        return damageHistory.size();
    }
}
