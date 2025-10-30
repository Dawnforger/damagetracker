# Developer Guide

A comprehensive guide for developers who want to modify, extend, or contribute to the Damage Tracker mod.

## Table of Contents

1. [Development Environment Setup](#development-environment-setup)
2. [Project Architecture](#project-architecture)
3. [Building and Testing](#building-and-testing)
4. [Extending the Mod](#extending-the-mod)
5. [Common Modifications](#common-modifications)
6. [Debugging](#debugging)
7. [Best Practices](#best-practices)

## Development Environment Setup

### Prerequisites

- **JDK 17 or later** (Amazon Corretto, Adoptium, or Oracle)
- **Git** (optional, for version control)
- **IDE**: IntelliJ IDEA (recommended) or Eclipse

### Initial Setup

1. **Clone or download the project**
   ```bash
   git clone <repository-url>
   cd minecraft-damage-tracker
   ```

2. **Generate IDE files**

   For IntelliJ IDEA:
   ```bash
   ./gradlew genIntellijRuns
   ```

   For Eclipse:
   ```bash
   ./gradlew eclipse
   ```

3. **Import into your IDE**

   **IntelliJ IDEA:**
   - File → Open → Select project folder
   - Wait for Gradle sync to complete

   **Eclipse:**
   - File → Import → Existing Gradle Project
   - Select project folder

4. **Run the development client**
   ```bash
   ./gradlew runClient
   ```

   Or use the IDE's run configuration (usually called "runClient")

## Project Architecture

### Package Structure

```
com.dawnforger
├── DamageTrackerMod          # Main mod class
├── DamageTracker             # Singleton tracking system
├── DamageEntry               # Individual damage event
├── DamageStats               # Aggregated statistics
├── DamageTrackerConfig       # Forge config
├── MineAndSlashIntegration   # Event handler
├── OverlayRenderer           # GUI rendering
└── KeyBindingHandler         # Input handling
```

### Component Responsibilities

| Component | Purpose | Modify When... |
|-----------|---------|----------------|
| DamageTrackerMod | Initialization | Adding new event handlers |
| DamageTracker | Data management | Changing storage logic |
| DamageEntry | Data model | Adding fields to track |
| DamageStats | Aggregation | Adding new calculations |
| DamageTrackerConfig | Settings | Adding new options |
| MineAndSlashIntegration | Event capture | Improving M&S integration |
| OverlayRenderer | Display | Changing appearance |
| KeyBindingHandler | Input | Adding new controls |

## Building and Testing

### Build Commands

```bash
# Clean build artifacts
./gradlew clean

# Build the mod
./gradlew build

# Build without tests
./gradlew build -x test

# Refresh dependencies
./gradlew build --refresh-dependencies
```

### Running Tests

```bash
# Run in development environment
./gradlew runClient

# Run with specific Minecraft arguments
./gradlew runClient --args="--username TestPlayer"
```

### Debug Mode

Enable debug logging in development:

1. Edit `src/main/resources/log4j2.xml` (create if needed):
   ```xml
   <Logger level="debug" name="com.dawnforger"/>
   ```

2. Use logger in code:
   ```java
   DamageTrackerMod.LOGGER.debug("Debug message: {}", value);
   ```

## Extending the Mod

### Adding a New Configuration Option

1. **Add to DamageTrackerConfig.java:**
   ```java
   public static final ForgeConfigSpec.BooleanValue MY_NEW_OPTION;
   
   static {
       // ... existing config ...
       
       MY_NEW_OPTION = BUILDER
               .comment("Description of my new option")
               .define("myNewOption", true);
   }
   ```

2. **Use in your code:**
   ```java
   if (DamageTrackerConfig.MY_NEW_OPTION.get()) {
       // Do something
   }
   ```

### Adding a New Keybinding

1. **Add to KeyBindingHandler.java:**
   ```java
   public static KeyMapping myNewKey;
   
   public static void register(RegisterKeyMappingsEvent event) {
       // ... existing keys ...
       
       myNewKey = new KeyMapping(
               "key.damagetracker.mynewkey",
               KeyConflictContext.IN_GAME,
               InputConstants.Type.KEYSYM,
               InputConstants.KEY_M,
               CATEGORY
       );
       event.register(myNewKey);
   }
   
   @SubscribeEvent
   public void onClientTick(TickEvent.ClientTickEvent event) {
       if (event.phase == TickEvent.Phase.END) {
           if (myNewKey.consumeClick()) {
               // Handle key press
           }
       }
   }
   ```

2. **Add translation in en_us.json:**
   ```json
   {
       "key.damagetracker.mynewkey": "My New Action"
   }
   ```

### Adding a New Damage Statistic

1. **Extend DamageStats.java:**
   ```java
   public class DamageStats {
       // ... existing fields ...
       private double criticalDamage;
       
       public void addCriticalDamage(double amount) {
           this.criticalDamage += amount;
       }
       
       public double getCriticalDamage() {
           return criticalDamage;
       }
   }
   ```

2. **Track in MineAndSlashIntegration.java:**
   ```java
   private void extractDamageSource(...) {
       // ... existing code ...
       
       if (isCritical(event)) {
           stats.addCriticalDamage(damage);
       }
   }
   ```

3. **Display in OverlayRenderer.java:**
   ```java
   String critText = String.format("Crits: %.0f", stat.getCriticalDamage());
   guiGraphics.drawString(font, critText, x, y, color);
   ```

## Common Modifications

### 1. Change Overlay Colors

Edit `OverlayRenderer.java`:

```java
private int getBarColor(int index) {
    int[] colors = {
        0xFFFF0000,  // Pure red
        0xFF00FF00,  // Pure green
        0xFF0000FF,  // Pure blue
        // ... add more colors ...
    };
    return colors[index % colors.length];
}
```

### 2. Add Custom Damage Categories

Edit `MineAndSlashIntegration.java`:

```java
private String categorizeVanillaDamage(String sourceType, Player player) {
    // Add your custom categories
    if (sourceType.contains("mycustomdamage")) {
        return "Custom Damage Type";
    }
    
    // ... existing categories ...
}
```

### 3. Change Overlay Layout

Edit `OverlayRenderer.java`:

```java
@SubscribeEvent
public void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
    // Change positions, sizes, layouts
    int x = DamageTrackerConfig.OVERLAY_X.get();
    int y = DamageTrackerConfig.OVERLAY_Y.get();
    
    // Example: Vertical layout instead of horizontal
    for (int i = 0; i < stats.size(); i++) {
        int itemY = y + (i * 20);  // Stack vertically
        // ... render each item ...
    }
}
```

### 4. Add Data Export

Create new class `DataExporter.java`:

```java
package com.dawnforger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataExporter {
    public static void exportToCSV(List<DamageStats> stats, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Source,Total Damage,DPS,Hit Count\n");

            for (DamageStats stat : stats) {
                writer.write(String.format("%s,%.2f,%.2f,%d\n",
                        stat.getSource(),
                        stat.getTotalDamage(),
                        stat.getDps(),
                        stat.getHitCount()
                ));
            }
        } catch (IOException e) {
            DamageTrackerMod.LOGGER.error("Failed to export data", e);
        }
    }
}
```

### 5. Improve Mine and Slash Integration

Edit `MineAndSlashIntegration.java` to use M&S API directly:

```java
import com.robertx22.mine_and_slash.api.DamageApi;
import com.robertx22.mine_and_slash.capability.entity.EntityData;

private String tryExtractMineAndSlashSource(...) {
    // Use Mine and Slash API
    EntityData data = EntityData.get(player);
    if (data != null) {
        // Access M&S specific data
        String abilityName = data.getLastUsedAbility();
        if (abilityName != null) {
            return abilityName;
        }
    }
    
    return null;
}
```

## Debugging

### Common Issues

**Issue: Mod not loading**
- Check `logs/latest.log` for errors
- Verify all dependencies are installed
- Ensure Java version is 17+

**Issue: Events not firing**
- Check event handler registration
- Verify @SubscribeEvent annotation
- Ensure handler is registered to correct bus

**Issue: Config not working**
- Check TOML syntax
- Verify config is registered as CLIENT type
- Restart Minecraft after changes

### Debug Techniques

1. **Add logging:**
   ```java
   DamageTrackerMod.LOGGER.info("Damage recorded: {} from {}", amount, source);
   ```

2. **Use breakpoints in IDE:**
   - Set breakpoint in your code
   - Run "runClient" in debug mode
   - Step through execution

3. **Check render updates:**
   ```java
   DamageTrackerMod.LOGGER.debug("Rendering {} damage sources", stats.size());
   ```

4. **Validate data:**
   ```java
   if (damage < 0) {
       DamageTrackerMod.LOGGER.warn("Negative damage detected: {}", damage);
   }
   ```

## Best Practices

### Code Style

1. **Use descriptive names:**
   ```java
   // Good
   double totalDamageInWindow = calculateTotalDamage();
   
   // Bad
   double td = calc();
   ```

2. **Add comments for complex logic:**
   ```java
   // Calculate damage per second by dividing total damage by window duration
   // Window is in seconds, so no conversion needed
   double dps = totalDamage / windowSeconds;
   ```

3. **Handle nulls safely:**
   ```java
   if (player != null && player.level().isClientSide) {
       // Safe to proceed
   }
   ```

### Performance

1. **Avoid creating objects in render loop:**
   ```java
   // Bad
   public void onRender() {
       List<String> temp = new ArrayList<>();  // Created every frame!
   }
   
   // Good
   private final List<String> reusableList = new ArrayList<>();
   public void onRender() {
       reusableList.clear();
       // Reuse existing list
   }
   ```

2. **Use efficient data structures:**
   ```java
   // For frequent lookups: HashMap
   Map<String, DamageStats> statsMap = new HashMap<>();
   
   // For ordered access: LinkedHashMap
   Map<String, DamageStats> orderedStats = new LinkedHashMap<>();
   ```

3. **Batch operations:**
   ```java
   // Remove old entries in batch
   damageHistory.removeIf(entry -> !entry.isWithinWindow(currentTime, window));
   ```

### Testing

1. **Test edge cases:**
   - Zero damage
   - Negative damage (should it be allowed?)
   - Very large numbers
   - Empty damage sources

2. **Test with different configs:**
   - Minimum time window (10s)
   - Maximum time window (60s)
   - Large overlay sizes
   - Small overlay sizes

3. **Test mod compatibility:**
   - Other UI mods
   - Other damage mods
   - Different Mine and Slash versions

## Version Control

### Git Workflow

```bash
# Create feature branch
git checkout -b feature/my-new-feature

# Make changes and commit
git add .
git commit -m "Add new feature: description"

# Push to remote
git push origin feature/my-new-feature

# Create pull request on GitHub
```

### Commit Messages

Follow conventional commits:
```
feat: Add critical damage tracking
fix: Resolve overlay rendering bug
docs: Update installation guide
refactor: Optimize damage aggregation
```

## Release Process

1. **Update version in build.gradle:**
   ```gradle
   version = '1.1.0'
   ```

2. **Update CHANGELOG.md:**
   ```markdown
   ## [1.1.0] - 2025-11-01
   ### Added
   - Critical damage tracking
   ```

3. **Build release:**
   ```bash
   ./gradlew clean build
   ```

4. **Test thoroughly:**
   - Install in clean Minecraft instance
   - Test all features
   - Verify config works

5. **Tag release:**
   ```bash
   git tag -a v1.1.0 -m "Release version 1.1.0"
   git push origin v1.1.0
   ```

6. **Publish:**
   - Upload JAR to CurseForge
   - Create GitHub release
   - Update documentation

## Resources

### Forge Documentation
- [Forge Docs](https://docs.minecraftforge.net/)
- [Forge Forums](https://forums.minecraftforge.net/)

### Minecraft Development
- [Minecraft Wiki](https://minecraft.fandom.com/wiki/Minecraft_Wiki)
- [Forge MDK](https://github.com/MinecraftForge/MinecraftForge)

### Mine and Slash
- [Mine and Slash Wiki](https://github.com/RobertSkalko/Mine-And-Slash-Rework/wiki)
- [CurseForge Page](https://www.curseforge.com/minecraft/mc-mods/mine-and-slash)

## Getting Help

1. **Check existing documentation:**
   - This guide
   - README.md
   - INSTALLATION.md
   - Code comments

2. **Search for similar issues:**
   - GitHub issues
   - Forge forums
   - Minecraft modding Discord

3. **Ask for help:**
   - Open GitHub issue
   - Post on Forge forums
   - Join modding Discord communities

## Contributing

See the main README.md for contribution guidelines.

**Happy modding! 🎮**
