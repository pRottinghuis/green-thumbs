package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.custom.GTSimpleCropBlock;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.state.CropState;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Beetroot like crops. Max age is 3.
 */
public class BeetrootCrop extends BasicCrop{

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D)};
    public static final IntegerProperty AGE_3 = BlockStateProperties.AGE_3;


    public BeetrootCrop(String name, Supplier<GTGenomeCropBlockItem> seeds, Supplier<Item> crop, Supplier<GTGenomeCropBlockItem> cutting) {
        super(name, seeds, crop, cutting);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<ICropSpecies, CropState> builder) {
        builder.add(this.AGE_3);
    }

    @Override
    public void registerDefaultState() {
        this.defaultCropState = this.cropStateDef.any().setValue(AGE_3, 0);
    }

    @Override
    public void randomTick(ServerLevel level, BlockPos pos, RandomSource random, GTSimpleCropBlock block, ICropEntity cropEntity) {
        // Reduce rate that beetroot grows because age is lower. See vanilla's BeetrootBlock.java
        if (random.nextInt(getMaxAge()) != 0) {
            super.randomTick(level, pos, random, block, cropEntity);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, ICropEntity cropEntity) {
        if (this.SHAPE_BY_AGE[getAge(cropEntity)] != null) {
            return this.SHAPE_BY_AGE[getAge(cropEntity)];
        }
        GreenThumbs.LOGGER.warn("{} missing voxel for age {}", this, cropEntity, getAge(cropEntity));
        return this.SHAPE_BY_AGE[0];
    }


    @Override
    public @NotNull IntegerProperty getAgeProperty() {
        return this.AGE_3;
    }

    @Override
    public int getBonemealAgeIncrease(Level level) {
        return super.getBonemealAgeIncrease(level) / this.getMaxAge();
    }
}
