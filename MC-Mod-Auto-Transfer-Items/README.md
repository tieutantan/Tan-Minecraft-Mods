# Auto Transfer Items

<div align="center">

![Minecraft Version](https://img.shields.io/badge/Minecraft-26.1.2-brightgreen)
![Mod Loader](https://img.shields.io/badge/Mod%20Loader-NeoForge-orange)
![License](https://img.shields.io/badge/License-MIT-blue)

**A lightweight NeoForge mod that adds a quick-transfer button to container screens.**

[Features](#-features) • [Installation](#-installation) • [Usage](#-usage) • [Configuration](#%EF%B8%8F-configuration) • [Building](#-building-from-source)

</div>

---

## 📖 Description

**Auto Transfer Items** is a quality-of-life mod for Minecraft 26.1.2 (NeoForge) that simplifies inventory management. With a single click, transfer all items from your inventory into any container—chest, barrel, furnace, hopper, and more!

Perfect for players who want to:
- Quickly unload inventory after mining trips
- Organize items faster
- Reduce repetitive clicking

---

## ✨ Features

- **One-Click Transfer**: Press the `▶` button in any container GUI to instantly move items
- **Smart Transfer**: Automatically stacks items with existing ones in the container
- **Configurable**:
  - Choose to transfer hotbar items or keep them safe
  - Whitelist specific items (e.g., only transfer ores)
  - Set maximum container slots to use
- **Performance Optimized**: < 1ms transfer time, no lag
- **Works with All Containers**: Chest, barrel, furnace, hopper, shulker box, and modded containers
- **Client + Server Compatible**: Works in singleplayer and multiplayer

---

## 📦 Installation

### Requirements
- **Minecraft**: 26.1.2
- **Mod Loader**: NeoForge 26.1.2+ ([Download NeoForge](https://neoforged.net/))

### Steps
1. Download the latest `.jar` file from [Releases](../../releases)
2. Place the `.jar` file in your `mods` folder:
   - **Windows**: `%appdata%\.minecraft\mods`
   - **macOS**: `~/Library/Application Support/minecraft/mods`
   - **Linux**: `~/.minecraft/mods`
3. Launch Minecraft with the NeoForge profile
4. Done! The mod will auto-generate a config file on first run

---

## 🎮 Usage

### Basic Usage
1. Open any container (chest, barrel, etc.)
2. Look for the **`▶` button** in the top-right corner of the container GUI
3. Click the button → All items from your inventory (slots 9-35) will transfer to the container
4. Items stack automatically with existing items

---

## ⚙️ Configuration

Config file location: `config/autotransferitems-common.toml`

### Default Settings
```toml
# Transfer hotbar (0-8) or only main inventory (9-35)
transferHotbar = false

# Whitelist items (empty = all). Format: 'minecraft:iron_ingot'
whitelistItems = []

# Max container slots to transfer
maxContainerSlots = 54

Example 1: Transfer Everything (Including Hotbar)

```bash
transferHotbar = true
whitelistItems = []
maxContainerSlots = 54
```

Example 2: Only Transfer Ores

```bash
transferHotbar = false
whitelistItems = [
    "minecraft:iron_ore",
    "minecraft:gold_ore",
    "minecraft:diamond_ore",
    "minecraft:emerald_ore"
]
maxContainerSlots = 54
```

Example 3: Small Containers Only (Hoppers)

```bash
transferHotbar = false
whitelistItems = []
maxContainerSlots = 5
```

# Build the mod
`./gradlew build`

# JAR output location (in project root)
`tantn_autotransferitems-1.0.0.jar`

# Test in development environment
`./gradlew runClient`
