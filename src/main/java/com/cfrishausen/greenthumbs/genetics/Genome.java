package com.cfrishausen.greenthumbs.genetics;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.NBTTags;
import com.cfrishausen.greenthumbs.registries.GTCropSpecies;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class Genome {

    public static final String GROWTH_SPEED = "growth-speed";
    public static final String TEMPERATURE_PREFERENCE = "temperature-preference";

    public static final String MUTATIVITY = "mutativity";

    private final Map<String, String> GENES = new HashMap<>();

    public Genome(Map<String, String> geneMap) {
        this.GENES.putAll(geneMap);
    }

    public Genome(CompoundTag tag) {
       for (String geneName : tag.getAllKeys()) {
           GENES.put(geneName, tag.getString(geneName));
       }
    }

    public CompoundTag writeTag() {
        CompoundTag geneTag = new CompoundTag();
        for (String geneName : GENES.keySet()) {
            geneTag.putString(geneName, GENES.get(geneName));
        }
        return geneTag;
    }

    /**
     * Pick random allele in each gene and assign it a random allele.
     * Simulates reproduction through pollination would have a genome where each gene has one allele from this plant and another random one from another plant.
     */
    public CompoundTag writeReproductionTag(RandomSource random) {
        CompoundTag geneTag = new CompoundTag();
        for (String geneName : GENES.keySet()) {
            char[] alleles = GENES.get(geneName).toCharArray();
            int posForNewAllele = random.nextInt(2);
            if (random.nextBoolean()) {
                alleles[posForNewAllele] = Character.toUpperCase(alleles[posForNewAllele]);
            } else {
                alleles[posForNewAllele] = Character.toLowerCase(alleles[posForNewAllele]);
            }
            geneTag.putString(geneName, String.valueOf(alleles));
        }
        return geneTag;
    }

    public CompoundTag writeCuttingTag(RandomSource random) {
        CompoundTag geneTag = new CompoundTag();
        for (String geneName : GENES.keySet()) {
            String gene = GENES.get(geneName);
            // Probability of a gene mutating
            if (random.nextDouble() < mutationChance()) {
                // Pick allele to mutate
                int alleleForMutation = random.nextInt(2);
                char[] alleles = gene.toCharArray();
                // Swap case of allele for mutation
                if (Character.isUpperCase(alleles[alleleForMutation])) {
                    alleles[alleleForMutation] = Character.toLowerCase(alleles[alleleForMutation]);
                } else {
                    alleles[alleleForMutation] = Character.toUpperCase(alleles[alleleForMutation]);
                }
                geneTag.putString(geneName, String.valueOf(alleles));
            } else {
                geneTag.putString(geneName, GENES.get(geneName));
            }
        }
        return geneTag;
    }

    public static CompoundTag fullSpliceTag(CompoundTag seed1tag, CompoundTag seed2tag) {
        CompoundTag splicedTag = new CompoundTag();
        if (seed1tag.contains(NBTTags.INFO_TAG) && seed2tag.contains(NBTTags.INFO_TAG)) {
            if (seedsFromSameGenome(seed1tag, seed2tag)) {
                // Splice seed 2 genome onto seed 1 genome
                CompoundTag seed1genomeTag = seed1tag.getCompound(NBTTags.INFO_TAG).getCompound(NBTTags.GENOME_TAG);
                CompoundTag seed2genomeTag = seed2tag.getCompound(NBTTags.INFO_TAG).getCompound(NBTTags.GENOME_TAG);
                seed1genomeTag.getAllKeys().forEach((geneName) -> {
                    String splicedAlleles = seed1genomeTag.getString(geneName).substring(0, 1) + seed2genomeTag.getString(geneName).substring(1, 2);
                    seed1genomeTag.putString(geneName, splicedAlleles);
                });
                // set return tag to copy of seed 1 tag after genome has been spliced on to it
                splicedTag = seed1tag;
            }
        } else {
            GreenThumbs.LOGGER.warn("Tried splicing seeds with incomplete tags");
        }
        return splicedTag;
    }

    private static boolean seedsFromSameGenome(CompoundTag seed1tag, CompoundTag seed2tag) {
        ICropSpecies species1 = GTCropSpecies.getSpecies(new ResourceLocation(seed1tag.getCompound(NBTTags.INFO_TAG).getString(NBTTags.CROP_SPECIES_TAG)));
        ICropSpecies species2 = GTCropSpecies.getSpecies(new ResourceLocation(seed2tag.getCompound(NBTTags.INFO_TAG).getString(NBTTags.CROP_SPECIES_TAG)));
        return species1.defineGenome().equals(species2.defineGenome());
    }

    public Map<String, String> getGenes() {
        return GENES;
    }

    private boolean isRecessive(String alleles) {
        Character allele1 = alleles.charAt(0);
        Character allele2 = alleles.charAt(1);
        return Character.isLowerCase(allele1) && Character.isLowerCase(allele2);
    }

    public float getGrowthSpeed(Block pBlock, BlockGetter pLevel, BlockPos pPos) {
        float f = 1.0F;
        BlockPos blockpos = pPos.below();

        // Add an increase in growth rate when adjacent block poses can support a plant or are fertile (vanilla: in range of water)
        for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
                float f1 = 0.0F;
                BlockState blockstate = pLevel.getBlockState(blockpos.offset(i, 0, j));
                if (blockstate.canSustainPlant(pLevel, blockpos.offset(i, 0, j), net.minecraft.core.Direction.UP, (net.minecraftforge.common.IPlantable) pBlock)) {
                    f1 = 1.0F;
                    if (blockstate.isFertile(pLevel, pPos.offset(i, 0, j))) {
                        f1 = 3.0F;
                    }
                }

                // Increase in growth speed is less from blockpos bonuses adjacent to the crop
                if (i != 0 || j != 0) {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        BlockPos blockpos1 = pPos.north();
        BlockPos blockpos2 = pPos.south();
        BlockPos blockpos3 = pPos.west();
        BlockPos blockpos4 = pPos.east();
        boolean flag = pLevel.getBlockState(blockpos3).is(pBlock) || pLevel.getBlockState(blockpos4).is(pBlock);
        boolean flag1 = pLevel.getBlockState(blockpos1).is(pBlock) || pLevel.getBlockState(blockpos2).is(pBlock);
        // Reduce if crop growth rate if surrounded by same crop in all directions
        if (flag && flag1) {
            f /= 2.0F;
        } else {
            // If next to one other crop of same type reduce growth rate
            boolean flag2 = pLevel.getBlockState(blockpos3.north()).is(pBlock) || pLevel.getBlockState(blockpos4.north()).is(pBlock) || pLevel.getBlockState(blockpos4.south()).is(pBlock) || pLevel.getBlockState(blockpos3.south()).is(pBlock);
            if (flag2) {
                f /= 2.0F;
            }
        }

        // Homozygous recessive gives growth rate boost
        if (GENES.containsKey(GROWTH_SPEED)) {
            if (isRecessive(GENES.get(GROWTH_SPEED))) {
                f *= 4;
            }
        }

        return f;
    }

    public double mutationChance() {
        // % how likely is to mutate
        double mutationChance = 0.7;
        if (isRecessive(GENES.get(MUTATIVITY))) {
            // mutation chance is reduced by __ % when recessive trait is expressed
            mutationChance *= 0.5;
        }
        return mutationChance;
    }

    /**
     * Compare gene entries. Same entries means same genome
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Genome other) {
            return this.getGenes().equals(other.getGenes());
        }
        return false;
    }
}
