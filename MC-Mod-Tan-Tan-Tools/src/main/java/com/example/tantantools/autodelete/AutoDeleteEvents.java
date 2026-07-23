package com.example.tantantools.autodelete;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Periodically scans the player's inventory for items in the delete list
 * and removes them. Runs on the configured interval (default: 1 minute).
 */
public final class AutoDeleteEvents {

    // Local registry reference for fast lookup
    private static final Registry<Item> ITEM_REGISTRY = BuiltInRegistries.ITEM;
    private final Map<UUID, Long> nextScanByPlayer = new HashMap<>();
    private int cachedIntervalMinutes = -1;
    private boolean cachedEnabled;
    private Set<String> cachedDeleteList = Set.of();

    public AutoDeleteEvents() {
        refreshConfiguration();
    }

    public void refreshConfiguration() {
        cachedIntervalMinutes = AutoDeleteConfig.DELETE_INTERVAL_MINUTES.get();
        cachedEnabled = AutoDeleteConfig.ENABLED.get();
        cachedDeleteList = new HashSet<>(AutoDeleteConfig.DELETE_LIST.get());
        nextScanByPlayer.clear();
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!cachedEnabled || cachedDeleteList.isEmpty()) {
            return;
        }

        long currentGameTime = player.level().getGameTime();
        long nextScan = nextScanByPlayer.computeIfAbsent(
                player.getUUID(), ignored -> currentGameTime + Math.max(1, cachedIntervalMinutes) * 20L * 60L);
        if (currentGameTime < nextScan) return;
        nextScanByPlayer.put(player.getUUID(), currentGameTime + Math.max(1, cachedIntervalMinutes) * 20L * 60L);

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
                int previousCount = deletedItems.getOrDefault(key, 0);
                deletedItems.put(key, previousCount + stack.getCount());
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

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        nextScanByPlayer.remove(event.getEntity().getUUID());
    }
}
