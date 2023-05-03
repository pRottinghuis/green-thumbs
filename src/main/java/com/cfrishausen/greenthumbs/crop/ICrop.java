package com.cfrishausen.greenthumbs.crop;

import com.cfrishausen.greenthumbs.genetics.Genome;
import net.minecraft.nbt.CompoundTag;

/**
 * Defines what Crop entity requires
 */
public interface ICrop {

    Genome getGenome();

    boolean isMaxAge();

    int getAge();

    void setAge(int age);

    int getMaxAge();

    /**
     *  Update fields from nbt
     */
    void load(CompoundTag nbt);
}
