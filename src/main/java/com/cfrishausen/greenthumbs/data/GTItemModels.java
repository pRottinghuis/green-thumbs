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
        this.basicItem(GTItems.SWEET_BERRY_SEEDS.get());
        this.basicItem(GTItems.PUMPKIN_SEEDS.get());
        this.basicItem(GTItems.MELON_SEEDS.get());

        this.basicItem(GTItems.CARROT_CUTTING.get());
        this.basicItem(GTItems.WHEAT_CUTTING.get());
        this.basicItem(GTItems.POTATO_CUTTING.get());
        this.basicItem(GTItems.BEETROOT_CUTTING.get());
        this.basicItem(GTItems.SWEET_BERRY_CUTTING.get());
        // pumpkin cutting
        // melon cutting

    }
}
