package com.tantn.tangun;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

public final class TanGunLoopSound extends AbstractTickableSoundInstance {
    private final Player player;

    public TanGunLoopSound(Player player) {
        super(TanGun.GUNSHOT.value(), SoundSource.PLAYERS, RandomSource.create());
        this.player = player;
        this.looping = true;
        this.volume = 0.8F;
        this.pitch = 1.0F;
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }

    @Override
    public void tick() {
        if (!this.player.isRemoved()) {
            this.x = this.player.getX();
            this.y = this.player.getY();
            this.z = this.player.getZ();
        }
    }

    public void stopPlaying() {
        stop();
    }
}