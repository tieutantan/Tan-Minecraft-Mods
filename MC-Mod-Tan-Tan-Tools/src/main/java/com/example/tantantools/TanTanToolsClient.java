package com.example.tantantools;

import com.example.tantantools.gui.TanTanToolsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ScreenEvent;

@Mod(value = TanTanTools.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = TanTanTools.MODID)
public final class TanTanToolsClient {

    @SubscribeEvent
    public static void onInitScreen(final ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof PauseScreen pauseScreen) || !pauseScreen.showsPauseMenu()) {
            return;
        }

        int bottom = screen.height / 4;
        for (var child : screen.children()) {
            if (child instanceof AbstractWidget widget) {
                bottom = Math.max(bottom, widget.getY() + widget.getHeight());
            }
        }

        event.addListener(Button.builder(
            Component.translatable("gui.tan_tan_tools.settings"),
                button -> Minecraft.getInstance().setScreen(new TanTanToolsScreen()))
            .pos(screen.width / 2 - 100, Math.min(bottom + 4, screen.height - 24))
            .size(200, 20)
            .build());
    }
}
