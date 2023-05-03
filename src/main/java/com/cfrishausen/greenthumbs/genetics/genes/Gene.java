package com.cfrishausen.greenthumbs.genetics.genes;

import com.cfrishausen.greenthumbs.genetics.AllelePair;
import net.minecraft.util.RandomSource;

public class Gene {

    protected AllelePair allelePair;

    public Gene(char symbol, RandomSource random) {
        this.allelePair = new AllelePair(symbol, random);
    }

    public Gene(String alleles) {
        this.allelePair = new AllelePair(alleles);
    }

    public void setAllelePair(String allelePair) {
        this.allelePair = new AllelePair(allelePair);
    }

    @Override
    public String toString() {
        return allelePair.toString();
    }
}
