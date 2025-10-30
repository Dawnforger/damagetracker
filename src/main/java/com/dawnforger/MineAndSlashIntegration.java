package com.dawnforger;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Enhanced damage event handler with Mine and Slash integration
 * 
 * This handler attempts to extract detailed damage information from Mine and Slash.
 * For full integration, it hooks into both vanilla damage events and Mine and Slash's
 * custom damage system.
 */
public class MineAndSlashIntegration {
    
    /**
     * Primary damage tracking hook
     * This method is called for all damage events
     */
    @SubscribeEvent
    public void onDamageDealt(LivingHurtEvent event) {
        // Only process on client side
        if (!event.getEntity().level().isClientSide) {
            return;
        }
        
        // Check if the damage source is the player
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }
        
        // Only track damage dealt by the local player
        if (player != net.minecraft.client.Minecraft.getInstance().player) {
            return;
        }
        
        LivingEntity target = event.getEntity();
        float damage = event.getAmount();
        
        // Extract damage source information
        String damageSource = extractDamageSource(event, player, target);
        
        // Record the damage
        DamageTracker.getInstance().recordDamage(damageSource, damage);
    }
    
    /**
     * Extracts the damage source from the event
     * 
     * This method attempts to identify the source of damage by checking:
     * 1. Mine and Slash ability data (if available)
     * 2. Vanilla damage type
     * 3. Item being held
     * 4. Effect application
     */
    private String extractDamageSource(LivingHurtEvent event, Player player, LivingEntity target) {
        String sourceType = event.getSource().getMsgId();
        
        // Try to extract Mine and Slash specific data
        // Note: This requires access to Mine and Slash's damage calculation results
        // which are typically stored in NBT data or custom fields
        
        // Check for Mine and Slash damage types
        String mnsSource = tryExtractMineAndSlashSource(event, player);
        if (mnsSource != null) {
            return mnsSource;
        }
        
        // Fallback to vanilla damage categorization
        return categorizeVanillaDamage(sourceType, player);
    }
    
    /**
     * Attempts to extract Mine and Slash ability/effect information
     * 
     * Mine and Slash stores damage metadata that includes:
     * - Ability names
     * - Damage element types
     * - Spell schools
     * - Effect sources
     */
    private String tryExtractMineAndSlashSource(LivingHurtEvent event, Player player) {
        // This is a placeholder for Mine and Slash integration
        // Full integration requires accessing Mine and Slash's API
        
        // Example of what this could look like with full API access:
        /*
        if (event instanceof MineAndSlashDamageEvent mnsEvent) {
            DamageInfo damageInfo = mnsEvent.getDamageInfo();
            if (damageInfo.hasAbility()) {
                return damageInfo.getAbilityName();
            }
            if (damageInfo.hasEffect()) {
                return damageInfo.getEffectName();
            }
            return damageInfo.getElement().getName();
        }
        */
        
        // For now, we'll use NBT data if available
        try {
            // Check player's held item for ability data
            var heldItem = player.getMainHandItem();
            if (heldItem.hasTag()) {
                var tag = heldItem.getTag();
                // Mine and Slash often stores ability data in NBT
                if (tag.contains("mns_ability")) {
                    return tag.getString("mns_ability");
                }
            }
        } catch (Exception e) {
            // Silently fail if NBT access doesn't work
        }
        
        return null;
    }
    
    /**
     * Categorizes damage based on vanilla damage types
     */
    private String categorizeVanillaDamage(String sourceType, Player player) {
        // Direct damage type mapping
        if (sourceType.contains("arrow") || sourceType.contains("trident")) {
            return "Ranged Attack";
        }
        
        if (sourceType.contains("magic")) {
            return "Magic Damage";
        }
        
        if (sourceType.contains("fire") || sourceType.contains("lava")) {
            return "Fire Damage";
        }
        
        if (sourceType.contains("explosion")) {
            return "Explosion";
        }
        
        if (sourceType.contains("lightning")) {
            return "Lightning";
        }
        
        if (sourceType.contains("thorns")) {
            return "Thorns";
        }
        
        // Check held item
        var heldItem = player.getMainHandItem();
        if (!heldItem.isEmpty()) {
            String itemName = heldItem.getDisplayName().getString();
            return itemName + " (Melee)";
        }
        
        // Default
        if (sourceType.equals("player")) {
            return "Melee Attack";
        }
        
        return formatSourceName(sourceType);
    }
    
    /**
     * Formats a source name to be more readable
     */
    private String formatSourceName(String source) {
        if (source == null || source.isEmpty()) {
            return "Unknown";
        }
        
        // Remove minecraft: prefix
        source = source.replace("minecraft:", "");
        
        // Convert snake_case to Title Case
        String[] parts = source.split("_");
        StringBuilder formatted = new StringBuilder();
        
        for (String part : parts) {
            if (!part.isEmpty()) {
                formatted.append(Character.toUpperCase(part.charAt(0)))
                         .append(part.substring(1).toLowerCase())
                         .append(" ");
            }
        }
        
        return formatted.toString().trim();
    }
}
