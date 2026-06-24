package com.example.examplemod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(AutoTransferItems.MOD_ID)
public class AutoTransferItems {
    public static final String MOD_ID = "autotransferitems";

    public AutoTransferItems(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(AutoTransferPacket::register);
    }
}
