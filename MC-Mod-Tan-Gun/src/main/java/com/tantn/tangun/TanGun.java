package com.tantn.tangun;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.core.component.DataComponents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(TanGun.MOD_ID)
public final class TanGun {
    public static final String MOD_ID = "tangun";
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredItem<TanGunItem> TAN_GUN = ITEMS.register("tan_gun",
        () -> new TanGunItem(new Item.Properties()
            .setId(ResourceKey.create(Registries.ITEM, id("tan_gun")))
            .enchantable(15)
            .component(DataComponents.WEAPON, new Weapon(1))
            .stacksTo(1)));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB =
        CREATIVE_TABS.register("tan_gun", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tangun"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> TAN_GUN.get().getDefaultInstance())
            .displayItems((parameters, output) -> output.accept(TAN_GUN.get()))
            .build());

    public TanGun(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        modEventBus.addListener(TanGunPacket::register);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.register(TanGunServer.class);
    }

    private static net.minecraft.resources.Identifier id(String path) {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}