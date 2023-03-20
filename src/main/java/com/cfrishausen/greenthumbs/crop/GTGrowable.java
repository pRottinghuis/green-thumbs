package com.cfrishausen.greenthumbs.crop;

import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface GTGrowable extends BonemealableBlock {

    default boolean isRandomlyTicking() {
        return true;
    }

    void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random);

    @Override
    default boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean p_50900_) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof GTCropBlockEntity cropEntity) {
            return !cropEntity.isMaxAge();
        }
        return false;
    }

    @Override
    default boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        return true;
    }

    @Override
    default void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof GTCropBlockEntity cropEntity) {
            cropEntity.growCrops(level);
        }
    }
}
