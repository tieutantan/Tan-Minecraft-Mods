package com.example.examplemod;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = AutoTransferItems.MOD_ID, value = Dist.CLIENT)
public class AutoTransferItemsClient {

    @SubscribeEvent
    public static void onInitScreen(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen) ||
            screen instanceof CreativeModeInventoryScreen) {
            return;
        }

        int x = containerScreen.getLeftPos() + containerScreen.getImageWidth() - 20;
        int y = containerScreen.getTopPos() + 5;

        event.addListener(Button.builder(Component.literal("▶"), b -> AutoTransferPacket.sendToServer())
            .pos(x, y)
            .size(18, 18)
            .tooltip(Tooltip.create(Component.literal("Auto Transfer")))
            .build());
    }
}
