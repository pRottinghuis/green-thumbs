package com.cfrishausen.greenthumbs.crop;

import com.cfrishausen.greenthumbs.genetics.Genome;
import net.minecraft.nbt.CompoundTag;

/**
 * Defines what Crop entity requires. Crop entity is functionality that is shared across all crop species
 */
public interface ICropEntity {

    Genome getGenome();

    boolean isMaxAge();

    int getAge();

    void setAge(int age);

    int getMaxAge();

    ICropSpecies getCropSpecies();

    /**
     *  Update fields from nbt
     */
    void load(CompoundTag nbt);
}
