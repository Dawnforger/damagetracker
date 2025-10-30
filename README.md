# Damage Tracker for Minecraft 1.20.1

A client-side Minecraft mod that tracks damage dealt by the player and displays it in a real-time overlay chart. Designed to work with **Mine and Slash v6.3.11**, providing functionality similar to World of Warcraft's Details! addon.

## Features

- **Real-time Damage Tracking**: Monitors all damage dealt by the player
- **Categorization by Source**: Organizes damage by ability, effect, or attack type
- **Configurable Time Window**: Adjustable from 10 to 60 seconds (default: 30s)
- **Visual Overlay**: Horizontal bar chart showing damage distribution
- **DPS Calculation**: Shows damage per second for each source
- **Percentage Display**: Shows what percentage of total damage each source represents
- **Customizable**: Configuration file for all settings
- **Keybindings**: 
  - **K** - Toggle overlay visibility
  - **L** - Clear damage data

## Requirements

- Minecraft 1.20.1
- Forge 47.2.0 or later
- Mine and Slash v6.3.11 or later

## Installation

### For Users

1. Download the latest release JAR file
2. Install Minecraft Forge 1.20.1 (version 47.2.0+)
3. Install Mine and Slash v6.3.11
4. Place the Damage Tracker JAR file in your `.minecraft/mods` folder
5. Launch Minecraft with the Forge profile

### For Developers

#### Prerequisites

- JDK 17 or later
- Gradle (included via wrapper)

#### Building from Source

1. Clone this repository:
   ```bash
   git clone <repository-url>
   cd minecraft-damage-tracker
   ```

2. Build the mod:
   ```bash
   ./gradlew build
   ```
   
   On Windows:
   ```cmd
   gradlew.bat build
   ```

3. The compiled JAR will be located at:
   ```
   build/libs/damagetracker-1.0.0.jar
   ```

#### Development Setup

1. Generate IDE files:
   
   For IntelliJ IDEA:
   ```bash
   ./gradlew genIntellijRuns
   ```
   
   For Eclipse:
   ```bash
   ./gradlew eclipse
   ```

2. Import the project into your IDE

3. Run the client:
   ```bash
   ./gradlew runClient
   ```

## Configuration

The configuration file is located at `.minecraft/config/damagetracker-client.toml`

### Available Settings

```toml
[Damage Tracker Configuration]
    # Time window in seconds for damage tracking (10-60)
    timeWindow = 30
    
    # X position of overlay
    overlayX = 10
    
    # Y position of overlay
    overlayY = 10
    
    # Width of overlay
    overlayWidth = 300
    
    # Height of overlay
    overlayHeight = 200
    
    # Show DPS (damage per second)
    showDPS = true
    
    # Show damage percentage
    showPercentage = true
    
    # Maximum number of damage sources to display
    maxSourcesDisplayed = 10
```

## Usage

### In-Game Controls

- **K Key** (default): Toggle the damage overlay on/off
- **L Key** (default): Clear all recorded damage data

You can rebind these keys in Minecraft's Controls settings under the "Damage Tracker" category.

### Reading the Overlay

The overlay displays:
- **Header**: Shows "Damage Tracker" with the current time window
- **Total Damage**: Yellow text showing cumulative damage
- **Damage Bars**: Colored horizontal bars for each damage source
  - Bar length represents proportion of total damage
  - Source name on the left
  - Damage amount, percentage, and DPS on the right

### Color Coding

Different damage sources are shown in different colors:
- Red, Orange, Yellow, Green, Cyan, Blue, Purple, Pink

The colors rotate based on ranking (highest damage = red).

## Integration with Mine and Slash

This mod integrates with Mine and Slash's damage system to accurately track:
- Ability damage
- Effect damage (DoTs, buffs, etc.)
- Melee and ranged attacks
- Elemental damage types

The mod automatically categorizes damage based on Mine and Slash's damage metadata.

## Performance

The overlay is designed to be lightweight:
- Efficient data structure using rolling time windows
- Minimal memory footprint
- No impact on game FPS during normal gameplay
- Client-side only (no server load)

## Troubleshooting

### Overlay Not Showing
- Press K to ensure overlay is enabled
- Check that you're in-game (not in menus)
- Verify the overlay position in config isn't off-screen

### No Damage Recorded
- Ensure Mine and Slash is properly installed
- Check that you're dealing damage in combat
- Try clearing data (L key) and starting fresh

### Build Errors
- Ensure you're using JDK 17
- Run `./gradlew clean` before building
- Check that Mine and Slash dependency is accessible

## Known Limitations

- Currently tracks basic damage categories; full Mine and Slash ability integration requires additional API access
- Overlay position must be manually configured in the config file
- Limited to tracking damage dealt by the player (not damage taken)

## Future Enhancements

Planned features:
- Drag-and-drop overlay positioning
- In-game configuration GUI
- More detailed Mine and Slash ability tracking
- Session statistics and history
- Export damage logs
- Custom color themes

## License

This project is licensed under the MIT License.

## Credits

- Built for Minecraft 1.20.1 using Forge
- Integrates with Mine and Slash by Robert
- Inspired by World of Warcraft's Details! addon

## Support

For bugs, feature requests, or questions, please open an issue on the GitHub repository.

---

**Note**: This mod is client-side only and does not require installation on servers. It only tracks damage dealt by your own character.
