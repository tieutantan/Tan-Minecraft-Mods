package com.tantn.tangun;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class TanGunItem extends Item {
    public static final int FIRE_INTERVAL_TICKS = 4;
    private static final double RANGE = 100.0D;
    private static final float DAMAGE = 5.0F;
    private static final int TRACER_PARTICLES = 2;
    private static final float ROUNDS_PER_SECOND = 20.0F / FIRE_INTERVAL_TICKS;

    public TanGunItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay,
            Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.tangun.damage", (int) DAMAGE)
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        tooltipAdder.accept(Component.translatable("tooltip.tangun.fire_rate", (int) ROUNDS_PER_SECOND)
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        tooltipAdder.accept(Component.translatable("tooltip.tangun.author")
            .withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
    }

    public static boolean fire(ServerLevel level, ServerPlayer player) {
        if (!player.getAbilities().instabuild && !consumeAmmo(player)) {
            player.playSound(SoundEvents.DISPENSER_FAIL, 0.8F, 1.2F);
            player.sendOverlayMessage(Component.translatable("message.tangun.out_of_ammo"));
            return false;
        }

        player.sendOverlayMessage(Component.translatable(
            "message.tangun.ammo", player.getAbilities().instabuild
                ? Component.translatable("message.tangun.unlimited") : countAmmo(player)));

        Vec3 start = player.getEyePosition();
        Vec3 direction = player.getViewVector(1.0F);
        Vec3 maximumEnd = start.add(direction.scale(RANGE));
        BlockHitResult blockHit = level.clip(new ClipContext(
            start, maximumEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 end = blockHit.getType() == HitResult.Type.MISS ? maximumEnd : blockHit.getLocation();
        AABB searchBox = player.getBoundingBox().expandTowards(end.subtract(start)).inflate(1.0D);
        List<Entity> targets = level.getEntities(player, searchBox,
            entity -> entity instanceof LivingEntity && entity.isPickable() && entity != player);
        targets.sort((first, second) -> Double.compare(
            first.distanceToSqr(start), second.distanceToSqr(start)));

        Vec3 tracerEnd = end;
        for (Entity target : targets) {
            java.util.Optional<Vec3> targetHit = target.getBoundingBox().inflate(0.2D).clip(start, end);
            if (targetHit.isPresent()) {
                if (tracerEnd == end) {
                    tracerEnd = targetHit.get();
                }
                if (target instanceof LivingEntity livingTarget) {
                    livingTarget.invulnerableTime = 0;
                }
                Vec3 velocityBeforeHit = target.getDeltaMovement();
                target.hurtServer(level, level.damageSources().playerAttack(player), DAMAGE);
                target.setDeltaMovement(velocityBeforeHit);
            }
        }

        spawnTracer(level, start, tracerEnd);
        player.playSound(SoundEvents.GENERIC_EXPLODE.value(), 0.18F, 2.0F);
        return true;
    }

    private static void spawnTracer(ServerLevel level, Vec3 start, Vec3 end) {
        Vec3 difference = end.subtract(start);
        double distance = difference.length();
        if (distance <= 0.0D) {
            return;
        }

        Vec3 direction = difference.normalize();
        int particleCount = Math.min(TRACER_PARTICLES, Math.max(2, (int) Math.ceil(distance / 2.0D)));
        for (int index = 1; index <= particleCount; index++) {
            Vec3 position = start.add(direction.scale(distance * index / (particleCount + 1.0D)));
            level.sendParticles(ParticleTypes.CRIT, position.x, position.y, position.z,
                1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private static boolean consumeAmmo(ServerPlayer player) {
        int slot = findAmmoSlot(player);
        if (slot < 0) {
            return false;
        }
        player.getInventory().getItem(slot).shrink(1);
        return true;
    }

    private static int findAmmoSlot(ServerPlayer player) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            if (player.getInventory().getItem(slot).is(Items.IRON_NUGGET)) {
                return slot;
            }
        }
        return -1;
    }

    private static int countAmmo(ServerPlayer player) {
        int count = 0;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.is(Items.IRON_NUGGET)) {
                count += stack.getCount();
            }
        }
        return count;
    }
}