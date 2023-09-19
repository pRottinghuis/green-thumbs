package com.cfrishausen.greenthumbs.block.custom;

import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.registries.GTItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Visual aspect of the crop.
 */
public class GTSimpleCropBlock extends Block implements IPlantable, BonemealableBlock, EntityBlock {

    VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

    public GTSimpleCropBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof GTCropBlockEntity cropEntity) {
            if (cropEntity.getCropSpecies() != null) {
                return cropEntity.getCropSpecies().getShape(state, level, pos, context, cropEntity);
            }
        }
        return SHAPE_BY_AGE[0];
    }

    // TODO move into crop species
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof GTCropBlockEntity cropEntity) {
                return cropEntity.getCropSpecies().use(state, level, pos, player, hand, hit, cropEntity);
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof GTCropBlockEntity cropEntity) {
            return cropEntity.getCropSpecies().canSurvive(state, level, pos, this);
        }
        return false;
    }

    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    /**
     * Performs a random tick on a block. Random tick means every tick certain number of blocks in a chunk are chosen to tick.
     */
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof GTCropBlockEntity cropEntity) {
            cropEntity.getCropSpecies().randomTick(level, pos, random, this, cropEntity);
        }

    }



    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.CROP;
    }

    @Override
    public BlockState getPlant(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() != this) return defaultBlockState();
        return state;
    }


    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean p_50900_) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof GTCropBlockEntity cropEntity) {
            return cropEntity.getCropSpecies().isValidBonemealTarget(level, pos, state, p_50900_, cropEntity);
        }
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        return true;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof GTCropBlockEntity cropEntity) {
            cropEntity.getCropSpecies().entityInside(state, level, pos, entity, cropEntity);
        } else {
            super.entityInside(state, level, pos, entity);
        }
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof GTCropBlockEntity cropEntity) {
            cropEntity.getCropSpecies().performBonemeal(level, random, pos, state, cropEntity);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GTCropBlockEntity(pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof GTCropBlockEntity cropEntity) {
            ItemStack stack = new ItemStack(cropEntity.getCropSpecies().getBaseItemId());
            stack.setTag(cropEntity.getUpdateTag());
            return stack;
        } else {
            return super.getCloneItemStack(level, pos, state);
        }
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        BlockEntity entity = level.getBlockEntity(currentPos);
        BlockState blockState = null;
        if (entity instanceof GTCropBlockEntity cropBlockEntity) {
            if (cropBlockEntity.getCropSpecies() != null) {
                blockState = cropBlockEntity.getCropSpecies().updateShape(state, direction, neighborState, level, currentPos, neighborPos, this, cropBlockEntity);
            }
        }
        // If there is no crop species use default block update shape
        return !(blockState == null) ? blockState : super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }


}
