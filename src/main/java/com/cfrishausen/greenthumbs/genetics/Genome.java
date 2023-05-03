package com.cfrishausen.greenthumbs.genetics;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.genetics.genes.Gene;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class Genome {

    public static final String GENOME_TAG = GreenThumbs.ID + ".Genome";
    public static final String GROWTH_SPEED = "growth-speed";

    public static final Map<String, Gene> GENES = new HashMap<>();

    public void addGene(String name, Gene gene) {
        GENES.put(name, gene);
    }

    public void setGenomeFromTag(CompoundTag tag) {
        for (String geneName : GENES.keySet()) {
            if (tag.contains(geneName)) {
                // Update gene alleles with string from tag
                GENES.get(geneName).setAllelePair(tag.getString(geneName));
            }
        }
    }

    public CompoundTag writeTag() {
        CompoundTag geneTag = new CompoundTag();
        for (String geneName : GENES.keySet()) {
            geneTag.putString(geneName, GENES.get(geneName).toString());
        }
        return geneTag;
    }

    public Gene getGene(String geneName) {
        return this.GENES.get(geneName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Genome otherGenome) {
            return this.writeTag().equals(otherGenome.writeTag());
        }
        return false;
    }
}
