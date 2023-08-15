package com.cfrishausen.greenthumbs.crop;

import com.cfrishausen.greenthumbs.crop.state.CropState;
import com.cfrishausen.greenthumbs.genetics.Genome;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

/**
 * Defines what Crop entity requires. Crop entity is functionality that is shared across all crop species
 */
public interface ICropEntity {

    Genome getGenome();

    void setGenome(Genome genome);

    boolean isMaxAge();

    int getAge();

    void setAge(int age);

    int getMaxAge();

    ICropSpecies getCropSpecies();

    /**
     *  Update fields from nbt
     */
    void load(CompoundTag nbt);

    CropState getCropState();

    void setCropState(CropState cropState);

    void setCropSpecies(ICropSpecies cropSpecies);

    void refreshCropState();

    void growCrops(Level pLevel);

    void markUpdated();
}
