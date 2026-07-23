package com.example.tantantools.mobcustomizer;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class MobCustomizerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ===== ZOMBIE SETTINGS =====
    public static final ModConfigSpec.BooleanValue ALLOW_ZOMBIE_SPAWN;
    public static final ModConfigSpec.IntValue ZOMBIE_SPAWN_RATE_PERCENT;
    public static final ModConfigSpec.DoubleValue ZOMBIE_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue ZOMBIE_MOVEMENT_SPEED;
    public static final ModConfigSpec.DoubleValue ZOMBIE_ATTACK_DAMAGE;

    // ===== CREEPER SETTINGS =====
    public static final ModConfigSpec.BooleanValue ALLOW_CREEPER_SPAWN;
    public static final ModConfigSpec.IntValue CREEPER_SPAWN_RATE_PERCENT;
    public static final ModConfigSpec.DoubleValue CREEPER_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue CREEPER_MOVEMENT_SPEED;
    public static final ModConfigSpec.IntValue CREEPER_EXPLOSION_RADIUS;

    // ===== SKELETON SETTINGS =====
    public static final ModConfigSpec.BooleanValue ALLOW_SKELETON_SPAWN;
    public static final ModConfigSpec.IntValue SKELETON_SPAWN_RATE_PERCENT;
    public static final ModConfigSpec.DoubleValue SKELETON_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue SKELETON_MOVEMENT_SPEED;
    public static final ModConfigSpec.DoubleValue SKELETON_ATTACK_DAMAGE;

    // ===== SPIDER SETTINGS =====
    public static final ModConfigSpec.BooleanValue ALLOW_SPIDER_SPAWN;
    public static final ModConfigSpec.IntValue SPIDER_SPAWN_RATE_PERCENT;
    public static final ModConfigSpec.DoubleValue SPIDER_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue SPIDER_MOVEMENT_SPEED;
    public static final ModConfigSpec.DoubleValue SPIDER_ATTACK_DAMAGE;

    // ===== ENDERMAN SETTINGS =====
    public static final ModConfigSpec.BooleanValue ALLOW_ENDERMAN_SPAWN;
    public static final ModConfigSpec.IntValue ENDERMAN_SPAWN_RATE_PERCENT;
    public static final ModConfigSpec.DoubleValue ENDERMAN_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue ENDERMAN_MOVEMENT_SPEED;
    public static final ModConfigSpec.DoubleValue ENDERMAN_ATTACK_DAMAGE;

    // ===== WITCH SETTINGS =====
    public static final ModConfigSpec.BooleanValue ALLOW_WITCH_SPAWN;
    public static final ModConfigSpec.IntValue WITCH_SPAWN_RATE_PERCENT;
    public static final ModConfigSpec.DoubleValue WITCH_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue WITCH_MOVEMENT_SPEED;

    // ===== SLIME SETTINGS =====
    public static final ModConfigSpec.BooleanValue ALLOW_SLIME_SPAWN;
    public static final ModConfigSpec.IntValue SLIME_SPAWN_RATE_PERCENT;
    public static final ModConfigSpec.DoubleValue SLIME_FOLLOW_RANGE;
    public static final ModConfigSpec.DoubleValue SLIME_MOVEMENT_SPEED;
    public static final ModConfigSpec.DoubleValue SLIME_ATTACK_DAMAGE;

    public static final ModConfigSpec SPEC;

    static {
        // ===== ZOMBIE SECTION =====
        BUILDER.comment("Zombie spawn and behavior settings").push("zombie");
        ALLOW_ZOMBIE_SPAWN = BUILDER
                .comment("Allow zombies to spawn")
                .translation("tantantools.config.mobcustomizer.allowZombieSpawn")
                .define("allowSpawn", true);
        ZOMBIE_SPAWN_RATE_PERCENT = BUILDER
                .comment("Spawn amount, as a percentage of vanilla (1-300, default 100). Below 100 = fewer mobs, above 100 = extra mobs spawn alongside each natural spawn")
                .translation("tantantools.config.mobcustomizer.zombieSpawnRatePercent")
                .defineInRange("spawnRatePercent", 100, 1, 300);
        ZOMBIE_FOLLOW_RANGE = BUILDER
                .comment("Detection range in blocks (vanilla: 35)")
                .translation("tantantools.config.mobcustomizer.zombieFollowRange")
                .defineInRange("followRange", 35.0, 16.0, 128.0);
        ZOMBIE_MOVEMENT_SPEED = BUILDER
                .comment("Movement speed (vanilla: 0.23)")
                .translation("tantantools.config.mobcustomizer.zombieMovementSpeed")
                .defineInRange("movementSpeed", 0.23, 0.1, 1.0);
        ZOMBIE_ATTACK_DAMAGE = BUILDER
                .comment("Attack damage (vanilla: 3.0)")
                .translation("tantantools.config.mobcustomizer.zombieAttackDamage")
                .defineInRange("attackDamage", 3.0, 1.0, 20.0);
        BUILDER.pop();

        // ===== CREEPER SECTION =====
        BUILDER.comment("Creeper spawn and behavior settings").push("creeper");
        ALLOW_CREEPER_SPAWN = BUILDER
                .comment("Allow creepers to spawn")
                .translation("tantantools.config.mobcustomizer.allowCreeperSpawn")
                .define("allowSpawn", false);
        CREEPER_SPAWN_RATE_PERCENT = BUILDER
                .comment("Spawn amount, as a percentage of vanilla (1-300, default 100). Below 100 = fewer mobs, above 100 = extra mobs spawn alongside each natural spawn")
                .translation("tantantools.config.mobcustomizer.creeperSpawnRatePercent")
                .defineInRange("spawnRatePercent", 100, 1, 300);
        CREEPER_FOLLOW_RANGE = BUILDER
                .comment("Detection range (vanilla: 16)")
                .translation("tantantools.config.mobcustomizer.creeperFollowRange")
                .defineInRange("followRange", 16.0, 8.0, 64.0);
        CREEPER_MOVEMENT_SPEED = BUILDER
                .comment("Movement speed (vanilla: 0.25)")
                .translation("tantantools.config.mobcustomizer.creeperMovementSpeed")
                .defineInRange("movementSpeed", 0.25, 0.1, 0.8);
        CREEPER_EXPLOSION_RADIUS = BUILDER
                .comment("Explosion radius (vanilla: 3)")
                .translation("tantantools.config.mobcustomizer.creeperExplosionRadius")
                .defineInRange("explosionRadius", 3, 1, 10);
        BUILDER.pop();

        // ===== SKELETON SECTION =====
        BUILDER.comment("Skeleton spawn and behavior settings").push("skeleton");
        ALLOW_SKELETON_SPAWN = BUILDER
                .comment("Allow skeletons to spawn")
                .translation("tantantools.config.mobcustomizer.allowSkeletonSpawn")
                .define("allowSpawn", false);
        SKELETON_SPAWN_RATE_PERCENT = BUILDER
                .comment("Spawn amount, as a percentage of vanilla (1-300, default 100). Below 100 = fewer mobs, above 100 = extra mobs spawn alongside each natural spawn")
                .translation("tantantools.config.mobcustomizer.skeletonSpawnRatePercent")
                .defineInRange("spawnRatePercent", 100, 1, 300);
        SKELETON_FOLLOW_RANGE = BUILDER
                .comment("Detection range (vanilla: 15)")
                .translation("tantantools.config.mobcustomizer.skeletonFollowRange")
                .defineInRange("followRange", 15.0, 8.0, 64.0);
        SKELETON_MOVEMENT_SPEED = BUILDER
                .comment("Movement speed (vanilla: 0.25)")
                .translation("tantantools.config.mobcustomizer.skeletonMovementSpeed")
                .defineInRange("movementSpeed", 0.25, 0.1, 0.8);
        SKELETON_ATTACK_DAMAGE = BUILDER
                .comment("Arrow damage (vanilla: 2.0)")
                .translation("tantantools.config.mobcustomizer.skeletonAttackDamage")
                .defineInRange("attackDamage", 2.0, 1.0, 15.0);
        BUILDER.pop();

        // ===== SPIDER SECTION =====
        BUILDER.comment("Spider spawn and behavior settings").push("spider");
        ALLOW_SPIDER_SPAWN = BUILDER
                .comment("Allow spiders to spawn")
                .translation("tantantools.config.mobcustomizer.allowSpiderSpawn")
                .define("allowSpawn", false);
        SPIDER_SPAWN_RATE_PERCENT = BUILDER
                .comment("Spawn amount, as a percentage of vanilla (1-300, default 100). Below 100 = fewer mobs, above 100 = extra mobs spawn alongside each natural spawn")
                .translation("tantantools.config.mobcustomizer.spiderSpawnRatePercent")
                .defineInRange("spawnRatePercent", 100, 1, 300);
        SPIDER_FOLLOW_RANGE = BUILDER
                .comment("Detection range (vanilla: 16)")
                .translation("tantantools.config.mobcustomizer.spiderFollowRange")
                .defineInRange("followRange", 16.0, 8.0, 64.0);
        SPIDER_MOVEMENT_SPEED = BUILDER
                .comment("Movement speed (vanilla: 0.3)")
                .translation("tantantools.config.mobcustomizer.spiderMovementSpeed")
                .defineInRange("movementSpeed", 0.3, 0.1, 0.8);
        SPIDER_ATTACK_DAMAGE = BUILDER
                .comment("Attack damage (vanilla: 2.0)")
                .translation("tantantools.config.mobcustomizer.spiderAttackDamage")
                .defineInRange("attackDamage", 2.0, 1.0, 15.0);
        BUILDER.pop();

        // ===== ENDERMAN SECTION =====
        BUILDER.comment("Enderman spawn and behavior settings").push("enderman");
        ALLOW_ENDERMAN_SPAWN = BUILDER
                .comment("Allow endermen to spawn")
                .translation("tantantools.config.mobcustomizer.allowEndermanSpawn")
                .define("allowSpawn", false);
        ENDERMAN_SPAWN_RATE_PERCENT = BUILDER
                .comment("Spawn amount, as a percentage of vanilla (1-300, default 100). Below 100 = fewer mobs, above 100 = extra mobs spawn alongside each natural spawn")
                .translation("tantantools.config.mobcustomizer.endermanSpawnRatePercent")
                .defineInRange("spawnRatePercent", 100, 1, 300);
        ENDERMAN_FOLLOW_RANGE = BUILDER
                .comment("Detection range (vanilla: 64)")
                .translation("tantantools.config.mobcustomizer.endermanFollowRange")
                .defineInRange("followRange", 64.0, 16.0, 128.0);
        ENDERMAN_MOVEMENT_SPEED = BUILDER
                .comment("Movement speed (vanilla: 0.3)")
                .translation("tantantools.config.mobcustomizer.endermanMovementSpeed")
                .defineInRange("movementSpeed", 0.3, 0.1, 0.8);
        ENDERMAN_ATTACK_DAMAGE = BUILDER
                .comment("Attack damage (vanilla: 7.0)")
                .translation("tantantools.config.mobcustomizer.endermanAttackDamage")
                .defineInRange("attackDamage", 7.0, 1.0, 30.0);
        BUILDER.pop();

        // ===== WITCH SECTION =====
        BUILDER.comment("Witch spawn and behavior settings").push("witch");
        ALLOW_WITCH_SPAWN = BUILDER
                .comment("Allow witches to spawn")
                .translation("tantantools.config.mobcustomizer.allowWitchSpawn")
                .define("allowSpawn", false);
        WITCH_SPAWN_RATE_PERCENT = BUILDER
                .comment("Spawn amount, as a percentage of vanilla (1-300, default 100). Below 100 = fewer mobs, above 100 = extra mobs spawn alongside each natural spawn")
                .translation("tantantools.config.mobcustomizer.witchSpawnRatePercent")
                .defineInRange("spawnRatePercent", 100, 1, 300);
        WITCH_FOLLOW_RANGE = BUILDER
                .comment("Detection range (vanilla: 16)")
                .translation("tantantools.config.mobcustomizer.witchFollowRange")
                .defineInRange("followRange", 16.0, 8.0, 64.0);
        WITCH_MOVEMENT_SPEED = BUILDER
                .comment("Movement speed (vanilla: 0.25)")
                .translation("tantantools.config.mobcustomizer.witchMovementSpeed")
                .defineInRange("movementSpeed", 0.25, 0.1, 0.8);
        BUILDER.pop();

        // ===== SLIME SECTION =====
        BUILDER.comment("Slime spawn and behavior settings").push("slime");
        ALLOW_SLIME_SPAWN = BUILDER
                .comment("Allow slimes to spawn")
                .translation("tantantools.config.mobcustomizer.allowSlimeSpawn")
                .define("allowSpawn", false);
        SLIME_SPAWN_RATE_PERCENT = BUILDER
                .comment("Spawn amount, as a percentage of vanilla (1-300, default 100). Below 100 = fewer mobs, above 100 = extra mobs spawn alongside each natural spawn")
                .translation("tantantools.config.mobcustomizer.slimeSpawnRatePercent")
                .defineInRange("spawnRatePercent", 100, 1, 300);
        SLIME_FOLLOW_RANGE = BUILDER
                .comment("Detection range (vanilla: 16)")
                .translation("tantantools.config.mobcustomizer.slimeFollowRange")
                .defineInRange("followRange", 16.0, 8.0, 64.0);
        SLIME_MOVEMENT_SPEED = BUILDER
                .comment("Movement speed (vanilla: varies by size)")
                .translation("tantantools.config.mobcustomizer.slimeMovementSpeed")
                .defineInRange("movementSpeed", 0.3, 0.1, 1.0);
        SLIME_ATTACK_DAMAGE = BUILDER
                .comment("Attack damage (vanilla: 2-6 by size)")
                .translation("tantantools.config.mobcustomizer.slimeAttackDamage")
                .defineInRange("attackDamage", 2.0, 1.0, 15.0);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private MobCustomizerConfig() {}
}
