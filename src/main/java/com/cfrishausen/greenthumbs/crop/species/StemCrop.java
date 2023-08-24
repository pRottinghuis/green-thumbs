package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.block.custom.GTSimpleCropBlock;
import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.state.CropState;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public class StemCrop extends BasicCrop {
    protected static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 4.0D, 9.0D),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 12.0D, 9.0D),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D),
    };

    private final Supplier<ICropSpecies> fruit;

    public StemCrop(String name, GTGenomeCropBlockItem seed, Item crop, GTGenomeCropBlockItem cutting, Supplier<ICropSpecies> fruit) {
        super(name, seed, crop, cutting);
        this.fruit = fruit;
    }

    @Override
    public void randomTick(ServerLevel level, BlockPos pos, RandomSource random, GTSimpleCropBlock block, ICropEntity cropEntity) {
        if (!level.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (level.getRawBrightness(pos, 0) >= 9) {
            float f = cropEntity.getGenome().getGrowthSpeed(block, level, pos);
            // TODO change back to 25
            if (random.nextInt((int)(/*25.0F*/ 5.0F / f) + 1) == 0) {
                int i = cropEntity.getCropState().getValue(AGE);
                if (i < getMaxAge()) {
                    growCrops(level, cropEntity);
                } else {
                    Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                    BlockPos fruitPos = pos.relative(direction);
                    BlockState blockstate = level.getBlockState(fruitPos.below());
                    if (level.isEmptyBlock(fruitPos) && (blockstate.canSustainPlant(level, fruitPos.below(), Direction.UP, ((GTSimpleCropBlock) GTBlocks.GT_CROP_BLOCK.get())) || blockstate.is(Blocks.FARMLAND) || blockstate.is(BlockTags.DIRT))) {
                        // set block in targeted position to GTCropBlock and then set species to fruit
                        level.setBlockAndUpdate(fruitPos, GTBlocks.GT_VEGETABLE_BLOCK.get().defaultBlockState());
                        GTCropBlockEntity fruitCropBlockEntity = ((GTCropBlockEntity) level.getBlockEntity(fruitPos));
                        fruitCropBlockEntity.setCropSpecies(this.fruit.get());
                        fruitCropBlockEntity.setGenome(cropEntity.getGenome());
                        fruitCropBlockEntity.markUpdated();
                        // change the old stem to be an attached stem connected to the fruit
                        cropEntity.setCropSpecies(((StemGrownCrop) this.fruit.get()).getAttachedStemSpecies());
                        cropEntity.setCropState(cropEntity.getCropState().setValue(AttachedStemCrop.FACING, direction));
                    }
                }
            }

        }
    }

    @Override
    public boolean doesQuickReplant() {
        return false;
    }

    public StemGrownCrop getFruit() {
        return ((StemGrownCrop) this.fruit.get());
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, GTCropBlockEntity cropEntity) {
        super.performBonemeal(level, random, pos, state, cropEntity);
        // Ensure that there is a chance of growing a fruit
        state.randomTick(level, pos, random);
    }


}
