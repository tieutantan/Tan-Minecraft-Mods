# Tan-Minecraft-Mods

> **Minecraft 26.1.2 — NeoForge** | Java 25

A collection of client-side/singleplayer Minecraft mods that automate gameplay and customize your experience.

---

## 📋 Mod List

### 1️⃣ Auto Delete Items (`MC-Mod-Auto-Delete-Items`)
**🗑️ Automatically removes unwanted items from your inventory.**
- Choose items to auto-delete by their item ID
- Scans inventory at a configurable interval and removes blacklisted items
- Press **O** to open the configuration screen
- Config caching for performance optimization

### 2️⃣ Auto Eat (`MC-Mod-Auto-Eat`)
**🍔 Automatically eats food when health/hunger is low — no need to hold food.**
- Auto-eats from inventory when health ≤ 85% (eats until full) or hunger < 70%
- Eats 1 item every 80 ticks, prioritizing the first inventory slot
- Sends a random chat message after each meal

### 3️⃣ Auto Transfer Items (`MC-Mod-Auto-Transfer-Items`)
**📦 Transfer all inventory items into a container with a single click.**
- Adds a **▶** button to container GUIs (chests, barrels, furnaces, etc.)
- Smart transfer — auto-stacks items, supports whitelist filtering
- Configurable: transfer hotbar, slot limit

### 4️⃣ Get EXP From Nature (`MC-Mod-Get-Exp-From-Nature`)
**⛏️ Gain EXP when breaking stone and tree blocks in the wild.**
- +1 EXP per stone block (overworld/nether stone, ores)
- +1 EXP per wood/leaf block
- EXP orb spawns in front of the player, works with Mending

### 5️⃣ Ignore Pickup Items (`MC-Mod-Ignore-Pickup-Items`)
**🚫 Skip picking up unwanted items on the ground.**
- Choose items to ignore by their item ID
- Player character automatically avoids picking up listed items
- Press **I** to open the configuration screen

### 6️⃣ Mob Customizer (`MC-Mod-Mob-Customizer`)
**👹 Fully customize spawn rates and stats of hostile mobs.**
- Toggle spawning for each mob type: Zombie, Creeper, Skeleton, Spider, Enderman, Witch, Slime
- Adjust: spawn weight, group size, speed, damage, follow range
- Configure in-game via the Mods menu or the `/mobcustom reload` command

---

> ⚡ All mods run on **NeoForge** for **Minecraft 26.1.2**, requiring **Java 25**.
