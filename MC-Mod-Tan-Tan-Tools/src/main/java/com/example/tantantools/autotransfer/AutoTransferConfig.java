package com.example.tantantools.autotransfer;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class AutoTransferConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue TRANSFER_HOTBAR = BUILDER
            .comment("Transfer hotbar (slots 0-8) as well, or only main inventory (slots 9-35)")
            .translation("tantantools.config.autotransfer.transferHotbar")
            .define("transferHotbar", false);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> WHITELIST_ITEMS = BUILDER
            .comment("Whitelist items to transfer (empty = transfer all). Format: 'minecraft:iron_ingot'")
            .translation("tantantools.config.autotransfer.whitelistItems")
            .defineListAllowEmpty("whitelistItems", List.of(), obj ->
                obj instanceof String s && BuiltInRegistries.ITEM.containsKey(Identifier.parse(s)));

    public static final ModConfigSpec.IntValue MAX_TRANSFER_SLOTS = BUILDER
            .comment("Maximum number of container slots to fill during a transfer (default 54)")
            .translation("tantantools.config.autotransfer.maxContainerSlots")
            .defineInRange("maxContainerSlots", 54, 1, 256);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private AutoTransferConfig() {}
}
