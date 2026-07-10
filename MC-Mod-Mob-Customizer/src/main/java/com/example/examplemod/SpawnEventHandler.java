package com.example.mobcustomizer;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber(modid = MobCustomizer.MODID)
public class SpawnEventHandler {

    // Bitmask cache: bit i = 1 if MobConfigs.get(i).allowSpawn() == true.
    // Using volatile int ensures thread-safe visibility between config (client thread)
    // and spawn (server thread) without expensive synchronization.
    // Initialized with safe defaults (all allowed = no blocking) because
    // ModConfigSpec values cannot be read before config is loaded.
    // First real refresh happens in onWorldLoad() → refreshCache().
    private static volatile int allowMask = ~0; // all bits set = all allowed
    private static volatile boolean allMobsAllowed = true;

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        refreshCache();
        logStatus();
    }

    /** Reload cached booleans from Config. Call after any config change. */
    public static void refreshCache() {
        int mask = 0;
        boolean all = true;
        for (int i = 0; i < MobConfigs.count(); i++) {
            if (MobConfigs.get(i).allowSpawn().get()) {
                mask |= (1 << i);
            } else {
                all = false;
            }
        }
        // Volatile writes — guaranteed visibility across threads
        allowMask = mask;
        allMobsAllowed = all;
    }

    private static void logStatus() {
        if (allMobsAllowed) {
            MobCustomizer.LOGGER.info("=== Mob Spawn Control: All mobs allowed ===");
            return;
        }
        var sb = new StringBuilder("=== Mob Spawn Control: Blocked: ");
        boolean first = true;
        int mask = allowMask; // volatile read once
        for (int i = 0; i < MobConfigs.count(); i++) {
            if ((mask & (1 << i)) == 0) {
                if (!first) sb.append(", ");
                sb.append(MobConfigs.get(i).entityClass().getSimpleName());
                first = false;
            }
        }
        MobCustomizer.LOGGER.info(sb.toString());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onMobSpawn(FinalizeSpawnEvent event) {
        // Ultra-fast path: all mobs allowed → skip all checks
        if (allMobsAllowed) return;

        Entity entity = event.getEntity();
        int mask = allowMask; // volatile read once

        // Iterate registry — single loop replaces 7 if-else branches
        for (int i = 0; i < MobConfigs.count(); i++) {
            if (MobConfigs.get(i).matches(entity)) {
                if ((mask & (1 << i)) == 0) {
                    cancelSpawn(event);
                }
                break;
            }
        }
    }

    private static void cancelSpawn(FinalizeSpawnEvent event) {
        event.setCanceled(true);
        event.setSpawnCancelled(true);
    }
}
