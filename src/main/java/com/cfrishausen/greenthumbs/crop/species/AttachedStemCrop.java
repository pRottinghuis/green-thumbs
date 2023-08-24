package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.state.CropState;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

public class AttachedStemCrop extends BasicCrop{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.SOUTH, Block.box(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 16.0D), Direction.WEST, Block.box(0.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D), Direction.NORTH, Block.box(6.0D, 0.0D, 0.0D, 10.0D, 10.0D, 10.0D), Direction.EAST, Block.box(6.0D, 0.0D, 6.0D, 16.0D, 10.0D, 10.0D)));
    private final Supplier<ICropSpecies> fruit;



    public AttachedStemCrop(String name, GTGenomeCropBlockItem seed, Item crop, GTGenomeCropBlockItem cutting, Supplier<ICropSpecies> fruit) {
        super(name, seed, crop, cutting);
        this.fruit = fruit;
    }

    @Override
    public void registerDefaultState() {
        this.defaultCropState = this.cropStateDef.any().setValue(this.FACING, Direction.NORTH);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, ICropEntity cropEntity) {
        return AABBS.get(cropEntity.getCropState().getValue(this.FACING));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<ICropSpecies, CropState> builder) {
        builder.add(FACING);
    }

    @Override
    public Map<CropState, ModelResourceLocation> getModelMap() {
        Map<CropState, ModelResourceLocation> modelMap = new HashMap<>();
        Direction.Plane.HORIZONTAL.stream().forEach(dir -> {
            modelMap.put(defaultCropState.setValue(FACING, dir), ModelResourceLocation.vanilla(pathName, "facing=" + dir.getName()));
        });

        return modelMap;
    }


    // TODO check if this should be on StemGrownCrop instead
    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos, Block block, GTCropBlockEntity cropBlockEntity) {
        BlockEntity neighborEntity = level.getBlockEntity(facingPos);
        // only check in direction the block is facing
        if (facing == cropBlockEntity.getCropState().getValue(FACING)) {
            // is neighbor a crop entity
            if (neighborEntity instanceof GTCropBlockEntity neighborCropEntity) {
                // is neighbor a StemGrownCropBlock
                if (neighborCropEntity.getCropSpecies() instanceof StemGrownCrop neighborStemCrop) {
                    // is the StemGrownBlock the correct fruit
                    if (neighborStemCrop.getPath() == this.fruit.get().getPath()) {
                        return state;
                    }
                }
            }
            // Revert this AttachedStemCrop to a StemCrop
            cropBlockEntity.setCropSpecies(((StemGrownCrop) this.fruit.get()).getStemSpecies());
            setAge(cropBlockEntity, cropBlockEntity.getCropSpecies().getMaxAge());
        }

        return state;
    }
}
