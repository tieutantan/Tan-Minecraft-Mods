# Tan-Minecraft-Mods — AI Agent Guide

## Project Overview

This workspace contains **6 independent NeoForge mods** for **Minecraft 26.1.2** targeting **Java 25**, all authored by **Tran Ngoc Tan** (info@tantn.com). Each mod is standalone — no inter-mod dependencies.

All 6 mods share:
- NeoForge for Minecraft 26.1.2
- Java 25 toolchain
- `All Rights Reserved` license
- `modLoader="javafml"` in `neoforge.mods.toml`
- **No Mixins** and **no Access Transformers**
- UTF-8 encoding, `org.gradle.parallel=true`
- Sided client separation via `@EventBusSubscriber(value = Dist.CLIENT, modid = ...)` or separate `@Mod(value = ..., dist = Dist.CLIENT)` classes
- Event-driven architecture via NeoForge event bus

---

## Mods at a Glance

| Directory | Mod ID | Group | Package | Plugin Type | Config System | Key Feature |
|-----------|--------|-------|---------|-------------|---------------|-------------|
| `MC-Mod-Auto-Delete-Items` | `tantn_autodeleteitems` | `com.example.tantn_autodeleteitems` | `com.example.tantn_autodeleteitems` | `userdev 7.1.36` | JSON (Gson) | Key **O** → auto-delete item list |
| `MC-Mod-Auto-Eat` | `autoeat` | `com.tantn.autoeat` | `com.tantn.autoeat` | `moddev 2.0.141` | ModConfigSpec | Auto-eat when hunger/máu thấp |
| `MC-Mod-Auto-Transfer-Items` | `autotransferitems` | `com.example.examplemod` | `com.example.examplemod` | `userdev 7.1.36` | ModConfigSpec | **▶** button on container GUI |
| `MC-Mod-Get-Exp-From-Nature` | `tantn_getexpfromnature` | `com.example.tantn_getexpfromnature` | `com.example.tantn_getexpfromnature` | `userdev 7.1.36` | ModConfigSpec | +1 EXP per stone/tree block |
| `MC-Mod-Ignore-Pickup-Items` | `tantn_ignorepickupitems` | `com.example.tantn_ignorepickupitems` | `com.example.ignorepickup` | `userdev 7.1.36` | JSON (Gson) | Key **I** → ignore item list |
| `MC-Mod-Mob-Customizer` | `mobcustomizer` | `com.example.mobcustomizer` | `com.example.examplemod` | `moddev 2.0.141` | ModConfigSpec | `/mobcustom reload` command |

---

## Build System

### Two Gradle Plugin Lineages

**`net.neoforged.gradle.userdev` v7.1.36** (4 mods):
- `neoforge.mods.toml` placed directly in `src/main/resources/META-INF/`
- Variable substitution via `tasks.withType(ProcessResources)` + `filesMatching`

**`net.neoforged.moddev` v2.0.141** (2 mods: Auto-Eat, Mob-Customizer):
- `neoforge.mods.toml` placed in `src/main/templates/META-INF/`
- Variable substitution via `generateModMetadata` task

### Common Build Commands (run from any mod directory)
```bash
./gradlew --refresh-dependencies   # Refresh all dependencies
./gradlew clean                    # Clean build artifacts
./gradlew runClient                # Launch Minecraft client with mod
./gradlew build                    # Build mod jar
```

### Auto-Version Increment

Most mods auto-increment `mod_version` in `gradle.properties` after each build. Three patterns exist:

1. **`doLast` in `tasks.named('build')`** (Auto-Eat, Ignore-Pickup-Items, Mob-Customizer, Get-Exp-From-Nature)
2. **Config-time Groovy** (Auto-Transfer-Items)
3. **None** (Auto-Delete-Items)

Example (Auto-Eat approach):
```groovy
tasks.named('build').configure {
    doLast {
        def propsFile = layout.projectDirectory.file('gradle.properties').asFile
        def content = propsFile.getText('UTF-8')
        def matcher = (content =~ /(?m)^mod_version=(.+)$/)
        if (matcher.find()) {
            def parts = matcher.group(1).trim().split('\\.')
            parts[-1] = (parts[-1].toInteger() + 1).toString()
            content = content.replaceFirst(/(?m)^mod_version=.+$/, "mod_version=${parts.join('.')}")
            propsFile.setText(content, 'UTF-8')
        }
    }
}
```

### Shared `gradle.properties`
```properties
minecraft_version=26.1.2
neo_version=26.1.2.68-beta
java.toolchain.languageVersion=JavaLanguageVersion.of(25)
org.gradle.jvmargs=-Xmx1G
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true
mod_license=All Rights Reserved
```

---

## Architecture & Coding Conventions

### 1. Main Mod Class Patterns

There are **three patterns** for the `@Mod`-annotated main class:

**Pattern A — Simple constructor (no `IEventBus`)**
Used by: Auto-Delete-Items, Ignore-Pickup-Items
```java
@Mod(AutoDeleteItems.MODID)
public final class AutoDeleteItems {
    public static final String MODID = "tantn_autodeleteitems";
    public AutoDeleteItems() {
        Config.load();
        NeoForge.EVENT_BUS.register(new AutoDeleteEvents());
    }
}
```

**Pattern B — `IEventBus` parameter**
Used by: Auto-Eat, Get-Exp-From-Nature
```java
@Mod(AutoEat.MODID)
public class AutoEat {
    public AutoEat(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        // DeferredRegister registration
        BLOCKS.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
    }
}
```

**Pattern C — `IEventBus` + `ModContainer` parameters**
Used by: Auto-Transfer-Items, Mob-Customizer
```java
@Mod(AutoTransferItems.MOD_ID)
public class AutoTransferItems {
    public AutoTransferItems(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(AutoTransferPacket::register);
    }
}
```

### 2. Event Registration

- **Mod event bus** (`modEventBus`): Used for lifecycle events (`FMLCommonSetupEvent`, `RegisterPayloadHandlersEvent`)
- **NeoForge global bus** (`NeoForge.EVENT_BUS`): Used for game events (`BreakBlockEvent`, `EntityItemPickupEvent`, `ScreenEvent.Init.Post`, `InputEvent.Key`)
- **`@EventBusSubscriber`**: Used for client-side classes (`value = Dist.CLIENT, modid = MODID`)

### 3. Configuration Systems

**JSON-based config** (Auto-Delete-Items, Ignore-Pickup-Items):
- Custom file: `config/<modname>.json`
- Uses `GsonBuilder().setPrettyPrinting()`, atomic writes via `Files.move` with `ATOMIC_MOVE` fallback
- Thread-safe via `synchronized (Config.class)`
- `ConcurrentSkipListSet` for in-memory sets
- Batch I/O coalescing: `startBatch()` / `endBatch()` pattern

**ModConfigSpec** (Auto-Eat, Auto-Transfer-Items, Get-Exp-From-Nature, Mob-Customizer):
- Standard NeoForge `ModConfigSpec.Builder`
- Registered via `container.registerConfig(ModConfig.Type.COMMON, SPEC)`
- Mob-Customizer uses a custom filename: `container.registerConfig(ModConfig.Type.COMMON, SPEC, "mobcustomizer.toml")`

### 4. Keybinding Registration

Used by: Auto-Delete-Items (key **O**), Ignore-Pickup-Items (key **I**)

```java
private static final KeyMapping OPEN_CONFIG = new KeyMapping(
    "key.autodeleteitems.config", GLFW.GLFW_KEY_O, KeyMapping.CATEGORY_MISC);

@SubscribeEvent
public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
    event.register(OPEN_CONFIG);
}

@SubscribeEvent
public static void onKeyInput(InputEvent.Key event) {
    if (event.getAction() == GLFW.GLFW_PRESS && OPEN_CONFIG.isDown()) {
        Minecraft.getInstance().setScreen(new ConfigScreen());
    }
}
```

### 5. Network Packets

Used only by: Auto-Transfer-Items (play-to-server)

```java
// Record-based packet with StreamCodec
public record AutoTransferPacket() implements CustomPacketPayload {
    public static final Type<AutoTransferPacket> TYPE = new Type<>(
        Identifier.fromNamespaceAndPath(MOD_ID, "transfer"));
    public static final StreamCodec<FriendlyByteBuf, AutoTransferPacket> STREAM_CODEC =
        StreamCodec.unit(new AutoTransferPacket());

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(MOD_ID)
            .playToServer(TYPE, STREAM_CODEC, AutoTransferPacket::handle);
    }

    private void handle(AutoTransferPacket packet, IPayloadContext ctx) { ... }
}
```

### 6. GUI / Screen Patterns

Three distinct patterns exist:

1. **Custom full-screen with item grid search**: Auto-Delete-Items, Ignore-Pickup-Items — `Screen` subclass with `EditBox`, paginated grid from `BuiltInRegistries.ITEM`, toggle buttons `[X] / [ ]`

2. **Container GUI button overlay**: Auto-Transfer-Items — adds `▶` button via `ScreenEvent.Init.Post` on `AbstractContainerScreen`

3. **Built-in NeoForge ConfigurationScreen**: Get-Exp-From-Nature, Mob-Customizer — registered via `container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new)`

### 7. Client-Side Separation

Three mods (Auto-Eat, Get-Exp-From-Nature, Mob-Customizer) use a separate `@Mod(value = MODID, dist = Dist.CLIENT)` client class. Others use `@EventBusSubscriber(value = Dist.CLIENT, modid = MODID)` on a standalone client events class.

### 8. Logging

Two logger patterns coexist:
- `org.apache.logging.log4j.LogManager.getLogger()` — used by Auto-Delete-Items, Ignore-Pickup-Items
- `com.mojang.logging.LogUtils.getLogger()` — used by Auto-Eat, Auto-Transfer-Items, Get-Exp-From-Nature, Mob-Customizer

---

## Resource & Data Files

| Mod | Assets |
|-----|--------|
| Auto-Delete-Items | `assets/tantn_autodeleteitems/lang/en_us.json` |
| Auto-Eat | `assets/autoeat/lang/en_us.json`, `autoeat_messages.txt` |
| Auto-Transfer-Items | `assets/autotransferitems/lang/en_us.json` |
| Get-Exp-From-Nature | `assets/getexpfrommining_tantn/lang/en_us.json` |
| Ignore-Pickup-Items | `assets/ignorepickup/lang/en_us.json` |
| Mob-Customizer | `assets/mobcustomizer/lang/en_us.json` + `lang/vi_vn.json` (bilingual!) |

Mob-Customizer is the **only mod with non-English localization** (Vietnamese).

Mob-Customizer also has a biome modifier data file: `src/main/resources/data/zombiespawnrate/neoforge/biome_modifier/increase_zombie_spawns.json`.

---

## Known Inconsistencies (Be Aware)

1. **Package naming is inconsistent** across mods — some use `com.example.<modid>`, others use `com.tantn.<modid>`, and Auto-Transfer-Items still uses the default `com.example.examplemod`
2. **Gradle `config-cache`**: `false` for Auto-Delete-Items and Ignore-Pickup-Items; `true` for all others
3. **Jar output destination**: Some output directly to project root, others to `build/libs/` then copy to root
4. **`mod_authors` field**: Present in most `gradle.properties` but absent in Auto-Eat and Mob-Customizer
5. **JSON config code duplication**: `Config.java` in Auto-Delete-Items and Ignore-Pickup-Items is nearly identical (copy-pasted)
6. **No shared base classes** between mods — each is entirely self-contained

---

## Workflow Notes for AI Agents

- Each mod lives in its own directory with its own `build.gradle`, `gradle.properties`, and `settings.gradle`
- Always run `./gradlew` commands from within a specific mod directory, not from the root
- After editing code, run `./gradlew build` to verify compilation
- Config changes in `gradle.properties` require a Gradle sync (reload project)
- The root `README.md` and this `AGENTS.md` are the entry points for understanding the project
