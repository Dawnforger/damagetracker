# Quick Start Guide

Get up and running with Damage Tracker in 5 minutes!

## 1. Installation (2 minutes)

### What You Need
- ✅ Minecraft 1.20.1
- ✅ Forge 47.2.0+ ([Download](https://files.minecraftforge.net/))
- ✅ Mine and Slash v6.3.11+ ([CurseForge](https://www.curseforge.com/minecraft/mc-mods/mine-and-slash))
- ✅ Damage Tracker mod JAR

### Steps
1. Install Forge for Minecraft 1.20.1
2. Launch Minecraft once to create mod folders
3. Place Mine and Slash JAR in `.minecraft/mods/`
4. Place Damage Tracker JAR in `.minecraft/mods/`
5. Launch Minecraft with Forge profile

## 2. First Use (1 minute)

### See It in Action
1. Join any world
2. Look at top-left corner - you'll see the overlay
3. Attack a mob
4. Watch damage appear in real-time!

```
┌─────────────────────────────┐
│ Damage Tracker (30s)        │
│ Total: 1250                  │
│ ███████ Fireball: 450 (36%) │
│ █████ Magic: 350 (28%)      │
└─────────────────────────────┘
```

## 3. Basic Controls (1 minute)

| Key | What It Does |
|-----|--------------|
| **K** | Hide/show the overlay |
| **L** | Clear all damage data |

**Change keys:** Options → Controls → Damage Tracker

## 4. Customize (1 minute)

### Edit Config File
Location: `.minecraft/config/damagetracker-client.toml`

**Quick Changes:**
```toml
timeWindow = 30          # Track 30 seconds of damage
overlayX = 10            # Position from left
overlayY = 10            # Position from top
overlayWidth = 300       # Width in pixels
showDPS = true           # Show damage per second
```

**Save and restart Minecraft** to apply changes.

## 5. Use It! (∞)

### Best Practices

**Between Fights:**
- Press **L** to clear data for accurate per-fight stats

**For Boss Fights:**
- Set `timeWindow = 60` for longer tracking
- Increase `maxSourcesDisplayed = 15`

**For PvP:**
- Set `timeWindow = 10` for burst damage tracking
- Enable both DPS and percentage displays

### Reading the Display

**Each bar shows:**
- **Length** = Proportion of total damage
- **Color** = Rank (red = highest)
- **Left side** = Damage source name
- **Right side** = Damage amount + percentage + DPS

**Total** = All damage in the time window

## Troubleshooting

### Overlay not showing?
→ Press **K** to toggle it on

### No damage recorded?
→ Make sure you're attacking (not being attacked)
→ Press **L** to clear and try again

### Overlay in wrong position?
→ Edit `overlayX` and `overlayY` in config file

## Next Steps

- Read [INSTALLATION.md](INSTALLATION.md) for detailed configuration
- Read [README.md](README.md) for full feature list
- Join our community for tips and tricks

## Pro Tips

💡 **Use shorter time windows (10-20s)** to see burst damage patterns

💡 **Clear data between fights** for accurate encounter statistics

💡 **Position overlay next to your hotbar** for easy glancing

💡 **Disable DPS display** if overlay is too cluttered

---

**Need help?** Check the full documentation in INSTALLATION.md

**Found a bug?** Report it on our GitHub issues page

**Enjoy tracking your damage! 🎯**
