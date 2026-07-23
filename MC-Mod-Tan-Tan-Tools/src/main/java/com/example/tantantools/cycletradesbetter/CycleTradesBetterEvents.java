package com.example.tantantools.cycletradesbetter;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public final class CycleTradesBetterEvents {

    public CycleTradesBetterEvents() {}

    @SubscribeEvent
    public void onEntityTick(final EntityTickEvent.Post event) {
        if (!CycleTradesBetterConfig.ENABLED.get() || !(event.getEntity() instanceof Villager villager)
                || villager.level().isClientSide()) {
            return;
        }
        if (!villager.getVillagerData().profession().is(VillagerProfession.LIBRARIAN)) {
            return;
        }
        if (!(villager.getTradingPlayer() instanceof ServerPlayer player)) {
            return;
        }

        MerchantOffers offers = villager.getOffers();
        if (upgradeOffers(offers)) {
            player.sendMerchantOffers(
                    player.containerMenu.containerId,
                    offers,
                    villager.getVillagerData().level(),
                    villager.getVillagerXp(),
                    villager.showProgressBar(),
                    villager.canRestock()
            );
        }
    }

    private static boolean upgradeOffers(final MerchantOffers offers) {
        boolean changedAny = false;
        for (int index = 0; index < offers.size(); index++) {
            MerchantOffer offer = offers.get(index);
            ItemStack result = offer.getResult();
            if (!result.is(Items.ENCHANTED_BOOK)) {
                continue;
            }

            ItemEnchantments storedEnchantments = result.get(DataComponents.STORED_ENCHANTMENTS);
            if (storedEnchantments == null || storedEnchantments.isEmpty()) {
                continue;
            }

            ItemEnchantments.Mutable upgradedEnchantments = new ItemEnchantments.Mutable(storedEnchantments);
            boolean changed = false;
            for (var entry : storedEnchantments.entrySet()) {
                Enchantment enchantment = entry.getKey().value();
                int maximumLevel = enchantment.getMaxLevel();
                if (maximumLevel > entry.getIntValue() && maximumLevel > 0) {
                    upgradedEnchantments.set(entry.getKey(), maximumLevel);
                    changed = true;
                }
            }
            if (!changed) {
                continue;
            }

            MerchantOffer upgradedOffer = offer.copy();
            upgradedOffer.getResult().set(DataComponents.STORED_ENCHANTMENTS, upgradedEnchantments.toImmutable());
            offers.set(index, upgradedOffer);
            changedAny = true;
        }
        return changedAny;
    }
}