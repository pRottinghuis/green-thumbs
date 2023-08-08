package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.block.custom.GTSimpleCropBlock;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.state.CropState;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StemBlockCrop extends BasicCrop {

    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;

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

    private final GTSimpleCropBlock fruit;

    public StemBlockCrop(String name, GTGenomeCropBlockItem seed, Item crop, GTGenomeCropBlockItem cutting, GTSimpleCropBlock fruit) {
        super(name, seed, crop, cutting);
        this.fruit = fruit;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<ICropSpecies, CropState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE);
    }
    /*
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, GTSimpleCropBlock block, ICropEntity cropEntity) {
        if (!level.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (level.getRawBrightness(pos, 0) >= 9) {
            float f = cropEntity.getGenome().getGrowthSpeed(block, level, pos);
            if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(level, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0)) {
                int i = state.getValue(AGE);
                if (i < 7) {
                    level.setBlock(pos, state.setValue(AGE, Integer.valueOf(i + 1)), 2);
                } else {
                    Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                    BlockPos blockpos = pos.relative(direction);
                    BlockState blockstate = level.getBlockState(blockpos.below());
                    if (level.isEmptyBlock(blockpos) && (blockstate.canSustainPlant(level, blockpos.below(), Direction.UP, this.fruit) || blockstate.is(Blocks.FARMLAND) || blockstate.is(BlockTags.DIRT))) {
                        level.setBlockAndUpdate(blockpos, this.fruit.defaultBlockState());
                        level.setBlockAndUpdate(pos, this.fruit.getAttachedStem().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, direction));
                    }
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(level, pos, state);
            }

        }
    }*/

    @Override
    public boolean doesQuickReplant() {
        return false;
    }
}
