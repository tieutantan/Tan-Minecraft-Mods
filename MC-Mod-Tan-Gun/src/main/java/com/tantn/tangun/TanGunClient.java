package com.tantn.tangun;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = TanGun.MOD_ID)
public final class TanGunClient {
    private static boolean sentHeld;
    private static boolean firing;
    private static float shakePhase;

    private TanGunClient() {
    }

    @SubscribeEvent
    public static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        sentHeld = false;
        firing = false;
        shakePhase = 0.0F;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean triggerPressed = minecraft.player != null && minecraft.screen == null &&
            minecraft.player.getMainHandItem().is(TanGun.TAN_GUN.get()) &&
            minecraft.mouseHandler.isLeftPressed();
        if (triggerPressed != sentHeld) {
            sentHeld = triggerPressed;
            TanGunPacket.sendToServer(triggerPressed);
        }

        boolean shouldShake = triggerPressed && minecraft.player != null &&
            (minecraft.player.getAbilities().instabuild || hasAmmo(minecraft.player));
        if (shouldShake != firing) {
            firing = shouldShake;
            if (!firing) {
                shakePhase = 0.0F;
            }
        }
        if (firing) {
            shakePhase += 1.35F;
        }
    }

    private static boolean hasAmmo(Player player) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            if (player.getInventory().getItem(slot).is(Items.IRON_NUGGET)) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (!firing || event.getHand() != InteractionHand.MAIN_HAND ||
            !event.getItemStack().is(TanGun.TAN_GUN.get())) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        float horizontal = (float) Math.sin(shakePhase) * 0.018F;
        float vertical = (float) Math.cos(shakePhase * 1.17F) * 0.014F;
        float roll = (float) Math.sin(shakePhase * 0.83F) * 1.8F;
        float pitch = (float) Math.cos(shakePhase * 1.11F) * 1.2F;
        poseStack.translate(horizontal, vertical, 0.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(roll));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
    }
}