package com.cfrishausen.greenthumbs.data;

import com.cfrishausen.greenthumbs.registries.GTItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class GTItemModels extends ItemModelProvider {
    public GTItemModels(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.basicItem(GTItems.GT_DEBUG_STICK.get());
        this.basicItem(GTItems.CARROT_SEEDS.get());
        this.basicItem(GTItems.WHEAT_SEEDS.get());
        this.basicItem(GTItems.POTATO_SEEDS.get());
        this.basicItem(GTItems.BEETROOT_SEEDS.get());
    }
}
