package com.cfrishausen.greenthumbs.genetics;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.genetics.genes.Gene;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.concurrent.Computable;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;

import java.util.HashMap;
import java.util.Map;

public class Genome {

    public static final String GENOME_TAG = GreenThumbs.ID + ".Genome";
    public static final String GROWTH_SPEED = "growth-speed";

    private Map<String, Gene> genes;

    // Constructor field initialization must be in the same order

    public Genome(CompoundTag tag) {
        initializeFromTag(tag);
    }

    public Genome(RandomSource random) {
        genes.put(GROWTH_SPEED, new Gene('G', random));
    }

    private void initializeFromTag(CompoundTag tag) {
        if (tag != null && tag.contains(GENOME_TAG)) {
            // add all genes to initialize
            CompoundTag geneTag = tag.getCompound(GENOME_TAG);
            setGeneFromTag(geneTag, GROWTH_SPEED);
            return;
        }
        GreenThumbs.LOGGER.error("Tried initializing Genome from null tag or tag without genome data.");
    }

    private void setGeneFromTag(CompoundTag tag, String geneName) {
        String tagName = GENOME_TAG + "." + geneName;
        if (tag.contains(tagName)) {
            genes.put(geneName, new Gene(tag.getString(tagName)));
        } else {
            GreenThumbs.LOGGER.error(geneName + " missing from gene tag");
        }
    }

    public CompoundTag writeTag() {
        CompoundTag geneTag = new CompoundTag();
        String tagName;
        String tagGene;
        for (String geneName : genes.keySet()) {
            tagName = GENOME_TAG + "." + geneName;
            tagGene = genes.get(geneName).toString();
            geneTag.putString(tagName, tagGene);
        }
        return geneTag;
    }

    public Gene getGene(String geneName) {
        return this.genes.get(geneName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Genome otherGenome) {
            return this.writeTag().equals(otherGenome.writeTag());
        }
        return false;
    }
}
