package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.custom.GTSimpleCropBlock;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeetrootCrop extends BasicCrop{

    private final int MAX_AGE = 3;

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D)};

    public BeetrootCrop(GTGenomeCropBlockItem seeds, Item crop) {
        super(seeds, crop);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, GTSimpleCropBlock block, ICropEntity cropEntity) {
        if (random.nextInt(3) != 0) {
            super.randomTick(state, level, pos, random, block, cropEntity);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, ICropEntity cropEntity) {
        if (this.SHAPE_BY_AGE[cropEntity.getAge()] != null) {
            return this.SHAPE_BY_AGE[cropEntity.getAge()];
        }
        GreenThumbs.LOGGER.warn("BeetrootCrop species does not have a voxel shape for {} for age {}", cropEntity, cropEntity.getAge());
        return this.SHAPE_BY_AGE[0];
    }

    @Override
    public int getMaxAge() {
        return this.MAX_AGE;
    }

    @Override
    public int getBonemealAgeIncrease(Level level) {
        return super.getBonemealAgeIncrease(level) / this.MAX_AGE;
    }
}
