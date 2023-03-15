package com.cfrishausen.greenthumbs.genetics;

import net.minecraft.util.RandomSource;

import java.util.*;

public class Gene {

    private final String name;
    private final char[] alleles = new char[2];

    private Gene(String name, char allele0, char allele1) {
        this.name = name;
        this.alleles[0] = allele0;
        this.alleles[1] = allele1;
    }

    public Gene(String name, char symbol, RandomSource random) {
        this(name, random.nextBoolean() ? Character.toLowerCase(symbol) : Character.toUpperCase(symbol), random.nextBoolean() ? Character.toLowerCase(symbol) : Character.toUpperCase(symbol));
    }

    public Gene(String name, String allelePair) {
        this(name, allelePair.charAt(0), allelePair.charAt(1));
    }

    @Override
    public String toString() {
        if (this.alleles != null) {
            return "" + this.alleles[0] + this.alleles[1];
        } else {
            return "alleles[] is null";
        }

    }
}
