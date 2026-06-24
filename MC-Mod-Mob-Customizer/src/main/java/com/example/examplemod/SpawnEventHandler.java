package com.example.mobcustomizer;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber(modid = MobCustomizer.MODID)
public class SpawnEventHandler {
    
    // Cached config values for ultra-fast lookups
    private static volatile boolean allowZombie = true;
    private static volatile boolean allowCreeper = true;
    private static volatile boolean allowSkeleton = true;
    private static volatile boolean allowSpider = true;
    private static volatile boolean allowEnderman = true;
    private static volatile boolean allowWitch = true;
    private static volatile boolean allowSlime = true;
    private static volatile boolean allMobsAllowed = true;
    
    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        refreshCache();
        logStatus();
    }
    
    public static void refreshCache() {
        allowZombie = Config.ALLOW_ZOMBIE_SPAWN.get();
        allowCreeper = Config.ALLOW_CREEPER_SPAWN.get();
        allowSkeleton = Config.ALLOW_SKELETON_SPAWN.get();
        allowSpider = Config.ALLOW_SPIDER_SPAWN.get();
        allowEnderman = Config.ALLOW_ENDERMAN_SPAWN.get();
        allowWitch = Config.ALLOW_WITCH_SPAWN.get();
        allowSlime = Config.ALLOW_SLIME_SPAWN.get();
        
        // Cache whether all mobs are allowed for ultra-fast early return
        allMobsAllowed = allowZombie && allowCreeper && allowSkeleton && allowSpider && 
                         allowEnderman && allowWitch && allowSlime;
    }
    
    private static void logStatus() {
        MobCustomizer.LOGGER.info("=== Mob Spawn Control ===");
        if (!allMobsAllowed) {
            MobCustomizer.LOGGER.info("Blocked mobs: {}{}{}{}{}{}{}",
                !allowZombie ? "Zombie " : "",
                !allowCreeper ? "Creeper " : "",
                !allowSkeleton ? "Skeleton " : "",
                !allowSpider ? "Spider " : "",
                !allowEnderman ? "Enderman " : "",
                !allowWitch ? "Witch " : "",
                !allowSlime ? "Slime " : "");
        } else {
            MobCustomizer.LOGGER.info("All mobs allowed");
        }
        MobCustomizer.LOGGER.info("========================");
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onMobSpawn(FinalizeSpawnEvent event) {
        // Ultra-fast path: if all mobs allowed, skip all checks
        if (allMobsAllowed) {
            return;
        }
        
        Entity entity = event.getEntity();
        
        // Optimized type checking with early returns
        if (entity instanceof Zombie) {
            if (!allowZombie) cancelSpawn(event);
        } else if (entity instanceof Creeper) {
            if (!allowCreeper) cancelSpawn(event);
        } else if (entity instanceof Skeleton) {
            if (!allowSkeleton) cancelSpawn(event);
        } else if (entity instanceof Spider) {
            if (!allowSpider) cancelSpawn(event);
        } else if (entity instanceof EnderMan) {
            if (!allowEnderman) cancelSpawn(event);
        } else if (entity instanceof Witch) {
            if (!allowWitch) cancelSpawn(event);
        } else if (entity instanceof Slime) {
            if (!allowSlime) cancelSpawn(event);
        }
    }
    
    private static void cancelSpawn(FinalizeSpawnEvent event) {
        event.setCanceled(true);
        event.setSpawnCancelled(true);
    }
}
