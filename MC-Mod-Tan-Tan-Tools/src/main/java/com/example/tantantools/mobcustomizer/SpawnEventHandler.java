package com.example.tantantools.mobcustomizer;

import com.example.tantantools.TanTanTools;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.concurrent.ThreadLocalRandom;

public final class SpawnEventHandler {

    // Max extra copies spawned alongside the original when the effective rate > 100.
    // Keep burst spawning bounded even when the configured percentages are high.
    private static final int MAX_EXTRA_SPAWNS = 2;

    // Cached per-mob effective spawn rate (1-1000). 100 = vanilla amount (subject to allowSpawn),
    // <100 = a percentage chance the natural spawn is allowed, >100 = the natural spawn
    // is always allowed plus a chance of spawning extra copies alongside it.
    // Rebuilt on config change via refreshCache(). Array reference is swapped atomically
    // (volatile) so the spawn-check thread always sees a fully-populated snapshot
    // without needing per-element synchronization.
    private static volatile int[] spawnRatePercent = defaultAllAllowedRates();
    private static volatile boolean allMobsAlwaysAllowed = true;

    private static int[] defaultAllAllowedRates() {
        int[] rates = new int[MobConfigs.count()];
        java.util.Arrays.fill(rates, 100);
        return rates;
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event) {
        refreshCache();
        logStatus();
    }

    /** Reload cached spawn rates from Config. Call after any config change. */
    public static void refreshCache() {
        int count = MobConfigs.count();
        int[] rates = new int[count];
        boolean allAlwaysAllowed = true;
        for (int i = 0; i < count; i++) {
            MobConfigs.MobDef mob = MobConfigs.get(i);
            int rate = mob.allowSpawn().get() ? effectiveRate(mob) : 0;
            rates[i] = rate;
            if (rate != 100) {
                allAlwaysAllowed = false;
            }
        }
        // Volatile writes — guaranteed visibility across threads
        spawnRatePercent = rates;
        allMobsAlwaysAllowed = allAlwaysAllowed;
    }

    private static void logStatus() {
        int[] rates = spawnRatePercent; // volatile read once
        var sb = new StringBuilder("=== Mob Spawn Control: ");
        boolean first = true;
        for (int i = 0; i < MobConfigs.count(); i++) {
            if (!first) sb.append(", ");
            sb.append(MobConfigs.get(i).entityClass().getSimpleName()).append('=').append(rates[i]).append('%');
            first = false;
        }
        TanTanTools.LOGGER.info(sb.toString());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMobSpawn(FinalizeSpawnEvent event) {
        if (event.getSpawnType() != EntitySpawnReason.NATURAL) return;

        // Ultra-fast path: every tracked mob spawns unconditionally → skip all checks
        if (allMobsAlwaysAllowed) return;

        Entity entity = event.getEntity();
        int[] rates = spawnRatePercent; // volatile read once

        // Iterate registry — single loop replaces 7 if-else branches
        for (int i = 0; i < MobConfigs.count(); i++) {
            MobConfigs.MobDef mob = MobConfigs.get(i);
            if (mob.matches(entity)) {
                int rate = rates[i];
                if (rate <= 0) {
                    cancelSpawn(event);
                } else if (rate < 100) {
                    if (ThreadLocalRandom.current().nextInt(100) >= rate) {
                        cancelSpawn(event);
                    }
                } else if (rate > 100 && entity instanceof Mob mobEntity) {
                    spawnExtraCopies(event, mob, mobEntity, rate);
                }
                break;
            }
        }
    }

    private static int effectiveRate(MobConfigs.MobDef mob) {
        long combined = (long) mob.spawnRatePercent().get() * mob.spawnSpeedPercent().get();
        return (int) Math.max(1, Math.min(1000, Math.round(combined / 100.0)));
    }

    /**
    * For the effective rate > 100, rolls a chance per extra copy (100% = +1 guaranteed,
     * 200% = +1 guaranteed and a roll for +1 more, capped at MAX_EXTRA_SPAWNS).
     * Extra copies are created directly (bypassing finalizeSpawn/this event) to avoid
     * re-triggering FinalizeSpawnEvent recursively, and have their attributes applied
     * the same way MobSpawnCustomizer does for the original spawn.
     */
    private static void spawnExtraCopies(FinalizeSpawnEvent event, MobConfigs.MobDef mob, Mob original, int rate) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        int extraChancePercent = rate - 100;
        for (int extra = 0; extra < MAX_EXTRA_SPAWNS; extra++) {
            int remainingChance = extraChancePercent - extra * 100;
            if (remainingChance <= 0) break;
            if (remainingChance < 100 && ThreadLocalRandom.current().nextInt(100) >= remainingChance) break;

            Entity created = original.getType().create(serverLevel, EntitySpawnReason.NATURAL);
            if (!(created instanceof Mob copy)) continue;

            double offsetX = ThreadLocalRandom.current().nextDouble(-1.5, 1.5);
            double offsetZ = ThreadLocalRandom.current().nextDouble(-1.5, 1.5);
            copy.snapTo(original.getX() + offsetX, original.getY(), original.getZ() + offsetZ, original.getYRot(), 0.0F);
            copy.yHeadRot = copy.getYRot();
            copy.yBodyRot = copy.getYRot();

            mob.applier().accept(copy);
            serverLevel.addFreshEntity(copy);
        }
    }

    private static void cancelSpawn(FinalizeSpawnEvent event) {
        event.setCanceled(true);
        event.setSpawnCancelled(true);
    }
}
