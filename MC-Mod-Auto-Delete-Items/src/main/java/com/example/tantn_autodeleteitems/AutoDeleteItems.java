package com.example.tantn_autodeleteitems;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(AutoDeleteItems.MODID)
public final class AutoDeleteItems {
    public static final String MODID = "tantn_autodeleteitems";

    public AutoDeleteItems() {
        Config.load();
        NeoForge.EVENT_BUS.register(new AutoDeleteEvents());
    }
}
