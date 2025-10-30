package com.dawnforger;

/**
 * Represents a single damage event
 */
public class DamageEntry {
    private final String source;
    private final double amount;
    private final long timestamp;
    
    public DamageEntry(String source, double amount, long timestamp) {
        this.source = source;
        this.amount = amount;
        this.timestamp = timestamp;
    }
    
    public String getSource() {
        return source;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public boolean isWithinWindow(long currentTime, long windowMillis) {
        return (currentTime - timestamp) <= windowMillis;
    }
}
