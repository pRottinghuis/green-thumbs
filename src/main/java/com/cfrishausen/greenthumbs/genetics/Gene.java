package com.cfrishausen.greenthumbs.genetics;

import net.minecraft.util.RandomSource;

import java.util.*;

public class Gene {

    private String name;
    private String[] alleles;

    private Gene(String name, String allele0, String allele1) {
        this.name = name;
        this.alleles = new String[] {allele0, allele1};
    }

    public Gene(String name, String symbol, RandomSource random) {
        new Gene(name, random.nextBoolean() ? symbol.toLowerCase() : symbol.toUpperCase(), random.nextBoolean() ? symbol.toLowerCase() : symbol.toUpperCase());
    }

    public Gene(String name, String allelePair) {
        new Gene(name, allelePair.substring(0, 1), allelePair.substring(1, 2));
    }

    @Override
    public String toString() {
        return this.alleles[0] + this.alleles[1];
    }
}
