package com.example.examplemod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import java.util.List;
import java.util.Set;

public record AutoTransferPacket() implements CustomPacketPayload {
    
    public static final Type<AutoTransferPacket> TYPE = 
        new Type<>(Identifier.fromNamespaceAndPath(AutoTransferItems.MOD_ID, "transfer"));
    
    public static final StreamCodec<FriendlyByteBuf, AutoTransferPacket> STREAM_CODEC = 
        StreamCodec.unit(new AutoTransferPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(AutoTransferItems.MOD_ID).playToServer(TYPE, STREAM_CODEC, AutoTransferPacket::handle);
    }

    public static void handle(AutoTransferPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (player == null) return;

            AbstractContainerMenu menu = player.containerMenu;
            if (menu == null) return;

            Inventory playerInv = player.getInventory();

            // Detect container slots (early exit)
            int containerSlotCount = 0;
            int slotCount = menu.slots.size();
            while (containerSlotCount < slotCount && menu.slots.get(containerSlotCount).container != playerInv) {
                containerSlotCount++;
            }
            if (containerSlotCount == 0) return;

            // Config (cache values)
            int startInv = Config.TRANSFER_HOTBAR.get() ? 0 : 9;
            List<? extends String> whitelistConfig = Config.WHITELIST_ITEMS.get();
            Set<Identifier> whitelist = whitelistConfig.isEmpty() ? null : 
                whitelistConfig.stream().map(Identifier::parse).collect(java.util.stream.Collectors.toSet());

            // Transfer loop
            for (int invIndex = startInv; invIndex < 36; invIndex++) {
                ItemStack stack = playerInv.getItem(invIndex);
                if (stack.isEmpty()) continue;

                // Whitelist check (skip if not in list)
                if (whitelist != null && !whitelist.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()))) {
                    continue;
                }

                // Find slot in container
                for (int i = 0; i < containerSlotCount; i++) {
                    Slot containerSlot = menu.slots.get(i);
                    ItemStack containerStack = containerSlot.getItem();

                    if (containerStack.isEmpty()) {
                        // Empty slot - move all
                        containerSlot.set(stack.copy());
                        playerInv.setItem(invIndex, ItemStack.EMPTY);
                        break;
                    } else if (ItemStack.isSameItemSameComponents(containerStack, stack)) {
                        // Same item - merge
                        int space = Math.min(containerStack.getMaxStackSize(), containerSlot.getMaxStackSize()) - containerStack.getCount();
                        if (space > 0) {
                            int transfer = Math.min(space, stack.getCount());
                            containerStack.grow(transfer);
                            stack.shrink(transfer);
                            if (stack.isEmpty()) {
                                playerInv.setItem(invIndex, ItemStack.EMPTY);
                                break;
                            }
                        }
                    }
                }
            }

            menu.broadcastChanges();
            menu.sendAllDataToRemote();
        });
    }

    public static void sendToServer() {
        net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(new AutoTransferPacket());
    }
}
