package com.cfrishausen.greenthumbs.data;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class GTBlockModels extends BlockModelProvider {
    public GTBlockModels(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.withExistingParent(GTBlocks.SEED_SPLICING_STATION.getId().getPath(), "cube")
                .texture("particle", modLoc("block/seed_splicing_station_north"))
                .texture("down", modLoc("block/seed_splicing_station_down"))
                .texture("up", modLoc("block/seed_splicing_station_up"))
                .texture("north", modLoc("block/seed_splicing_station_north"))
                .texture("south", modLoc("block/seed_splicing_station_south"))
                .texture("east", modLoc("block/seed_splicing_station_east"))
                .texture("west", modLoc("block/seed_splicing_station_west"));
    }
}
