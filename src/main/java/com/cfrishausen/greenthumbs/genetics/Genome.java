package com.cfrishausen.greenthumbs.genetics;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Genome {

    Gene cropYield;

    // Constructor field initialization must be in the same order

    public Genome(String genomeString) {
        this.cropYield = new Gene("crop_yield", genomeString);
    }

    public Genome(RandomSource random) {
        this.cropYield = new Gene("crop_yield", 'C', random);
    }

    public Genome() {
        this("Cc");
    }

    @Override
    public String toString() {
        String genomeString = "";
        // Order must match constructor field initialization
        genomeString += (cropYield.toString());
        return genomeString;
    }
}
