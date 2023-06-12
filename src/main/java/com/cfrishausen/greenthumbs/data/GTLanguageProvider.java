package com.cfrishausen.greenthumbs.data;

import com.cfrishausen.greenthumbs.genetics.Genome;
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
        this.add(GTItems.GT_DEBUG_STICK.get().getDescriptionId(), "Green Thumbs Debug Stick");

        this.add(GTItems.WHEAT_SEEDS.get(), "Wheat Seeds");
        this.add(GTItems.CARROT_SEEDS.get(), "Carrot Seeds");
        this.add(GTItems.POTATO_SEEDS.get(), "Potato Seeds");
        this.add(GTItems.BEETROOT_SEEDS.get(), "Beetroot Seeds");

        this.add(GTItems.WHEAT_CUTTING.get(), "Wheat Cutting");
        this.add(GTItems.CARROT_CUTTING.get(), "Carrot Cutting");
        this.add(GTItems.POTATO_CUTTING.get(), "Potato Cutting");
        this.add(GTItems.BEETROOT_CUTTING.get(), "Beetroot Cutting");

        this.add(Genome.MUTATIVITY, "Mutativity");
        this.add(Genome.TEMPERATURE_PREFERENCE, "Temperature Preference");
        this.add(Genome.GROWTH_SPEED, "Growth Speed");

    }
}
