package com.example.tantantools;

import com.example.tantantools.gui.TanTanToolsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

@Mod(value = TanTanTools.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = TanTanTools.MODID)
public final class TanTanToolsClient {

    private static final KeyMapping OPEN_SETTINGS = new KeyMapping(
            "key.tan_tan_tools.open_settings",
            GLFW.GLFW_KEY_O,
            KeyMapping.Category.MISC
    );

    @SubscribeEvent
    public static void onRegisterKeys(final RegisterKeyMappingsEvent event) {
        event.register(OPEN_SETTINGS);
    }

    public static final class ClientEvents {
        @SubscribeEvent
        public static void onKeyInput(final InputEvent.Key event) {
            if (event.getAction() == GLFW.GLFW_PRESS && OPEN_SETTINGS.isDown()) {
                Minecraft.getInstance().setScreen(new TanTanToolsScreen());
            }
        }
    }

    public TanTanToolsClient() {
        // Register NeoForge-bus handlers here (InputEvent is a global bus event)
        NeoForge.EVENT_BUS.register(ClientEvents.class);
    }
}
