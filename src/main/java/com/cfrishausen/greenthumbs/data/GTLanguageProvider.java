package com.cfrishausen.greenthumbs.data;

import com.cfrishausen.greenthumbs.genetics.Genome;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import com.cfrishausen.greenthumbs.registries.GTItems;
import net.minecraft.Util;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class GTLanguageProvider extends LanguageProvider {
    public GTLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        this.addItem(GTItems.GT_DEBUG_STICK, "Green Thumbs Debug Stick");
        this.addItem(GTItems.SEED_SPLICING_STATION, "Seed Splicing Station");

        this.add(GTItems.WHEAT_SEEDS.get(), "Wheat Seeds");
        this.add(GTItems.CARROT_SEEDS.get(), "Carrot Seeds");
        this.add(GTItems.POTATO_SEEDS.get(), "Potato Seeds");
        this.add(GTItems.BEETROOT_SEEDS.get(), "Beetroot Seeds");
        this.add(GTItems.SWEET_BERRY_SEEDS.get(), "Sweet Berry Seeds");
        this.add(GTItems.PUMPKIN_SEEDS.get(), "Pumpkin Seeds");
        this.add(GTItems.MELON_SEEDS.get(), "Melon Seeds");

        this.add(GTItems.WHEAT_CUTTING.get(), "Wheat Cutting");
        this.add(GTItems.CARROT_CUTTING.get(), "Carrot Cutting");
        this.add(GTItems.POTATO_CUTTING.get(), "Potato Cutting");
        this.add(GTItems.BEETROOT_CUTTING.get(), "Beetroot Cutting");
        this.add(GTItems.SWEET_BERRY_CUTTING.get(), "Sweet Berry Cutting");
        this.add(GTItems.PUMPKIN_CUTTING.get(), "Pumpkin Cutting");
        this.add(GTItems.MELON_CUTTING.get(), "Melon Cutting");


        this.add(GTBlocks.GT_CROP_BLOCK.get(), "Crop Block");
        this.add(GTBlocks.GT_VEGETABLE_BLOCK.get(), "Vegetable Block");


        this.add(Genome.MUTATIVITY, "Mutativity");
        this.add(Genome.TEMPERATURE_PREFERENCE, "Temperature Preference");
        this.add(Genome.GROWTH_SPEED, "Growth Speed");
        this.add(Genome.CROP_YIELD, "Crop Yield");



    }
}
