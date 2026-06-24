package com.example.mobcustomizer;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(MobCustomizer.MODID)
public class MobCustomizer {
    public static final String MODID = "mobcustomizer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MobCustomizer(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC, "mobcustomizer.toml");
        
        LOGGER.info("Mob Customizer mod initializing...");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("=== Mob Customizer Configuration ===");
        logConfig();
        LOGGER.info("====================================");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Mob Customizer mod active on server");
        registerCommands(event);
    }

    private static void logConfig() {
        LOGGER.info("Zombie Spawn: {}", Config.ALLOW_ZOMBIE_SPAWN.get());
        LOGGER.info("Weight Multiplier: {}x", Config.ZOMBIE_WEIGHT_MULTIPLIER.get());
        LOGGER.info("Group Size: {}-{}", Config.ZOMBIE_MIN_GROUP_SIZE.get(), Config.ZOMBIE_MAX_GROUP_SIZE.get());
        LOGGER.info("Speed: {} | Damage: {} | Range: {}", 
            Config.ZOMBIE_MOVEMENT_SPEED.get(),
            Config.ZOMBIE_ATTACK_DAMAGE.get(),
            Config.ZOMBIE_FOLLOW_RANGE.get());
    }

    private void registerCommands(ServerStartingEvent event) {
        event.getServer().getCommands().getDispatcher().register(
            Commands.literal("mobcustom")
                .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))
                .then(Commands.literal("reload")
                    .executes(context -> {
                        SpawnEventHandler.refreshCache();
                        context.getSource().sendSuccess(() -> 
                            Component.literal("§a[Mob Customizer] Config refreshed!"), true);
                        return 1;
                    })
                )
                .then(Commands.literal("status")
                    .executes(context -> {
                        context.getSource().sendSuccess(() -> 
                            Component.literal(String.format(
                                "§e=== Mob Customizer Status ===\n" +
                                "§7Zombie Spawn: §f%s\n" +
                                "§7Weight: §f%dx | §7Group: §f%d-%d\n" +
                                "§7Speed: §f%.2f | §7Damage: §f%.1f\n" +
                                "§7Creeper: §f%s §7Skeleton: §f%s §7Spider: §f%s\n" +
                                "§7Enderman: §f%s §7Witch: §f%s §7Slime: §f%s",
                                Config.ALLOW_ZOMBIE_SPAWN.get() ? "ON" : "OFF",
                                Config.ZOMBIE_WEIGHT_MULTIPLIER.get(),
                                Config.ZOMBIE_MIN_GROUP_SIZE.get(),
                                Config.ZOMBIE_MAX_GROUP_SIZE.get(),
                                Config.ZOMBIE_MOVEMENT_SPEED.get(),
                                Config.ZOMBIE_ATTACK_DAMAGE.get(),
                                Config.ALLOW_CREEPER_SPAWN.get() ? "ON" : "OFF",
                                Config.ALLOW_SKELETON_SPAWN.get() ? "ON" : "OFF",
                                Config.ALLOW_SPIDER_SPAWN.get() ? "ON" : "OFF",
                                Config.ALLOW_ENDERMAN_SPAWN.get() ? "ON" : "OFF",
                                Config.ALLOW_WITCH_SPAWN.get() ? "ON" : "OFF",
                                Config.ALLOW_SLIME_SPAWN.get() ? "ON" : "OFF"
                            )), false);
                        return 1;
                    })
                )
        );
    }
}
