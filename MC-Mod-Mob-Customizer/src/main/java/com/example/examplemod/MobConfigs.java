package com.example.mobcustomizer;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
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
     * A registered mob type with its entity class, allow-spawn config, and attribute applier.
     */
    public record MobDef(
            Class<?> entityClass,
            ModConfigSpec.BooleanValue allowSpawn,
            Consumer<LivingEntity> applier
    ) {
        public boolean matches(Entity entity) {
            return entityClass.isInstance(entity);
        }
    }

    /** All registered mobs in evaluation order. */
    private static final MobDef[] ALL = {
        new MobDef(Zombie.class,   Config.ALLOW_ZOMBIE_SPAWN,   MobConfigs::applyZombie),
        new MobDef(Creeper.class,  Config.ALLOW_CREEPER_SPAWN,  MobConfigs::applyCreeper),
        new MobDef(Skeleton.class, Config.ALLOW_SKELETON_SPAWN, MobConfigs::applySkeleton),
        new MobDef(Spider.class,   Config.ALLOW_SPIDER_SPAWN,   MobConfigs::applySpider),
        new MobDef(EnderMan.class, Config.ALLOW_ENDERMAN_SPAWN, MobConfigs::applyEnderman),
        new MobDef(Witch.class,    Config.ALLOW_WITCH_SPAWN,    MobConfigs::applyWitch),
        new MobDef(Slime.class,    Config.ALLOW_SLIME_SPAWN,    MobConfigs::applySlime),
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
        setAttr(e, Attributes.FOLLOW_RANGE, Config.ZOMBIE_FOLLOW_RANGE.get());
        setAttr(e, Attributes.MOVEMENT_SPEED, Config.ZOMBIE_MOVEMENT_SPEED.get());
        setAttr(e, Attributes.ATTACK_DAMAGE, Config.ZOMBIE_ATTACK_DAMAGE.get());
    }

    private static void applyCreeper(LivingEntity e) {
        setAttr(e, Attributes.FOLLOW_RANGE, Config.CREEPER_FOLLOW_RANGE.get());
        setAttr(e, Attributes.MOVEMENT_SPEED, Config.CREEPER_MOVEMENT_SPEED.get());
    }

    private static void applySkeleton(LivingEntity e) {
        setAttr(e, Attributes.FOLLOW_RANGE, Config.SKELETON_FOLLOW_RANGE.get());
        setAttr(e, Attributes.MOVEMENT_SPEED, Config.SKELETON_MOVEMENT_SPEED.get());
        setAttr(e, Attributes.ATTACK_DAMAGE, Config.SKELETON_ATTACK_DAMAGE.get());
    }

    private static void applySpider(LivingEntity e) {
        setAttr(e, Attributes.FOLLOW_RANGE, Config.SPIDER_FOLLOW_RANGE.get());
        setAttr(e, Attributes.MOVEMENT_SPEED, Config.SPIDER_MOVEMENT_SPEED.get());
        setAttr(e, Attributes.ATTACK_DAMAGE, Config.SPIDER_ATTACK_DAMAGE.get());
    }

    private static void applyEnderman(LivingEntity e) {
        setAttr(e, Attributes.FOLLOW_RANGE, Config.ENDERMAN_FOLLOW_RANGE.get());
        setAttr(e, Attributes.MOVEMENT_SPEED, Config.ENDERMAN_MOVEMENT_SPEED.get());
        setAttr(e, Attributes.ATTACK_DAMAGE, Config.ENDERMAN_ATTACK_DAMAGE.get());
    }

    private static void applyWitch(LivingEntity e) {
        setAttr(e, Attributes.FOLLOW_RANGE, Config.WITCH_FOLLOW_RANGE.get());
        setAttr(e, Attributes.MOVEMENT_SPEED, Config.WITCH_MOVEMENT_SPEED.get());
    }

    private static void applySlime(LivingEntity e) {
        setAttr(e, Attributes.FOLLOW_RANGE, Config.SLIME_FOLLOW_RANGE.get());
        setAttr(e, Attributes.MOVEMENT_SPEED, Config.SLIME_MOVEMENT_SPEED.get());
        setAttr(e, Attributes.ATTACK_DAMAGE, Config.SLIME_ATTACK_DAMAGE.get());
    }

    /** Helper to safely set an entity attribute base value. */
    static void setAttr(LivingEntity entity, Holder<Attribute> attribute, double value) {
        var attr = entity.getAttribute(attribute);
        if (attr != null) {
            attr.setBaseValue(value);
        }
    }
}
