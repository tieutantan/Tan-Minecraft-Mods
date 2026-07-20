package com.example.tantantools.mobcustomizer;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Consumer;

/**
 * Central registry mapping mob entity classes → config values and attribute appliers.
 * Eliminates if-else chains in event handlers (DRY, SOLID).
 */
public final class MobConfigs {
    private MobConfigs() {}

    /**
     * A registered mob type with its entity class, allow-spawn config, spawn-rate config, and attribute applier.
     */
    public record MobDef(
            Class<?> entityClass,
            ModConfigSpec.BooleanValue allowSpawn,
            ModConfigSpec.IntValue spawnRatePercent,
            Consumer<LivingEntity> applier
    ) {
        public boolean matches(Entity entity) {
            return entityClass.isInstance(entity);
        }
    }

    /** All registered mobs in evaluation order. */
    private static final MobDef[] ALL = {
        new MobDef(Zombie.class,   MobCustomizerConfig.ALLOW_ZOMBIE_SPAWN,   MobCustomizerConfig.ZOMBIE_SPAWN_RATE_PERCENT,   MobConfigs::applyZombie),
        new MobDef(Creeper.class,  MobCustomizerConfig.ALLOW_CREEPER_SPAWN,  MobCustomizerConfig.CREEPER_SPAWN_RATE_PERCENT,  MobConfigs::applyCreeper),
        new MobDef(Skeleton.class, MobCustomizerConfig.ALLOW_SKELETON_SPAWN, MobCustomizerConfig.SKELETON_SPAWN_RATE_PERCENT, MobConfigs::applySkeleton),
        new MobDef(Spider.class,   MobCustomizerConfig.ALLOW_SPIDER_SPAWN,   MobCustomizerConfig.SPIDER_SPAWN_RATE_PERCENT,   MobConfigs::applySpider),
        new MobDef(EnderMan.class, MobCustomizerConfig.ALLOW_ENDERMAN_SPAWN, MobCustomizerConfig.ENDERMAN_SPAWN_RATE_PERCENT, MobConfigs::applyEnderman),
        new MobDef(Witch.class,    MobCustomizerConfig.ALLOW_WITCH_SPAWN,    MobCustomizerConfig.WITCH_SPAWN_RATE_PERCENT,    MobConfigs::applyWitch),
        new MobDef(Slime.class,    MobCustomizerConfig.ALLOW_SLIME_SPAWN,    MobCustomizerConfig.SLIME_SPAWN_RATE_PERCENT,    MobConfigs::applySlime),
    };

    /** Returns the total number of registered mob types. */
    public static int count() {
        return ALL.length;
    }

    /** Returns the mob definition at the given index. */
    public static MobDef get(int index) {
        return ALL[index];
    }

    /**
     * Finds the first MobDef matching the given entity via instanceof check.
     * Returns {@code null} if the entity is not a tracked mob type.
     */
    public static MobDef find(Entity entity) {
        for (MobDef mob : ALL) {
            if (mob.matches(entity)) return mob;
        }
        return null;
    }

    // ===== Attribute appliers =====

    private static void applyZombie(LivingEntity e) {
        setAttr(e, Attributes.FOLLOW_RANGE, MobCustomizerConfig.ZOMBIE_FOLLOW_RANGE.get());
        setAttr(e, Attributes.MOVEMENT_SPEED, MobCustomizerConfig.ZOMBIE_MOVEMENT_SPEED.get());
        setAttr(e, Attributes.ATTACK_DAMAGE, MobCustomizerConfig.ZOMBIE_ATTACK_DAMAGE.get());
    }

    private static void applyCreeper(LivingEntity e) {
        setAttr(e, Attributes.FOLLOW_RANGE, MobCustomizerConfig.CREEPER_FOLLOW_RANGE.get());
        setAttr(e, Attributes.MOVEMENT_SPEED, MobCustomizerConfig.CREEPER_MOVEMENT_SPEED.get());
    }

    private static void applySkeleton(LivingEntity e) {
        setAttr(e, Attributes.FOLLOW_RANGE, MobCustomizerConfig.SKELETON_FOLLOW_RANGE.get());
        setAttr(e, Attributes.MOVEMENT_SPEED, MobCustomizerConfig.SKELETON_MOVEMENT_SPEED.get());
        setAttr(e, Attributes.ATTACK_DAMAGE, MobCustomizerConfig.SKELETON_ATTACK_DAMAGE.get());
    }

    private static void applySpider(LivingEntity e) {
        setAttr(e, Attributes.FOLLOW_RANGE, MobCustomizerConfig.SPIDER_FOLLOW_RANGE.get());
        setAttr(e, Attributes.MOVEMENT_SPEED, MobCustomizerConfig.SPIDER_MOVEMENT_SPEED.get());
        setAttr(e, Attributes.ATTACK_DAMAGE, MobCustomizerConfig.SPIDER_ATTACK_DAMAGE.get());
    }

    private static void applyEnderman(LivingEntity e) {
        setAttr(e, Attributes.FOLLOW_RANGE, MobCustomizerConfig.ENDERMAN_FOLLOW_RANGE.get());
        setAttr(e, Attributes.MOVEMENT_SPEED, MobCustomizerConfig.ENDERMAN_MOVEMENT_SPEED.get());
        setAttr(e, Attributes.ATTACK_DAMAGE, MobCustomizerConfig.ENDERMAN_ATTACK_DAMAGE.get());
    }

    private static void applyWitch(LivingEntity e) {
        setAttr(e, Attributes.FOLLOW_RANGE, MobCustomizerConfig.WITCH_FOLLOW_RANGE.get());
        setAttr(e, Attributes.MOVEMENT_SPEED, MobCustomizerConfig.WITCH_MOVEMENT_SPEED.get());
    }

    private static void applySlime(LivingEntity e) {
        setAttr(e, Attributes.FOLLOW_RANGE, MobCustomizerConfig.SLIME_FOLLOW_RANGE.get());
        setAttr(e, Attributes.MOVEMENT_SPEED, MobCustomizerConfig.SLIME_MOVEMENT_SPEED.get());
        setAttr(e, Attributes.ATTACK_DAMAGE, MobCustomizerConfig.SLIME_ATTACK_DAMAGE.get());
    }

    private static void setAttr(LivingEntity e, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, double value) {
        var instance = e.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }
}
