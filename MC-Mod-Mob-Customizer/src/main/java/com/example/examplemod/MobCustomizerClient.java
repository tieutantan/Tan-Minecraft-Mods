package com.example.mobcustomizer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = MobCustomizer.MODID, dist = Dist.CLIENT)
public class MobCustomizerClient {
    
    public MobCustomizerClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        MobCustomizer.LOGGER.info("Config screen registered");
    }
}
