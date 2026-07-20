package com.tantn.tangun;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TanGunPacket(boolean held) implements CustomPacketPayload {
    public static final Type<TanGunPacket> TYPE = new Type<>(
        Identifier.fromNamespaceAndPath(TanGun.MOD_ID, "trigger"));
    public static final StreamCodec<FriendlyByteBuf, TanGunPacket> STREAM_CODEC =
        StreamCodec.of((buffer, packet) -> buffer.writeBoolean(packet.held),
            buffer -> new TanGunPacket(buffer.readBoolean()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(TanGun.MOD_ID).playToServer(TYPE, STREAM_CODEC, TanGunPacket::handle);
    }

    private static void handle(TanGunPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                TanGunServer.setHeld(player, packet.held);
            }
        });
    }

    public static void sendToServer(boolean held) {
        net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(
            new TanGunPacket(held));
    }
}