package com.example.tantantools.autoeat;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class AutoEatConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Enable or disable auto-eat feature globally")
            .translation("tantantools.config.autoeat.enabled")
            .define("enabled", true);

    /** How many ticks between each eat check (default 80 = 4 seconds) */
    public static final ModConfigSpec.IntValue EAT_CHECK_INTERVAL_TICKS = BUILDER
            .comment("How many ticks between each auto-eat check (20 ticks = 1 second, default 80)")
            .translation("tantantools.config.autoeat.eatCheckIntervalTicks")
            .defineInRange("eatCheckIntervalTicks", 80, 20, 200);

    /** Eat when hunger % is below this (default 70 = 70% = 14 food bars) */
    public static final ModConfigSpec.IntValue LOW_HUNGER_PERCENT = BUILDER
            .comment("Eat when hunger drops below this percentage of max (default 70 = eat below 14/20 food)")
            .translation("tantantools.config.autoeat.lowHungerPercent")
            .defineInRange("lowHungerPercent", 70, 10, 100);

    /** If health is at or below this %, eat whenever hungry at all (default 85) */
    public static final ModConfigSpec.IntValue LOW_HEALTH_PERCENT = BUILDER
            .comment("When health is at or below this percentage of max HP, eat whenever any hunger is needed (default 85)")
            .translation("tantantools.config.autoeat.lowHealthPercent")
            .defineInRange("lowHealthPercent", 85, 10, 100);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private AutoEatConfig() {}
}
