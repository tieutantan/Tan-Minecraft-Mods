package com.example.tantantools.expfromnature;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

import java.util.Set;

/**
 * +XP khi người chơi phá block đá vanilla hoặc block thuộc nhóm "tree-like".
 */
public final class ExpFromNatureEvents {

    private static final Set<String> STONE_NAME_PARTS = Set.of(
            "andesite", "basalt", "blackstone", "cobblestone", "calcite", "deepslate",
            "diorite", "dripstone", "end_stone", "granite", "netherrack", "prismarine",
            "red_sandstone", "sandstone", "stone", "tuff"
    );

    @SuppressWarnings("unchecked")
    private static final TagKey<Block>[] ORE_TAGS = new TagKey[]{
            BlockTags.COAL_ORES,
            BlockTags.COPPER_ORES,
            BlockTags.DIAMOND_ORES,
            BlockTags.EMERALD_ORES,
            BlockTags.GOLD_ORES,
            BlockTags.IRON_ORES,
            BlockTags.LAPIS_ORES,
            BlockTags.REDSTONE_ORES
    };

    @SuppressWarnings("unchecked")
    private static final TagKey<Block>[] STONE_TAGS = new TagKey[]{
            BlockTags.BASE_STONE_OVERWORLD,
            BlockTags.BASE_STONE_NETHER,
            BlockTags.STONE_ORE_REPLACEABLES,
            BlockTags.DEEPSLATE_ORE_REPLACEABLES
    };

    @SuppressWarnings("unchecked")
    private static final TagKey<Block>[] TREE_TAGS = new TagKey[]{
            BlockTags.LOGS,
            BlockTags.LEAVES
    };

    public ExpFromNatureEvents() {}

    @SubscribeEvent
    public void onStoneBlockBreak(final BreakBlockEvent event) {
        if (!ExpFromNatureConfig.ENABLED.get()) return;
        if (!(event.getPlayer() instanceof ServerPlayer sp)) return;
        if (event.isCanceled()) return;
        if (sp.isCreative()) return;

        int xpPerStoneBlock = ExpFromNatureConfig.XP_PER_STONE_BLOCK.get();
        if (xpPerStoneBlock <= 0) return;

        final BlockState state = event.getState();
        if (state.isAir()) return;

        if (!isStoneLike(state)) return;

        awardXp(sp, xpPerStoneBlock);
    }

    @SubscribeEvent
    public void onTreeBlockBreak(final BreakBlockEvent event) {
        if (!ExpFromNatureConfig.ENABLED.get()) return;
        if (!(event.getPlayer() instanceof ServerPlayer sp)) return;
        if (event.isCanceled()) return;
        if (sp.isCreative()) return;

        int xpPerTreeBlock = ExpFromNatureConfig.XP_PER_TREE_BLOCK.get();
        if (xpPerTreeBlock <= 0) return;

        final BlockState state = event.getState();
        if (state.isAir()) return;

        boolean isTreeLike = false;
        for (final TagKey<Block> tag : TREE_TAGS) {
            if (state.is(tag)) { isTreeLike = true; break; }
        }
        if (!isTreeLike) return;

        awardXp(sp, xpPerTreeBlock);
    }

    private static boolean isStoneLike(final BlockState state) {
        for (final TagKey<Block> tag : ORE_TAGS) {
            if (state.is(tag)) return false;
        }

        for (final TagKey<Block> tag : STONE_TAGS) {
            if (state.is(tag)) return true;
        }

        final String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        for (final String namePart : STONE_NAME_PARTS) {
            if (path.contains(namePart)) return true;
        }
        return false;
    }

    private static void awardXp(final ServerPlayer sp, final int amount) {
        final ServerLevel sl = sp.level();

        // Lấy hướng nhìn chuẩn hóa
        final Vec3 lookDir = sp.getLookAngle().normalize();

        // Tính vị trí spawn: trước mặt 0.5 block, cao hơn mặt đất 0.4 block
        final Vec3 spot = new Vec3(
            sp.getX() + lookDir.x * 0.5,
            sp.getBoundingBox().minY + 0.4,
            sp.getZ() + lookDir.z * 0.5
        );

        ExperienceOrb.award(sl, spot, amount);
    }
}
