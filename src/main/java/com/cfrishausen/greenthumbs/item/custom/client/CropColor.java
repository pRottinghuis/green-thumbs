package com.cfrishausen.greenthumbs.item.custom.client;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.registries.GTCropSpecies;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class CropColor implements BlockColor {
    @Override
    public int getColor(BlockState pState, @Nullable BlockAndTintGetter pLevel, @Nullable BlockPos pPos, int pTintIndex) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof GTCropBlockEntity cropBlockEntity) {
            ICropSpecies cropSpecies = cropBlockEntity.getCropSpecies();
            if (cropSpecies != null) {
                return getColorFromCrop(cropBlockEntity, cropSpecies);
            } else {
                GreenThumbs.LOGGER.warn("Trying to get block color for null species");
            }
        }

        return 0;
    }

    private int getColorFromCrop(ICropEntity cropEntity, ICropSpecies cropSpecies) {
        if (sameSpecies(cropSpecies, GTCropSpecies.GT_ATTACHED_PUMPKIN_STEM) || sameSpecies(cropSpecies, GTCropSpecies.GT_ATTACHED_MELON_STEM)) {
            return 14731036;
        }
        if (sameSpecies(cropSpecies, GTCropSpecies.GT_PUMPKIN_STEM) || sameSpecies(cropSpecies, GTCropSpecies.GT_MELON_STEM)) {
            int i = cropEntity.getCropSpecies().getAge(cropEntity);
            int j = i * 32;
            int k = 255 - i * 8;
            int l = i * 4;
            return j << 16 | k << 8 | l;
        }
        return 0;
    }

    private boolean sameSpecies(ICropSpecies cropSpecies, RegistryObject<ICropSpecies> cropSpeciesRegistryObject) {
        return GTCropSpecies.CROP_SPECIES_REGISTRY.get().getKey(cropSpecies).equals(cropSpeciesRegistryObject.getId());
    }
}
