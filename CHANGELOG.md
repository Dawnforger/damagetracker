# Changelog

All notable changes to the Damage Tracker mod will be documented in this file.

## [1.0.0] - 2025-10-30

### Added
- Initial release
- Real-time damage tracking for player-dealt damage
- Integration with Mine and Slash v6.3.11
- Configurable time window (10-60 seconds)
- Visual overlay with horizontal bar chart display
- Damage categorization by source (abilities, effects, attacks)
- DPS (damage per second) calculation
- Percentage-based damage distribution display
- Keybinding for toggling overlay visibility (default: K)
- Keybinding for clearing damage data (default: L)
- Client-side configuration file
- Customizable overlay position and dimensions
- Color-coded damage sources (up to 8 unique colors)
- Support for up to 50 different damage sources
- Automatic data cleanup (rolling time window)
- Performance-optimized rendering

### Features
- **Damage Tracking**: Records all damage dealt by the local player
- **Source Detection**: Automatically identifies damage sources from Mine and Slash
- **Visual Display**: Clean, readable overlay similar to WoW Details! addon
- **Configuration**: Extensive config options for customization
- **Keybindings**: Configurable hotkeys for overlay control
- **Performance**: Minimal impact on game FPS

### Known Limitations
- Basic Mine and Slash integration (requires deeper API access for full ability names)
- Overlay position requires manual configuration (no drag-and-drop yet)
- Client-side only (does not track damage on dedicated servers without client)
- Limited to damage dealt (does not track damage taken)

### Technical Details
- Built for Minecraft 1.20.1
- Requires Forge 47.2.0+
- Requires Mine and Slash 6.3.11+
- Java 17 compatible
- Client-side only mod

### Future Roadmap
- [ ] Drag-and-drop overlay positioning
- [ ] In-game configuration GUI
- [ ] Enhanced Mine and Slash ability tracking via API
- [ ] Session statistics and history
- [ ] Damage taken tracking
- [ ] Export functionality (CSV, JSON)
- [ ] Custom color themes
- [ ] Multiple time window presets
- [ ] Combat log viewer
- [ ] Graphical charts (line graphs, pie charts)
- [ ] Comparison mode (compare multiple sessions)

---

## Version Format

Versions follow Semantic Versioning (SemVer): MAJOR.MINOR.PATCH

- **MAJOR**: Incompatible API changes or major feature overhauls
- **MINOR**: New features, backward compatible
- **PATCH**: Bug fixes, backward compatible
