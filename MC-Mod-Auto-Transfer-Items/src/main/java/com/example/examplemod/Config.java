package com.example.examplemod;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue TRANSFER_HOTBAR = BUILDER
            .comment("Transfer hotbar (0-8) or only main inventory (9-35)")
            .define("transferHotbar", false);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> WHITELIST_ITEMS = BUILDER
            .comment("Whitelist items (empty = all). Format: 'minecraft:iron_ingot'")
            .defineListAllowEmpty("whitelistItems", List.of(), obj -> 
                obj instanceof String s && BuiltInRegistries.ITEM.containsKey(Identifier.parse(s)));

    public static final ModConfigSpec.IntValue MAX_TRANSFER_SLOTS = BUILDER
            .comment("Max container slots to transfer")
            .defineInRange("maxContainerSlots", 54, 1, 256);

    static final ModConfigSpec SPEC = BUILDER.build();
}
