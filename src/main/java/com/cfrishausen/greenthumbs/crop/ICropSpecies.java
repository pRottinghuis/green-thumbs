package com.cfrishausen.greenthumbs.crop;

import com.cfrishausen.greenthumbs.block.custom.GTSimpleCropBlock;
import com.cfrishausen.greenthumbs.genetics.Genome;
import com.cfrishausen.greenthumbs.genetics.genes.Gene;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

/**
 * ICropSpecies defines functionality for making a new crop species. Each crop species contains functionality that is unique to a certain crop.
 * For example Carrots drop something other than wheat does. Maybe some crop species need different conditions to survive ect.
 */
public interface ICropSpecies {

    /**
     *  Add which genes this crop type should have
     */
    Map<String, Gene> defineGenome();

    /**
     *
     * @param pState
     * @return can a crop be placed on specified block?
     */
    default boolean mayPlaceOn(BlockState pState) {
        return pState.is(Blocks.FARMLAND);
    }

    /**
     * Drop items when crop is broken
     * @param quickReplant needs to be a instant replant and harvest
     * @return item required to plant in quick swap. Null if there is no quick swap
     */
    ItemStack drops(ICropEntity crop, Level level, BlockPos pos, boolean quickReplant);

    /**
     * @return Item stack of what crop should drop when broken at all ages and that is meant for re-planting the crop
     */
    ItemStack allAgeDrop(ICropEntity crop);

    /**
     * @return Item stack of what should drop when crop broken at max age
     */
    ItemStack maxAgeDrop(ICropEntity crop);


    /**
     * Override to implement quick replant compatibility
     */
    void quickReplant(BlockState pState, Level pLevel, BlockPos pPos, ICropEntity crop);

    /**
     *
     * @param crop ICropEntity item to copy genome from
     * @param item  What item to make item stack for
     * @return New item stack with genome tag from the crop entity
     */
    default ItemStack getStackWithReplantTag(ICropEntity crop, Item item) {
        ItemStack cropStack = new ItemStack(item, 1);
        CompoundTag infoTag = new CompoundTag();
        cropStack.getOrCreateTag().put(NBTTags.INFO_TAG, infoTag);
        infoTag.put(NBTTags.GENOME_TAG, crop.getGenome().writeTag());
        infoTag.putInt(NBTTags.AGE_TAG, 0);
        return cropStack;
    }

    /**
     * @return is a crop able to survive.
     */
    boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos, Block block);

    /**
     * What does crop need to do on a tick
     */
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, GTSimpleCropBlock block, ICropEntity crop);

    int getMaxAge();
}
