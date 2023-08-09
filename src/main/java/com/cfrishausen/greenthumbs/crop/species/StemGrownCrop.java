package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.state.CropState;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class StemGrownCrop extends BasicCrop{

    StemCrop stemSpecies;
    private AttachedStemCrop attachedStemSpecies;

    public StemGrownCrop(String pathName, GTGenomeCropBlockItem seed, Item crop, GTGenomeCropBlockItem cutting, StemCrop stemSpecies, AttachedStemCrop attachedStemSpecies) {
        super(pathName, seed, crop, cutting);
        this.stemSpecies = stemSpecies;
        this.attachedStemSpecies = attachedStemSpecies;
    }

    public StemCrop getStemSpecies() {
        return this.stemSpecies;
    }

    public AttachedStemCrop getAttachedStemSpecies() {
        return this.attachedStemSpecies;
    }

    // Can be placed on any block
    @Override
    public boolean mayPlaceOn(BlockState pState) {
        return true;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean p50900, GTCropBlockEntity cropEntity) {
        return false;
    }

    @Override
    public Map<CropState, ModelResourceLocation> getModelMap() {
        Map<CropState, ModelResourceLocation> modelMap = new HashMap<>();
        for (int age = 0; age <= getMaxAge(); age++) {
            modelMap.put(defaultCropState.setValue(AGE, age), ModelResourceLocation.vanilla(pathName, ""));
        }
        return modelMap;
    }
}
