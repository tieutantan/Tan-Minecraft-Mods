package com.tantn.tangun;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class TanGunServer {
    private static final Map<ServerPlayer, Integer> HELD_PLAYERS = new HashMap<>();

    private TanGunServer() {
    }

    public static void setHeld(ServerPlayer player, boolean held) {
        if (held) {
            HELD_PLAYERS.putIfAbsent(player, 0);
        } else {
            HELD_PLAYERS.remove(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            HELD_PLAYERS.remove(player);
        }
    }

    @SubscribeEvent
    public static void tick(ServerTickEvent.Post event) {
        HELD_PLAYERS.entrySet().removeIf(entry -> {
            ServerPlayer player = entry.getKey();
            if (!player.isAlive() || !player.getMainHandItem().is(TanGun.TAN_GUN.get())) {
                return true;
            }

            int ticksSinceShot = entry.getValue();
            if (ticksSinceShot == 0) {
                TanGunItem.fire((net.minecraft.server.level.ServerLevel) player.level(), player);
            }
            entry.setValue((ticksSinceShot + 1) % TanGunItem.FIRE_INTERVAL_TICKS);
            return false;
        });
    }
}