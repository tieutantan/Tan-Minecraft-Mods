package com.example.tantn_autodeleteitems;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Periodically scans the player's inventory for items in the delete list
 * and removes them. Runs on the configured interval (default: 5 minutes).
 *
 * Optimized: caches config values, uses local registry ref,
 * batches broadcastChanges into a single call.
 */
public final class AutoDeleteEvents {

    // Local registry reference for fast lookup
    private static final Registry<Item> ITEM_REGISTRY = BuiltInRegistries.ITEM;

    // Cache config values to avoid repeated lookups per tick
    private int cachedIntervalTicks = -1;
    private boolean cachedEnabled = true;
    private Set<String> cachedDeleteList = Set.of();

    public AutoDeleteEvents() {}

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // Refresh cache lazily — only when tickCount aligns with interval
        if (cachedIntervalTicks < 0 || player.tickCount % cachedIntervalTicks == 0) {
            int ticks = Config.getDeleteIntervalTicks();
            cachedIntervalTicks = ticks > 0 ? ticks : 6000;
            cachedEnabled = Config.isEnabled();
            cachedDeleteList = Config.getDeleteList();
        }

        if (player.tickCount % cachedIntervalTicks != 0 || !cachedEnabled || cachedDeleteList.isEmpty()) {
            return;
        }

        // Scan inventory
        Map<String, Integer> deletedItems = new HashMap<>();
        Inventory inventory = player.getInventory();
        int size = inventory.getContainerSize();
        var items = ITEM_REGISTRY;

        for (int slot = 0; slot < size; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty()) continue;

            Identifier id = items.getKey(stack.getItem());
            if (id == null) continue;

            String key = id.toString();
            if (cachedDeleteList.contains(key)) {
                deletedItems.merge(key, stack.getCount(), Integer::sum);
                inventory.setItem(slot, ItemStack.EMPTY);
            }
        }

        if (deletedItems.isEmpty()) return;

        // Notify player — single pass, reuse ItemStack for display name
        for (Map.Entry<String, Integer> entry : deletedItems.entrySet()) {
            String idStr = entry.getKey();
            int count = entry.getValue();

            ItemStack sample = new ItemStack(items.getValue(Identifier.parse(idStr)));
            String name = sample.getHoverName().getString();

            player.sendSystemMessage(
                Component.literal("§aDeleted " + count + "x " + name)
            );
        }

        inventory.setChanged();
        if (player.containerMenu != null) {
            player.containerMenu.broadcastChanges();
        }
    }
}
