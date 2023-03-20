package com.cfrishausen.greenthumbs.genetics;

import net.minecraft.util.RandomSource;

public class AllelePair implements GTAllelePairType{

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

    @Override
    public String toString() {
        return "" + this.allele0 + this.allele1;
    }

    @Override
    public boolean hasDominant() {
        return Character.isUpperCase(this.allele0) || Character.isUpperCase(allele1);
    }
}
