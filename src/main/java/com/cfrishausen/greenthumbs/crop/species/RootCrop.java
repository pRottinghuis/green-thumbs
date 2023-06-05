package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Species for crops that are similar to potatoes and carrots
 */
public class RootCrop extends BasicCrop{

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D)};

    public RootCrop(GTGenomeCropBlockItem seeds, Item crop) {
        super(seeds, crop);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, ICropEntity cropEntity) {
        if (this.SHAPE_BY_AGE[cropEntity.getAge()] != null) {
            return this.SHAPE_BY_AGE[cropEntity.getAge()];
        }
        GreenThumbs.LOGGER.warn("CarrotLikeCrop species does not have a voxel shape for {} for age {}", cropEntity, cropEntity.getAge());
        return this.SHAPE_BY_AGE[0];
    }
}
