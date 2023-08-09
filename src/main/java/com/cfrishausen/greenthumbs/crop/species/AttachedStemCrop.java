package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.state.CropState;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class AttachedStemCrop extends BasicCrop{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.SOUTH, Block.box(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 16.0D), Direction.WEST, Block.box(0.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D), Direction.NORTH, Block.box(6.0D, 0.0D, 0.0D, 10.0D, 10.0D, 10.0D), Direction.EAST, Block.box(6.0D, 0.0D, 6.0D, 16.0D, 10.0D, 10.0D)));
    private final StemGrownCrop fruit;



    public AttachedStemCrop(String name, GTGenomeCropBlockItem seed, Item crop, GTGenomeCropBlockItem cutting, StemGrownCrop fruit) {
        super(name, seed, crop, cutting);
        this.fruit = fruit;
        this.registerDefaultState(this.cropStateDef.any().setValue(AGE, 0).setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, ICropEntity cropEntity) {
        return AABBS.get(cropEntity.getCropState().getValue(FACING));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<ICropSpecies, CropState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos, Block block, GTCropBlockEntity cropBlockEntity) {
        BlockEntity neighborEntity = level.getBlockEntity(neighborPos);
        if (neighborEntity instanceof GTCropBlockEntity neighborCropEntity) {
            if (neighborCropEntity.getCropSpecies() instanceof StemCrop neighborStemCrop) {
                // If the dir the attached stem is facing is not attached to the correct StemGrownCrop block
                if (!(neighborStemCrop.getFruit().getPath() == this.fruit.getPath()) && direction == cropBlockEntity.getCropState().getValue(FACING)) {
                    // Revert this AttachedStemCrop to a StemCrop
                    cropBlockEntity.setCropSpecies(this.fruit.getStemSpecies());
                    cropBlockEntity.setAge(cropBlockEntity.getMaxAge());
                }
            }
        }
        return state;
    }
}
