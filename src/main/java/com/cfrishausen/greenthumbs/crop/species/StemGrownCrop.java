package com.cfrishausen.greenthumbs.crop.species;

import com.cfrishausen.greenthumbs.block.entity.GTCropBlockEntity;
import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.state.CropState;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class StemGrownCrop extends BasicCrop{

    private Supplier<ICropSpecies> stemSpecies;
    private Supplier<ICropSpecies> attachedStemSpecies;

    public StemGrownCrop(String pathName, GTGenomeCropBlockItem seed, Item crop, GTGenomeCropBlockItem cutting, Supplier<ICropSpecies> stemSpecies, Supplier<ICropSpecies> attachedStemSpecies) {
        super(pathName, seed, crop, cutting);
        this.stemSpecies = stemSpecies;
        this.attachedStemSpecies = attachedStemSpecies;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, ICropEntity cropEntity) {
        return Shapes.block();
    }

    public StemCrop getStemSpecies() {
        return ((StemCrop) this.stemSpecies.get());
    }

    public AttachedStemCrop getAttachedStemSpecies() {
        return ((AttachedStemCrop) this.attachedStemSpecies.get());
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

    @Override
    public boolean canTakeCutting(ICropEntity cropEntity) {
        return false;
    }
}
