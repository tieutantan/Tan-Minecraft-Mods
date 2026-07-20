package com.example.tantantools.mobcustomizer;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

public final class MobSpawnCustomizer {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onMobSpawn(FinalizeSpawnEvent event) {
        // Skip if spawn was already cancelled by SpawnEventHandler (HIGH priority)
        if (event.isSpawnCancelled()) return;

        if (!(event.getEntity() instanceof LivingEntity living)) return;

        // Look up mob in registry and apply attributes if spawn is allowed
        MobConfigs.MobDef mob = MobConfigs.find(living);
        if (mob != null && mob.allowSpawn().get()) {
            mob.applier().accept(living);
        }
    }
}
