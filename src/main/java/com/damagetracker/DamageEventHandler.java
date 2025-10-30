package com.damagetracker;

import com.robertx22.mine_and_slash.event_hooks.damage_hooks.OnNonPlayerDamageEntityEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handles damage events from Mine and Slash
 */
public class DamageEventHandler {
    
    /**
     * Handles Mine and Slash damage events
     * This integrates with Mine and Slash's damage system
     */
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        // Check if damage source is a player
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            
            // Only track damage dealt by the local player
            if (player.level().isClientSide && player == net.minecraft.client.Minecraft.getInstance().player) {
                float amount = event.getAmount();
                String source = getDamageSource(event);
                
                // Record the damage
                DamageTracker.getInstance().recordDamage(source, amount);
            }
        }
    }
    
    /**
     * Attempts to determine the damage source from the event
     * This will be enhanced to integrate with Mine and Slash's ability system
     */
    private String getDamageSource(LivingHurtEvent event) {
        String sourceType = event.getSource().getMsgId();
        
        // Check for Mine and Slash ability damage
        // Note: This is a simplified version. Full integration requires accessing
        // Mine and Slash's damage metadata which is passed through NBT or custom fields
        
        if (sourceType.contains("magic")) {
            return "Magic Damage";
        } else if (sourceType.contains("arrow")) {
            return "Ranged Attack";
        } else if (sourceType.contains("player")) {
            return "Melee Attack";
        } else if (sourceType.contains("explosion")) {
            return "Explosion";
        } else if (sourceType.contains("fire")) {
            return "Fire Damage";
        }
        
        // Default fallback
        return formatSourceName(sourceType);
    }
    
    /**
     * Format source name to be more readable
     */
    private String formatSourceName(String source) {
        if (source == null || source.isEmpty()) {
            return "Unknown";
        }
        
        // Convert snake_case or camelCase to Title Case
        String formatted = source.replaceAll("_", " ")
                                .replaceAll("([a-z])([A-Z])", "$1 $2");
        
        // Capitalize first letter of each word
        String[] words = formatted.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase())
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
}
