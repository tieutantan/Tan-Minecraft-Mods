package com.example.mobcustomizer;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

@EventBusSubscriber(modid = MobCustomizer.MODID)
public class MobSpawnCustomizer {
    
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onMobSpawn(FinalizeSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        
        // Apply config-based attributes when mob spawns
        if (entity instanceof Zombie && Config.ALLOW_ZOMBIE_SPAWN.get()) {
            applyZombieConfig(entity);
        } else if (entity instanceof Creeper && Config.ALLOW_CREEPER_SPAWN.get()) {
            applyCreeperConfig(entity);
        } else if (entity instanceof Skeleton && Config.ALLOW_SKELETON_SPAWN.get()) {
            applySkeletonConfig(entity);
        } else if (entity instanceof Spider && Config.ALLOW_SPIDER_SPAWN.get()) {
            applySpiderConfig(entity);
        } else if (entity instanceof EnderMan && Config.ALLOW_ENDERMAN_SPAWN.get()) {
            applyEndermanConfig(entity);
        } else if (entity instanceof Witch && Config.ALLOW_WITCH_SPAWN.get()) {
            applyWitchConfig(entity);
        } else if (entity instanceof Slime && Config.ALLOW_SLIME_SPAWN.get()) {
            applySlimeConfig(entity);
        }
    }
    
    private static void applyZombieConfig(LivingEntity entity) {
        setAttribute(entity, Attributes.FOLLOW_RANGE, Config.ZOMBIE_FOLLOW_RANGE.get());
        setAttribute(entity, Attributes.MOVEMENT_SPEED, Config.ZOMBIE_MOVEMENT_SPEED.get());
        setAttribute(entity, Attributes.ATTACK_DAMAGE, Config.ZOMBIE_ATTACK_DAMAGE.get());
    }
    
    private static void applyCreeperConfig(LivingEntity entity) {
        setAttribute(entity, Attributes.FOLLOW_RANGE, Config.CREEPER_FOLLOW_RANGE.get());
        setAttribute(entity, Attributes.MOVEMENT_SPEED, Config.CREEPER_MOVEMENT_SPEED.get());
    }
    
    private static void applySkeletonConfig(LivingEntity entity) {
        setAttribute(entity, Attributes.FOLLOW_RANGE, Config.SKELETON_FOLLOW_RANGE.get());
        setAttribute(entity, Attributes.MOVEMENT_SPEED, Config.SKELETON_MOVEMENT_SPEED.get());
        setAttribute(entity, Attributes.ATTACK_DAMAGE, Config.SKELETON_ATTACK_DAMAGE.get());
    }
    
    private static void applySpiderConfig(LivingEntity entity) {
        setAttribute(entity, Attributes.FOLLOW_RANGE, Config.SPIDER_FOLLOW_RANGE.get());
        setAttribute(entity, Attributes.MOVEMENT_SPEED, Config.SPIDER_MOVEMENT_SPEED.get());
        setAttribute(entity, Attributes.ATTACK_DAMAGE, Config.SPIDER_ATTACK_DAMAGE.get());
    }
    
    private static void applyEndermanConfig(LivingEntity entity) {
        setAttribute(entity, Attributes.FOLLOW_RANGE, Config.ENDERMAN_FOLLOW_RANGE.get());
        setAttribute(entity, Attributes.MOVEMENT_SPEED, Config.ENDERMAN_MOVEMENT_SPEED.get());
        setAttribute(entity, Attributes.ATTACK_DAMAGE, Config.ENDERMAN_ATTACK_DAMAGE.get());
    }
    
    private static void applyWitchConfig(LivingEntity entity) {
        setAttribute(entity, Attributes.FOLLOW_RANGE, Config.WITCH_FOLLOW_RANGE.get());
        setAttribute(entity, Attributes.MOVEMENT_SPEED, Config.WITCH_MOVEMENT_SPEED.get());
    }
    
    private static void applySlimeConfig(LivingEntity entity) {
        setAttribute(entity, Attributes.FOLLOW_RANGE, Config.SLIME_FOLLOW_RANGE.get());
        setAttribute(entity, Attributes.MOVEMENT_SPEED, Config.SLIME_MOVEMENT_SPEED.get());
        setAttribute(entity, Attributes.ATTACK_DAMAGE, Config.SLIME_ATTACK_DAMAGE.get());
    }
    
    // Fixed helper method - use Holder<Attribute> for 1.21.1
    private static void setAttribute(LivingEntity entity, Holder<Attribute> attribute, double value) {
        var attr = entity.getAttribute(attribute);
        if (attr != null) {
            attr.setBaseValue(value);
        }
    }
}
