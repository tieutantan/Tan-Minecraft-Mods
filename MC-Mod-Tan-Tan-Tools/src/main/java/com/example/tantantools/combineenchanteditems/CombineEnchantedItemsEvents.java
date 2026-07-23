package com.example.tantantools.combineenchanteditems;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

public final class CombineEnchantedItemsEvents {

    public CombineEnchantedItemsEvents() {}

    @SubscribeEvent
    public void onAnvilUpdate(final AnvilUpdateEvent event) {
        int percent = CombineEnchantedItemsConfig.XP_COST_PERCENT.get();
        if (percent >= 100) return;

        int vanillaCost = event.getXpCost();
        if (vanillaCost <= 0) return;

        event.setXpCost(Math.max(1, vanillaCost * percent / 100));
    }
}