package com.example.mobcustomizer;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ===== ZOMBIE SETTINGS =====
    public static final ModConfigSpec.BooleanValue ALLOW_ZOMBIE_SPAWN;
    public static final ModConfigSpec.IntValue ZOMBIE_WEIGHT_MULTIPLIER;
    public static final ModConfigSpec.IntValue ZOMBIE_MIN_GROUP_SIZE;
    public static final ModConfigSpec.IntValue ZOMBIE_MAX_GROUP_SIZE;
    public static final ModConfigSpec.DoubleValue ZOMBIE_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue ZOMBIE_MOVEMENT_SPEED;
    public static final ModConfigSpec.DoubleValue ZOMBIE_ATTACK_DAMAGE;

    // ===== CREEPER SETTINGS =====
    public static final ModConfigSpec.BooleanValue ALLOW_CREEPER_SPAWN;
    public static final ModConfigSpec.DoubleValue CREEPER_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue CREEPER_MOVEMENT_SPEED;
    public static final ModConfigSpec.IntValue CREEPER_EXPLOSION_RADIUS;

    // ===== SKELETON SETTINGS =====
    public static final ModConfigSpec.BooleanValue ALLOW_SKELETON_SPAWN;
    public static final ModConfigSpec.DoubleValue SKELETON_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue SKELETON_MOVEMENT_SPEED;
    public static final ModConfigSpec.DoubleValue SKELETON_ATTACK_DAMAGE;

    // ===== SPIDER SETTINGS =====
    public static final ModConfigSpec.BooleanValue ALLOW_SPIDER_SPAWN;
    public static final ModConfigSpec.DoubleValue SPIDER_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue SPIDER_MOVEMENT_SPEED;
    public static final ModConfigSpec.DoubleValue SPIDER_ATTACK_DAMAGE;

    // ===== ENDERMAN SETTINGS =====
    public static final ModConfigSpec.BooleanValue ALLOW_ENDERMAN_SPAWN;
    public static final ModConfigSpec.DoubleValue ENDERMAN_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue ENDERMAN_MOVEMENT_SPEED;
    public static final ModConfigSpec.DoubleValue ENDERMAN_ATTACK_DAMAGE;

    // ===== WITCH SETTINGS =====
    public static final ModConfigSpec.BooleanValue ALLOW_WITCH_SPAWN;
    public static final ModConfigSpec.DoubleValue WITCH_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue WITCH_MOVEMENT_SPEED;

    // ===== SLIME SETTINGS =====
    public static final ModConfigSpec.BooleanValue ALLOW_SLIME_SPAWN;
    public static final ModConfigSpec.DoubleValue SLIME_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue SLIME_MOVEMENT_SPEED;
    public static final ModConfigSpec.DoubleValue SLIME_ATTACK_DAMAGE;

    static final ModConfigSpec SPEC;

    static {
        // ===== ZOMBIE SECTION =====
        BUILDER.comment("Zombie spawn and behavior settings")
               .push("zombie");
        
        ALLOW_ZOMBIE_SPAWN = BUILDER
                .comment("Allow zombies to spawn")
                .translation("mobcustomizer.config.allowZombieSpawn")
                .define("allowSpawn", true);

        ZOMBIE_WEIGHT_MULTIPLIER = BUILDER
                .comment("Spawn weight multiplier (vanilla: ~1x, higher = more spawns)")
                .translation("mobcustomizer.config.zombieWeightMultiplier")
                .defineInRange("weightMultiplier", 1, 1, 50);

        ZOMBIE_MIN_GROUP_SIZE = BUILDER
                .comment("Minimum zombies per group (vanilla: 4)")
                .translation("mobcustomizer.config.zombieMinGroupSize")
                .defineInRange("minGroupSize", 4, 1, 20);

        ZOMBIE_MAX_GROUP_SIZE = BUILDER
                .comment("Maximum zombies per group (vanilla: 4)")
                .translation("mobcustomizer.config.zombieMaxGroupSize")
                .defineInRange("maxGroupSize", 4, 1, 50);

        ZOMBIE_FOLLOW_RANGE = BUILDER
                .comment("Detection range in blocks (vanilla: 35)")
                .translation("mobcustomizer.config.zombieFollowRange")
                .defineInRange("followRange", 35.0, 16.0, 128.0);

        ZOMBIE_MOVEMENT_SPEED = BUILDER
                .comment("Movement speed (vanilla: 0.23)")
                .translation("mobcustomizer.config.zombieMovementSpeed")
                .defineInRange("movementSpeed", 0.23, 0.1, 1.0);

        ZOMBIE_ATTACK_DAMAGE = BUILDER
                .comment("Attack damage (vanilla: 3.0)")
                .translation("mobcustomizer.config.zombieAttackDamage")
                .defineInRange("attackDamage", 3.0, 1.0, 20.0);

        BUILDER.pop();

        // ===== CREEPER SECTION =====
        BUILDER.comment("Creeper spawn and behavior settings")
               .push("creeper");

        ALLOW_CREEPER_SPAWN = BUILDER
                .comment("Allow creepers to spawn")
                .translation("mobcustomizer.config.allowCreeperSpawn")
                .define("allowSpawn", true);

        CREEPER_FOLLOW_RANGE = BUILDER
                .comment("Detection range (vanilla: 16)")
                .translation("mobcustomizer.config.creeperFollowRange")
                .defineInRange("followRange", 16.0, 8.0, 64.0);

        CREEPER_MOVEMENT_SPEED = BUILDER
                .comment("Movement speed (vanilla: 0.25)")
                .translation("mobcustomizer.config.creeperMovementSpeed")
                .defineInRange("movementSpeed", 0.25, 0.1, 0.8);

        CREEPER_EXPLOSION_RADIUS = BUILDER
                .comment("Explosion radius (vanilla: 3)")
                .translation("mobcustomizer.config.creeperExplosionRadius")
                .defineInRange("explosionRadius", 3, 1, 10);

        BUILDER.pop();

        // ===== SKELETON SECTION =====
        BUILDER.comment("Skeleton spawn and behavior settings")
               .push("skeleton");

        ALLOW_SKELETON_SPAWN = BUILDER
                .comment("Allow skeletons to spawn")
                .translation("mobcustomizer.config.allowSkeletonSpawn")
                .define("allowSpawn", true);

        SKELETON_FOLLOW_RANGE = BUILDER
                .comment("Detection range (vanilla: 15)")
                .translation("mobcustomizer.config.skeletonFollowRange")
                .defineInRange("followRange", 15.0, 8.0, 64.0);

        SKELETON_MOVEMENT_SPEED = BUILDER
                .comment("Movement speed (vanilla: 0.25)")
                .translation("mobcustomizer.config.skeletonMovementSpeed")
                .defineInRange("movementSpeed", 0.25, 0.1, 0.8);

        SKELETON_ATTACK_DAMAGE = BUILDER
                .comment("Arrow damage (vanilla: 2.0)")
                .translation("mobcustomizer.config.skeletonAttackDamage")
                .defineInRange("attackDamage", 2.0, 1.0, 15.0);

        BUILDER.pop();

        // ===== SPIDER SECTION =====
        BUILDER.comment("Spider spawn and behavior settings")
               .push("spider");

        ALLOW_SPIDER_SPAWN = BUILDER
                .comment("Allow spiders to spawn")
                .translation("mobcustomizer.config.allowSpiderSpawn")
                .define("allowSpawn", true);

        SPIDER_FOLLOW_RANGE = BUILDER
                .comment("Detection range (vanilla: 16)")
                .translation("mobcustomizer.config.spiderFollowRange")
                .defineInRange("followRange", 16.0, 8.0, 64.0);

        SPIDER_MOVEMENT_SPEED = BUILDER
                .comment("Movement speed (vanilla: 0.3)")
                .translation("mobcustomizer.config.spiderMovementSpeed")
                .defineInRange("movementSpeed", 0.3, 0.1, 0.8);

        SPIDER_ATTACK_DAMAGE = BUILDER
                .comment("Attack damage (vanilla: 2.0)")
                .translation("mobcustomizer.config.spiderAttackDamage")
                .defineInRange("attackDamage", 2.0, 1.0, 15.0);

        BUILDER.pop();

        // ===== ENDERMAN SECTION =====
        BUILDER.comment("Enderman spawn and behavior settings")
               .push("enderman");

        ALLOW_ENDERMAN_SPAWN = BUILDER
                .comment("Allow endermen to spawn")
                .translation("mobcustomizer.config.allowEndermanSpawn")
                .define("allowSpawn", true);

        ENDERMAN_FOLLOW_RANGE = BUILDER
                .comment("Detection range (vanilla: 64)")
                .translation("mobcustomizer.config.endermanFollowRange")
                .defineInRange("followRange", 64.0, 16.0, 128.0);

        ENDERMAN_MOVEMENT_SPEED = BUILDER
                .comment("Movement speed (vanilla: 0.3)")
                .translation("mobcustomizer.config.endermanMovementSpeed")
                .defineInRange("movementSpeed", 0.3, 0.1, 0.8);

        ENDERMAN_ATTACK_DAMAGE = BUILDER
                .comment("Attack damage (vanilla: 7.0)")
                .translation("mobcustomizer.config.endermanAttackDamage")
                .defineInRange("attackDamage", 7.0, 1.0, 30.0);

        BUILDER.pop();

        // ===== WITCH SECTION =====
        BUILDER.comment("Witch spawn and behavior settings")
               .push("witch");

        ALLOW_WITCH_SPAWN = BUILDER
                .comment("Allow witches to spawn")
                .translation("mobcustomizer.config.allowWitchSpawn")
                .define("allowSpawn", true);

        WITCH_FOLLOW_RANGE = BUILDER
                .comment("Detection range (vanilla: 16)")
                .translation("mobcustomizer.config.witchFollowRange")
                .defineInRange("followRange", 16.0, 8.0, 64.0);

        WITCH_MOVEMENT_SPEED = BUILDER
                .comment("Movement speed (vanilla: 0.25)")
                .translation("mobcustomizer.config.witchMovementSpeed")
                .defineInRange("movementSpeed", 0.25, 0.1, 0.8);

        BUILDER.pop();

        // ===== SLIME SECTION =====
        BUILDER.comment("Slime spawn and behavior settings")
               .push("slime");

        ALLOW_SLIME_SPAWN = BUILDER
                .comment("Allow slimes to spawn")
                .translation("mobcustomizer.config.allowSlimeSpawn")
                .define("allowSpawn", true);

        SLIME_FOLLOW_RANGE = BUILDER
                .comment("Detection range (vanilla: 16)")
                .translation("mobcustomizer.config.slimeFollowRange")
                .defineInRange("followRange", 16.0, 8.0, 64.0);

        SLIME_MOVEMENT_SPEED = BUILDER
                .comment("Movement speed (vanilla: varies by size)")
                .translation("mobcustomizer.config.slimeMovementSpeed")
                .defineInRange("movementSpeed", 0.3, 0.1, 1.0);

        SLIME_ATTACK_DAMAGE = BUILDER
                .comment("Attack damage (vanilla: 2-6 by size)")
                .translation("mobcustomizer.config.slimeAttackDamage")
                .defineInRange("attackDamage", 2.0, 1.0, 15.0);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private Config() {
        // Prevent instantiation
    }
}
