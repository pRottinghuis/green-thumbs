package com.cfrishausen.greenthumbs.block.custom;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class GTSeedCropBlock extends GTSimpleCropBlock{

    private final Supplier<GTGenomeBlockItem> SEED;


    public GTSeedCropBlock(Properties pProperties, Supplier<GTGenomeBlockItem> crop, Supplier<GTGenomeBlockItem> seed) {
        super(pProperties, crop);
        SEED = seed;
    }

    @Override
    public ItemStack drops(GTCropBlockEntity cropEntity, Level level, BlockPos pos, boolean quickReplant) {
        SimpleContainer drops = new SimpleContainer(2);
        // default item that all crops drop regardless of age
        ItemStack allAgeDropStack = allAgeDrop(cropEntity);
        ItemStack cropReplantStack = null;
        if (quickReplant) {
            // Set crop for return and reduce drop stack
            cropReplantStack = allAgeDropStack.copy();
            // Take one item out of the drop stack and return it for a quickReplant
            cropReplantStack.setCount(1);
            allAgeDropStack.shrink(1);
        }
        drops.setItem(0, allAgeDropStack);
        // Add crop max age drop
        if (cropEntity.isMaxAge()) {
            drops.setItem(1, maxAgeDrop(cropEntity));
        }
        Containers.dropContents(level, pos, drops);
        return cropReplantStack;
    }

    @Override
    public void quickReplant(BlockState pState, Level pLevel, BlockPos pPos, GTCropBlockEntity cropEntity) {
        ItemStack seed = drops(cropEntity, pLevel, pPos, true);
        if (seed != null) {
            CompoundTag seedTag = seed.getTag();
            if (seedTag != null && seedTag.contains(GreenThumbs.ID + ".Genome")) {
                seedTag.putInt(GreenThumbs.ID + ".Age", 0);
                cropEntity.load(seedTag);
                // sendBlockUpdated will cause new baked model to be created for crop at age 0
                pLevel.sendBlockUpdated(pPos, pState, pState, Block.UPDATE_ALL);
            }
        }
    }
}
