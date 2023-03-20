package com.cfrishausen.greenthumbs.crop;

import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RootCrop implements CropType{

    @Override
    public ItemStack allAgeDrop(GTCropBlockEntity cropEntity) {
        return getStackWithGenomeTag(cropEntity, this.CROP.get());
    }

    @Override
    public ItemStack maxAgeDrop(GTCropBlockEntity cropEntity) {
        return allAgeDrop(cropEntity);
    }

    @Override
    public void quickReplant(BlockState pState, Level pLevel, BlockPos pPos, GTCropBlockEntity cropEntity) {
        ItemStack replantItem = drops(cropEntity, pLevel, pPos, true);
        if (replantItem != null) {
            CompoundTag infoTag = replantItem.getTag();
            if (infoTag != null && infoTag.contains(GTCropBlockEntity.INFO_TAG)) {
                infoTag.putInt(GTCropBlockEntity.AGE_TAG, 0);
                cropEntity.load(infoTag);
                // sendBlockUpdated will cause new baked model to be created for crop at age 0
                pLevel.sendBlockUpdated(pPos, pState, pState, Block.UPDATE_ALL);
            }
        }
    }

    // TODO add fortune drops
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

}
