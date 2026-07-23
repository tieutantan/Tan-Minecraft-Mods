package com.example.tantantools;

import com.example.tantantools.autodelete.AutoDeleteConfig;
import com.example.tantantools.autodelete.AutoDeleteEvents;
import com.example.tantantools.autoeat.AutoEatConfig;
import com.example.tantantools.autoeat.AutoEatEvents;
import com.example.tantantools.autotransfer.AutoTransferConfig;
import com.example.tantantools.autotransfer.AutoTransferPacket;
import com.example.tantantools.expfromnature.ExpFromNatureConfig;
import com.example.tantantools.expfromnature.ExpFromNatureEvents;
import com.example.tantantools.combineenchanteditems.CombineEnchantedItemsConfig;
import com.example.tantantools.combineenchanteditems.CombineEnchantedItemsEvents;
import com.example.tantantools.mobcustomizer.MobCustomizerConfig;
import com.example.tantantools.mobcustomizer.MobSpawnCustomizer;
import com.example.tantantools.mobcustomizer.SpawnEventHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

/**
 * Tan Tan Tools — all-in-one toolbox mod merging:
 * Auto Delete Items, Auto Eat, Auto Transfer Items, Exp From Nature, Mob Customizer.
 */
@Mod(TanTanTools.MODID)
public final class TanTanTools {

    public static final String MODID = "tan_tan_tools";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TanTanTools(final IEventBus modEventBus, final ModContainer modContainer) {
        // Register per-feature configs under their own file names
        modContainer.registerConfig(ModConfig.Type.COMMON, AutoDeleteConfig.SPEC, MODID + "-autodelete.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, AutoEatConfig.SPEC, MODID + "-autoeat.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, AutoTransferConfig.SPEC, MODID + "-autotransfer.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, ExpFromNatureConfig.SPEC, MODID + "-expfromnature.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, MobCustomizerConfig.SPEC, MODID + "-mobcustomizer.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, CombineEnchantedItemsConfig.SPEC, MODID + "-combineenchanteditems.toml");

        // Network packet registration (mod event bus)
        modEventBus.addListener(AutoTransferPacket::register);

        // Feature event handlers (NeoForge global bus)
        AutoDeleteEvents autoDeleteEvents = new AutoDeleteEvents();
        NeoForge.EVENT_BUS.register(autoDeleteEvents);
        NeoForge.EVENT_BUS.register(new AutoEatEvents());
        NeoForge.EVENT_BUS.register(new ExpFromNatureEvents());
        NeoForge.EVENT_BUS.register(new CombineEnchantedItemsEvents());
        NeoForge.EVENT_BUS.register(new MobSpawnCustomizer());
        NeoForge.EVENT_BUS.register(new SpawnEventHandler());
        // MobAttributeHandler is auto-registered via @EventBusSubscriber — auto-routed to mod bus because EntityAttributeModificationEvent implements IModBusEvent

        NeoForge.EVENT_BUS.register(this);

        // Auto-refresh mob spawn cache when the mob customizer config changes (e.g. after Config GUI "Done")
        modEventBus.addListener(ModConfigEvent.Reloading.class, event -> {
            if (event.getConfig().getSpec() == AutoDeleteConfig.SPEC) {
                autoDeleteEvents.refreshConfiguration();
            }
            if (event.getConfig().getSpec() == MobCustomizerConfig.SPEC) {
                SpawnEventHandler.refreshCache();
                LOGGER.info("Mob Customizer config reloaded — spawn cache refreshed");
            }
        });

        LOGGER.info("Tan Tan Tools initializing...");
    }

    @SubscribeEvent
    public void onServerStarting(final ServerStartingEvent event) {
        registerCommands(event);
    }

    private void registerCommands(final ServerStartingEvent event) {
        event.getServer().getCommands().getDispatcher().register(
            Commands.literal("tantantools")
                .then(Commands.literal("mobcustom")
                    .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))
                    .then(Commands.literal("reload")
                        .executes(context -> {
                            SpawnEventHandler.refreshCache();
                            context.getSource().sendSuccess(() ->
                                Component.literal("§a[Tan Tan Tools] Mob Customizer config refreshed!"), true);
                            return 1;
                        })
                    )
                )
        );
    }
}
