package com.example.tantantools.cycletradesbetter;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CycleTradesBetterConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Upgrade Librarian enchanted book trades to the maximum enchantment level")
            .translation("tantantools.config.cycletradesbetter.enabled")
            .define("enabled", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private CycleTradesBetterConfig() {}
}