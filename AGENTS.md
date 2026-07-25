# Tan-Minecraft-Mods - AI Agent Guide

## Project Overview

This workspace contains two independent NeoForge mods for Minecraft 26.1.2,
targeting Java 25. Each mod is standalone; there are no inter-mod dependencies.

The current mods are:

- `MC-Mod-Tan-Tan-Tools`: an all-in-one toolbox with auto delete, auto eat, auto
  transfer, experience from nature, enchanted-item combining, and villager trade
  cycling features.
- `MC-Mod-Tan-Gun`: an automatic piercing gun that consumes Iron Nuggets and
  supports weapon enchantments.

Both modules share:

- NeoForge for Minecraft 26.1.2 (`neo_version=26.1.2.68-beta`)
- Java 25 toolchains
- `All Rights Reserved` licensing
- `net.neoforged.moddev` version 2.0.141
- UTF-8 Java compilation
- NeoForge event-driven architecture
- Separate client, server, game-test-server, and data-generation run configs

## Build System

Each mod has its own `build.gradle`, `gradle.properties`, `settings.gradle`, and
Gradle wrapper. Run commands from the module directory, not from the workspace
root.

```bash
cd MC-Mod-Tan-Tan-Tools   # or MC-Mod-Tan-Gun
./gradlew runClient       # Launch the client
./gradlew build           # Compile and package the mod
```

`COMMANDS.md` contains the short command list used by the project. The Gradle
`build` task in both modules copies the generated JAR to that module's project
root and increments the patch component of `mod_version` in
`gradle.properties` after a successful build. Account for that file change when
checking the working tree after a build.

Useful additional Gradle tasks include `clean`, `runServer`, `runData`, and
`runGameTestServer`.

## Architecture and Coding Conventions

### Module Boundaries

Keep changes inside the owning module. Shared behavior is not currently exposed
as a workspace-level library, and the two mods should remain independently
buildable and distributable.

### NeoForge Integration

- Use the mod event bus for lifecycle and registration events.
- Use `NeoForge.EVENT_BUS` for gameplay events.
- Keep client-only behavior in client-dist classes or client event subscribers
  so server loading does not reference client-only Minecraft classes.
- Register each module's source set through its `mod_id` in the `neoForge.mods`
  block.
- Put mod metadata templates under `src/main/templates` and let the existing
  `generateModMetadata` task expand Gradle properties into generated resources.

### Configuration and Features

Tan Tan Tools keeps feature implementations and configuration separate by
feature. Preserve the existing per-feature config and command behavior, including
`/tantantools mobcustom reload` where applicable. The shared settings screen is
opened with `O`; follow the existing keybinding and screen registration patterns
when adding client settings.

Tan Gun's firing behavior is client-triggered but must preserve the existing
server-safe gameplay and resource checks: Iron Nuggets are consumed in survival,
Creative mode has unlimited ammunition, shots stop at solid blocks, and each
living entity in the firing line can be hit.

### Resources

Keep assets under `src/main/resources/assets/<mod_id>` and data under
`src/main/resources/data/<namespace>`. Localization files currently include the
feature text for Tan Tan Tools and Tan Gun; update them when adding user-visible
names or messages.

## Workflow Notes for AI Agents

- Inspect the owning module's `README.md`, `build.gradle`, and
  `gradle.properties` before making changes.
- Run `./gradlew build` from the changed module after code or resource edits.
- Use `./gradlew runClient` for interactive client verification when behavior
  depends on Minecraft screens, input, rendering, or gameplay.
- Use `./gradlew runData` when changing data-generation inputs or generated
  resources.
- Use the module's `runGameTestServer` task when adding or changing GameTests.
- Configuration changes in `gradle.properties` require a Gradle sync or reload.
- The root `README.md` is the project feature overview; keep it aligned with
  current modules and user-facing behavior.