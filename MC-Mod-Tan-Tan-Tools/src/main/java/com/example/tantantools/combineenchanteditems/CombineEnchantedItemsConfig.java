package com.example.tantantools.combineenchanteditems;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CombineEnchantedItemsConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue XP_COST_PERCENT = BUILDER
            .comment("Percentage of the vanilla anvil XP cost to charge (0 to 100, default 100)")
            .translation("tantantools.config.combineenchanteditems.xpCostPercent")
            .defineInRange("xpCostPercent", 100, 0, 100);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private CombineEnchantedItemsConfig() {}
}