package com.example.tantantools.autoeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.example.tantantools.TanTanTools;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class AutoEatEvents {
    private static final int MAX_FOOD_LEVEL = 20;
    private static final String MESSAGES_RESOURCE = "/autoeat_messages.txt";
    private static final List<String> EAT_MESSAGES = loadEatMessages();

    public AutoEatEvents() {
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!AutoEatConfig.ENABLED.get()) {
            return;
        }

        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (serverPlayer.isCreative() || serverPlayer.isSpectator()) {
            return;
        }

        FoodData foodData = serverPlayer.getFoodData();
        int foodLevel = foodData.getFoodLevel();
        int lowHealthPercent = AutoEatConfig.LOW_HEALTH_PERCENT.get();
        boolean lowHealth = isHealthAtOrBelowPercent(serverPlayer, lowHealthPercent);

        int lowHungerEatLevel = MAX_FOOD_LEVEL * AutoEatConfig.LOW_HUNGER_PERCENT.get() / 100;
        if (!shouldEat(foodData, foodLevel, lowHealth, lowHungerEatLevel)) {
            return;
        }

        int checkInterval = AutoEatConfig.EAT_CHECK_INTERVAL_TICKS.get();
        if (serverPlayer.tickCount % checkInterval != 0) {
            return;
        }

        Inventory inventory = serverPlayer.getInventory();
        boolean consumed = consumeSingleItemFromTop(serverPlayer, inventory, foodData);

        if (consumed) {
            inventory.setChanged();
            serverPlayer.containerMenu.broadcastChanges();
        }
    }

    private static boolean shouldEat(FoodData foodData, int foodLevel, boolean lowHealth, int lowHungerEatLevel) {
        if (lowHealth) {
            return foodData.needsFood();
        }

        return foodLevel < lowHungerEatLevel;
    }

    private static boolean isHealthAtOrBelowPercent(Player player, int healthPercent) {
        return player.getHealth() <= player.getMaxHealth() * healthPercent / 100.0F;
    }

    private static boolean consumeSingleItemFromTop(ServerPlayer player, Inventory inventory, FoodData foodData) {
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }

            FoodProperties properties = stack.get(DataComponents.FOOD);
            if (properties == null) {
                continue;
            }

            foodData.eat(properties.nutrition(), properties.saturation());
            stack.shrink(1);
            sendEatChat(player, stack.getCount());
            return true;
        }

        return false;
    }

    private static void sendEatChat(ServerPlayer player, int remainingInStack) {
        player.sendSystemMessage(Component.literal("[AutoEat] " + remainingInStack + " | " + randomEatMessage()));
    }

    private static String randomEatMessage() {
        return EAT_MESSAGES.get(ThreadLocalRandom.current().nextInt(EAT_MESSAGES.size()));
    }

    private static List<String> loadEatMessages() {
        try (InputStream stream = AutoEatEvents.class.getResourceAsStream(MESSAGES_RESOURCE)) {
            if (stream == null) {
                TanTanTools.LOGGER.warn("Could not find {}. Falling back to default messages.", MESSAGES_RESOURCE);
                return List.of("AutoEat active.");
            }

            List<String> lines = new ArrayList<>(512);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) {
                        lines.add(trimmed);
                    }
                }
            }

            if (lines.isEmpty()) {
                TanTanTools.LOGGER.warn("{} is empty. Falling back to default messages.", MESSAGES_RESOURCE);
                return List.of("AutoEat active.");
            }

            return Collections.unmodifiableList(lines);
        } catch (IOException exception) {
            TanTanTools.LOGGER.warn("Failed reading {}. Falling back to default messages.", MESSAGES_RESOURCE, exception);
            return List.of("AutoEat active.");
        }
    }
}
