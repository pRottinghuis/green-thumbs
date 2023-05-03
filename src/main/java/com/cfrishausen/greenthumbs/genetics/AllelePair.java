package com.cfrishausen.greenthumbs.genetics;

import net.minecraft.util.RandomSource;

public class AllelePair {

    private char allele0;
    private char allele1;

    private AllelePair(char allele0, char allele1) {
        this.allele0 = allele0;
        this.allele1 = allele1;
    }

    public AllelePair(char symbol, RandomSource random) {
        this(random.nextBoolean() ? Character.toLowerCase(symbol) : Character.toUpperCase(symbol), random.nextBoolean() ? Character.toLowerCase(symbol) : Character.toUpperCase(symbol));
    }

    public AllelePair(String allelePairStr) {
        this(allelePairStr.charAt(0), allelePairStr.charAt(1));
    }

    public void randomizeAlleles(RandomSource randomSource) {
        allele0 = randomSource.nextBoolean() ? Character.toUpperCase(allele0) : Character.toLowerCase(allele0);
        allele1 = randomSource.nextBoolean() ? Character.toUpperCase(allele1) : Character.toLowerCase(allele1);
    }

    @Override
    public String toString() {
        return "" + this.allele0 + this.allele1;
    }

    public boolean hasDominant() {
        return Character.isUpperCase(this.allele0) || Character.isUpperCase(allele1);
    }
}
