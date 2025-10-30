# Installation and Usage Guide

## Quick Start

### Prerequisites
1. **Minecraft Java Edition 1.20.1**
2. **Minecraft Forge 1.20.1** (version 47.2.0 or later)
3. **Mine and Slash v6.3.11** or later

### Installation Steps

1. **Install Minecraft Forge**
   - Download Forge installer from [files.minecraftforge.net](https://files.minecraftforge.net/)
   - Run the installer and select "Install client"
   - Launch Minecraft and select the Forge profile to create necessary folders

2. **Install Mine and Slash**
   - Download Mine and Slash v6.3.11 from CurseForge
   - Place the JAR file in `.minecraft/mods/` folder

3. **Install Damage Tracker**
   - Download the latest Damage Tracker release
   - Place `damagetracker-1.0.0.jar` in `.minecraft/mods/` folder

4. **Launch Minecraft**
   - Select the Forge profile
   - Click Play

## Building from Source

### Requirements
- JDK 17 (Amazon Corretto, Adoptium, or Oracle JDK)
- Git (optional, for cloning)

### Build Instructions

#### Windows
```cmd
# Navigate to the project directory
cd minecraft-damage-tracker

# Build the mod
gradlew.bat build

# The JAR file will be in build\libs\
```

#### Linux/macOS
```bash
# Navigate to the project directory
cd minecraft-damage-tracker

# Make gradle wrapper executable
chmod +x gradlew

# Build the mod
./gradlew build

# The JAR file will be in build/libs/
```

### Development Environment

#### IntelliJ IDEA
```bash
./gradlew genIntellijRuns
```
Then open the project folder in IntelliJ IDEA.

#### Eclipse
```bash
./gradlew eclipse
```
Then import the project in Eclipse.

#### Running in Development
```bash
./gradlew runClient
```

## Configuration

### First Launch
On first launch, the mod creates a configuration file at:
```
.minecraft/config/damagetracker-client.toml
```

### Configuration Options

Edit the file with any text editor:

```toml
[Damage Tracker Configuration]
    # How many seconds of damage to track (10-60)
    timeWindow = 30
    
    # Screen position (pixels from top-left)
    overlayX = 10
    overlayY = 10
    
    # Overlay dimensions
    overlayWidth = 300
    overlayHeight = 200
    
    # Display options
    showDPS = true          # Show damage per second
    showPercentage = true   # Show percentage of total
    
    # Maximum damage sources to show at once
    maxSourcesDisplayed = 10
```

### Recommended Configurations

**Minimal Overlay (Small Screen)**
```toml
overlayWidth = 250
overlayHeight = 150
maxSourcesDisplayed = 5
showDPS = false
```

**Detailed Tracking (Large Screen)**
```toml
overlayWidth = 400
overlayHeight = 300
maxSourcesDisplayed = 15
showDPS = true
showPercentage = true
```

**Long-term Tracking**
```toml
timeWindow = 60  # Track full minute
maxSourcesDisplayed = 20
```

## Controls

### Default Keybindings

| Key | Action |
|-----|--------|
| **K** | Toggle overlay visibility |
| **L** | Clear all damage data |

### Rebinding Keys

1. Open Minecraft
2. Go to **Options** → **Controls**
3. Scroll to **Damage Tracker** category
4. Click on the key binding to change it
5. Press your desired key

## Usage

### Basic Workflow

1. Launch Minecraft with the mod installed
2. Join a world or server
3. The overlay appears in the top-left corner (default)
4. Deal damage to mobs/players
5. Watch damage statistics update in real-time

### Understanding the Display

```
┌─────────────────────────────────────┐
│ Damage Tracker (30s)                │
│ Total: 1250                          │
│ ━━━━━━━━━━━━━━━━ Fireball: 450 (36%) │
│ ━━━━━━━━━━━ Magic Bolt: 350 (28%)    │
│ ━━━━━━━━ Melee Attack: 250 (20%)     │
│ ━━━━━ Fire DoT: 200 (16%)            │
└─────────────────────────────────────┘
```

- **Header**: Shows tracking time window
- **Total**: Cumulative damage in the time window
- **Bars**: Length represents damage proportion
- **Numbers**: Damage amount and percentage

### Tips for Best Results

1. **Positioning**: Adjust `overlayX` and `overlayY` to avoid UI conflicts
2. **Time Window**: Use shorter windows (10-20s) for burst damage tracking
3. **Clear Data**: Press L between combat encounters for accurate per-fight stats
4. **DPS Tracking**: Enable `showDPS = true` for sustained damage analysis

## Troubleshooting

### Mod Not Loading

**Check Forge Version**
```
Required: 47.2.0+
```
View your version in the Minecraft launcher.

**Check Dependencies**
Ensure Mine and Slash v6.3.11+ is installed.

**Check Logs**
Look for errors in `.minecraft/logs/latest.log`:
```
[ERROR] [damagetracker] ...
```

### Overlay Not Visible

1. **Press K** to toggle visibility
2. **Check position** in config file - may be off-screen
3. **Reset config**: Delete `damagetracker-client.toml` and restart

### No Damage Recorded

1. Ensure you're dealing damage (not taking damage)
2. Check that Mine and Slash is active
3. Try clearing data (press L) and attacking again
4. Verify you're on the client (not dedicated server console)

### Performance Issues

If experiencing FPS drops:

1. Reduce `maxSourcesDisplayed` to 5
2. Disable `showDPS` and `showPercentage`
3. Use smaller overlay dimensions
4. Increase time window to reduce updates

### Build Errors

**"Invalid JDK version"**
- Install JDK 17 or later
- Set `JAVA_HOME` environment variable

**"Could not resolve dependencies"**
- Check internet connection
- Run `./gradlew clean build --refresh-dependencies`

**"Mine and Slash dependency not found"**
- The build script uses CurseMaven
- Ensure you have internet access during build
- Try clearing Gradle cache: `rm -rf ~/.gradle/caches`

## Mine and Slash Integration

### Damage Categories

The mod automatically detects:
- **Abilities**: Spells and skills from Mine and Slash
- **Elements**: Fire, Ice, Lightning, Nature, etc.
- **Effects**: Damage over time (DoT) effects
- **Attacks**: Melee and ranged attacks

### Enhanced Tracking (Advanced)

For developers wanting deeper integration:

1. The mod hooks into `LivingHurtEvent`
2. Attempts to extract Mine and Slash metadata
3. Falls back to vanilla damage types

To add custom categorization:
- Edit `MineAndSlashIntegration.java`
- Implement `tryExtractMineAndSlashSource()` with Mine and Slash API calls

## Advanced Features

### Exporting Data (Future Feature)

Currently in development:
- CSV export of damage sessions
- Detailed combat logs
- Session comparison

### Custom Themes (Future Feature)

Planned configuration options:
```toml
# Custom color scheme
barColors = ["#FF4444", "#44FF44", "#4444FF"]
backgroundColor = "#000000"
textColor = "#FFFFFF"
```

## Support

### Getting Help

1. **Check this guide** for common solutions
2. **Review logs** in `.minecraft/logs/latest.log`
3. **Check config** file for syntax errors
4. **Verify mod compatibility** with other mods

### Reporting Issues

When reporting bugs, include:
- Minecraft version
- Forge version
- Mine and Slash version
- Damage Tracker version
- Relevant log excerpts
- Steps to reproduce

## Version Compatibility

| Minecraft | Forge | Mine and Slash | Damage Tracker |
|-----------|-------|----------------|----------------|
| 1.20.1    | 47.2+ | 6.3.11+       | 1.0.0+        |

**Note**: This mod is designed specifically for Minecraft 1.20.1. Other versions are not supported.

## Uninstallation

1. Close Minecraft
2. Remove `damagetracker-1.0.0.jar` from `.minecraft/mods/`
3. (Optional) Delete `.minecraft/config/damagetracker-client.toml`
4. Restart Minecraft

Your worlds and other mods are unaffected.

---

**For more information, see README.md**
