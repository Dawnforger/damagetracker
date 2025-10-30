package com.dawnforger;

/**
 * Represents aggregated damage statistics for a specific source
 */
public class DamageStats implements Comparable<DamageStats> {
    private final String source;
    private double totalDamage;
    private int hitCount;
    private double dps;
    
    public DamageStats(String source) {
        this.source = source;
        this.totalDamage = 0;
        this.hitCount = 0;
        this.dps = 0;
    }
    
    public void addDamage(double amount) {
        this.totalDamage += amount;
        this.hitCount++;
    }
    
    public void calculateDPS(long windowSeconds) {
        if (windowSeconds > 0) {
            this.dps = totalDamage / windowSeconds;
        }
    }
    
    public String getSource() {
        return source;
    }
    
    public double getTotalDamage() {
        return totalDamage;
    }
    
    public int getHitCount() {
        return hitCount;
    }
    
    public double getDps() {
        return dps;
    }
    
    public void reset() {
        this.totalDamage = 0;
        this.hitCount = 0;
        this.dps = 0;
    }
    
    @Override
    public int compareTo(DamageStats other) {
        return Double.compare(other.totalDamage, this.totalDamage); // Descending order
    }
}
