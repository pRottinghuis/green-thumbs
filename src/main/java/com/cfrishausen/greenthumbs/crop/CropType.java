package com.cfrishausen.greenthumbs.crop;

import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.genetics.Genome;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import org.jetbrains.annotations.Nullable;

public interface CropType extends IPlantable, EntityBlock, GTGrowable {

    VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

    @Nullable
    @Override
    default BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GTCropBlockEntity(pos, state);
    }

    @Override
    default PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.CROP;
    }

    /**
     *
     * @param pState
     * @return can a crop be placed on another block
     */
    default boolean mayPlaceOn(BlockState pState) {
        return pState.is(Blocks.FARMLAND);
    }

    /**
     * Drop items when crop is broken
     * @param quickReplant needs to be a instant replant and harvest
     * @return item required to plant in quick swap. Null if there is no quick swap
     */
    ItemStack drops(GTCropBlockEntity cropEntity, Level level, BlockPos pos, boolean quickReplant);

    /**
     * @return Item stack of what crop should drop when broken at all ages and that is meant for re-planting the crop
     */
    ItemStack allAgeDrop(GTCropBlockEntity cropEntity);

    /**
     * @return Item stack of what should drop when crop broken at max age
     */
    ItemStack maxAgeDrop(GTCropBlockEntity cropEntity);


    /**
     * Override to implement quick replant compatibility
     */
    void quickReplant(BlockState pState, Level pLevel, BlockPos pPos, GTCropBlockEntity cropEntity);

    /**
     *
     * @param cropEntity Entity to copy genome from
     * @param item  What item to make item stack for
     * @return New item stack with genome tag from the crop entity
     */
    default ItemStack getStackWithGenomeTag(GTCropBlockEntity cropEntity, Item item) {
        ItemStack cropStack = new ItemStack(item, 1);
        CompoundTag infoTag = new CompoundTag();
        cropStack.getOrCreateTag().put(GTCropBlockEntity.INFO_TAG, infoTag);
        infoTag.put(Genome.GENOME_TAG, cropEntity.getGenome().writeTag());
        infoTag.putInt(GTCropBlockEntity.AGE_TAG, 0);
        return cropStack;
    }
}
