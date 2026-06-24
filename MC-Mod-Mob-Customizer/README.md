# Mob Customizer

A Minecraft mod for NeoForge 26.1.2 that gives you full control over hostile mob spawning and behavior.

## Features

- **Spawn Control**: Enable/disable individual mob types (Zombie, Creeper, Skeleton, Spider, Enderman, Witch, Slime)
- **Spawn Rate**: Adjust spawn weight multiplier (1-50x)
- **Group Size**: Configure min/max mobs per spawn group
- **Movement Speed**: Fine-tune how fast mobs move
- **Attack Damage**: Customize damage dealt by mobs
- **Detection Range**: Control how far mobs can detect players/villagers
- **In-Game Config**: Easy-to-use GUI configuration screen
- **Live Reload**: `/mobcustom reload` command to apply changes without restart

## Requirements

- **Minecraft**: 26.1.2
- **NeoForge**: 26.1.2.68-beta+
- **Java**: 25

## Installation

1. Download the latest `.jar` from root directory
2. Place in your `mods` folder
3. Launch Minecraft with NeoForge 26.1.2

## Configuration

Config file: `config/mobcustomizer.toml`

Access in-game via:
- **Mods menu** → Mob Customizer → Config button
- Or edit the TOML file directly

Each mob has individual settings:
- Allow Spawn (on/off)
- Spawn Weight Multiplier
- Group Size (min/max)
- Follow Range
- Movement Speed
- Attack Damage

## Commands

- `/mobcustom status` - View current configuration
- `/mobcustom reload` - Reload config from file (requires OP level 2)

## Development

### Build Commands

```bash
# Refresh dependencies
./gradlew --refresh-dependencies

# Clean build artifacts
./gradlew clean

# Run client (test in dev environment)
./gradlew runClient

# Build mod JAR
./gradlew build
