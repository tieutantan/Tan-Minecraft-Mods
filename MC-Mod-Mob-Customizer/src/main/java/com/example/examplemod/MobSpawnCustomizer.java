package com.example.mobcustomizer;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

@EventBusSubscriber(modid = MobCustomizer.MODID)
public class MobSpawnCustomizer {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onMobSpawn(FinalizeSpawnEvent event) {
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
