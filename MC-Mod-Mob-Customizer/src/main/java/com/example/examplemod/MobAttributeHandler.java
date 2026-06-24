package com.example.mobcustomizer;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

@EventBusSubscriber(modid = MobCustomizer.MODID)
public class MobAttributeHandler {
    
    @SubscribeEvent
    public static void modifyMobAttributes(EntityAttributeModificationEvent event) {
        // Set vanilla defaults - config will override per-entity in MobSpawnCustomizer
        
        // Zombie - Vanilla defaults
        event.add(EntityType.ZOMBIE, Attributes.FOLLOW_RANGE, 35.0);
        event.add(EntityType.ZOMBIE, Attributes.MOVEMENT_SPEED, 0.23);
        event.add(EntityType.ZOMBIE, Attributes.ATTACK_DAMAGE, 3.0);
        
        // Creeper - Vanilla defaults
        event.add(EntityType.CREEPER, Attributes.FOLLOW_RANGE, 16.0);
        event.add(EntityType.CREEPER, Attributes.MOVEMENT_SPEED, 0.25);
        
        // Skeleton - Vanilla defaults
        event.add(EntityType.SKELETON, Attributes.FOLLOW_RANGE, 15.0);
        event.add(EntityType.SKELETON, Attributes.MOVEMENT_SPEED, 0.25);
        event.add(EntityType.SKELETON, Attributes.ATTACK_DAMAGE, 2.0);
        
        // Spider - Vanilla defaults
        event.add(EntityType.SPIDER, Attributes.FOLLOW_RANGE, 16.0);
        event.add(EntityType.SPIDER, Attributes.MOVEMENT_SPEED, 0.3);
        event.add(EntityType.SPIDER, Attributes.ATTACK_DAMAGE, 2.0);
        
        // Enderman - Vanilla defaults
        event.add(EntityType.ENDERMAN, Attributes.FOLLOW_RANGE, 64.0);
        event.add(EntityType.ENDERMAN, Attributes.MOVEMENT_SPEED, 0.3);
        event.add(EntityType.ENDERMAN, Attributes.ATTACK_DAMAGE, 7.0);
        
        // Witch - Vanilla defaults
        event.add(EntityType.WITCH, Attributes.FOLLOW_RANGE, 16.0);
        event.add(EntityType.WITCH, Attributes.MOVEMENT_SPEED, 0.25);
        
        // Slime - Vanilla defaults
        event.add(EntityType.SLIME, Attributes.FOLLOW_RANGE, 16.0);
        event.add(EntityType.SLIME, Attributes.MOVEMENT_SPEED, 0.3);
        event.add(EntityType.SLIME, Attributes.ATTACK_DAMAGE, 2.0);
        
        MobCustomizer.LOGGER.info("Base mob attributes registered (vanilla defaults)");
    }
}
