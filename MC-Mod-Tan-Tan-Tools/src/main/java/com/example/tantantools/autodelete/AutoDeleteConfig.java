package com.example.tantantools.autodelete;

import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class AutoDeleteConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Enable or disable auto-delete feature globally")
            .translation("tantantools.config.autodelete.enabled")
            .define("enabled", true);

    public static final ModConfigSpec.IntValue DELETE_INTERVAL_MINUTES = BUILDER
            .comment("How often (in minutes) the inventory is scanned for items to delete (1-60, default 5)")
            .translation("tantantools.config.autodelete.deleteIntervalMinutes")
            .defineInRange("deleteIntervalMinutes", 5, 1, 60);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> DELETE_LIST = BUILDER
            .comment("List of item IDs to automatically delete from inventory. Format: 'minecraft:sand'")
            .translation("tantantools.config.autodelete.deleteList")
            .defineListAllowEmpty("deleteList", List.of(), obj -> obj instanceof String s && !s.isBlank());

    public static final ModConfigSpec SPEC = BUILDER.build();

    private AutoDeleteConfig() {}
}
