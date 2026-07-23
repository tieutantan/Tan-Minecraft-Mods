# Tan-Minecraft-Mods

> **Minecraft 26.1.2 — NeoForge** | Java 25

A collection of client-side/singleplayer Minecraft mods that automate gameplay and customize your experience.

---

## 📋 Mod List

### 1️⃣ Tan Tan Tools (`MC-Mod-Tan-Tan-Tools`)
**🧰 An all-in-one toolbox that combines practical automation and customization features.**
- Auto Delete Items — remove selected items from the inventory at a configurable interval
- Auto Eat — automatically eat when hunger or health falls below configured thresholds
- Auto Transfer Items — transfer inventory items into containers with a single GUI button
- Exp From Nature — gain configurable EXP from breaking stone, ores, wood, and leaves
- Combine Enchanted Items — combine compatible enchanted items according to the feature settings
- Cycle Trades Better — cycle villager trades more conveniently
- Mob Customizer — enable or disable hostile mob spawning and adjust spawn rates, groups, and attributes
- Press **O** to open the shared Tan Tan Tools settings screen
- The mod uses separate configuration files for each feature and supports `/tantantools mobcustom reload`

### 2️⃣ Tan Gun (`MC-Mod-Tan-Gun`)
**🔫 An automatic piercing gun for fast long-range combat.**
- Hold left click to fire at **20 rounds per second** without the vanilla item-use slowdown
- Each shot deals **5 damage** to every living entity in the firing line
- Shots travel up to 100 blocks and stop at solid blocks
- Each shot consumes one **Iron Nugget**; Creative mode has unlimited ammunition
- The action bar displays the remaining ammunition, and empty ammunition triggers a dry-fire sound
- Supports weapon enchantments, including Looting and Fire Aspect
- Includes a short tracer effect and light visual recoil while firing

### 3️⃣ Auto Delete Items (`MC-Mod-Auto-Delete-Items`)
**🗑️ Automatically removes unwanted items from your inventory.**
- Choose items to auto-delete by their item ID
- Scans inventory at a configurable interval and removes blacklisted items
- Press **O** to open the configuration screen
- Config caching for performance optimization

### 4️⃣ Auto Eat (`MC-Mod-Auto-Eat`)
**🍔 Automatically eats food when health/hunger is low — no need to hold food.**
- Auto-eats from inventory when health ≤ 85% (eats until full) or hunger < 70%
- Eats 1 item every 80 ticks, prioritizing the first inventory slot
- Sends a random chat message after each meal

### 5️⃣ Auto Transfer Items (`MC-Mod-Auto-Transfer-Items`)
**📦 Transfer all inventory items into a container with a single click.**
- Adds a **▶** button to container GUIs (chests, barrels, furnaces, etc.)
- Smart transfer — auto-stacks items, supports whitelist filtering
- Configurable: transfer hotbar, slot limit

### 6️⃣ Get EXP From Nature (`MC-Mod-Get-Exp-From-Nature`)
**⛏️ Gain EXP when breaking stone and tree blocks in the wild.**
- +1 EXP per stone block (overworld/nether stone, ores)
- +1 EXP per wood/leaf block
- EXP orb spawns in front of the player, works with Mending

### 7️⃣ Ignore Pickup Items (`MC-Mod-Ignore-Pickup-Items`)
**🚫 Skip picking up unwanted items on the ground.**
- Choose items to ignore by their item ID
- Player character automatically avoids picking up listed items
- Press **I** to open the configuration screen

### 8️⃣ Mob Customizer (`MC-Mod-Mob-Customizer`)
**👹 Fully customize spawn rates and stats of hostile mobs.**
- Toggle spawning for each mob type: Zombie, Creeper, Skeleton, Spider, Enderman, Witch, Slime
- Adjust: spawn weight, group size, speed, damage, follow range
- Configure in-game via the Mods menu or the `/mobcustom reload` command

---

> ⚡ All mods run on **NeoForge** for **Minecraft 26.1.2**, requiring **Java 25**.
