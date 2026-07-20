package com.example.tantantools.expfromnature;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ExpFromNatureConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Enable or disable exp-from-nature feature globally")
            .translation("tantantools.config.expfromnature.enabled")
            .define("enabled", true);

    public static final ModConfigSpec.IntValue XP_PER_STONE_BLOCK = BUILDER
            .comment("XP orbs to spawn when a stone-like block is broken (0 to disable, default 1)")
            .translation("tantantools.config.expfromnature.xpPerStoneBlock")
            .defineInRange("xpPerStoneBlock", 1, 0, 10);

    public static final ModConfigSpec.IntValue XP_PER_TREE_BLOCK = BUILDER
            .comment("XP orbs to spawn when a log or leaves block is broken (0 to disable, default 1)")
            .translation("tantantools.config.expfromnature.xpPerTreeBlock")
            .defineInRange("xpPerTreeBlock", 1, 0, 10);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ExpFromNatureConfig() {}
}
