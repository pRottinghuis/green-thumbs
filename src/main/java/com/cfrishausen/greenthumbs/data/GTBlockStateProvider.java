package com.cfrishausen.greenthumbs.data;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.block.custom.SeedSplicingStationBlock;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import com.cfrishausen.greenthumbs.registries.GTItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class GTBlockStateProvider extends BlockStateProvider {
    public GTBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.getVariantBuilder(GTBlocks.SEED_SPLICING_STATION.get()).forAllStates(state -> {
            return ConfiguredModel.builder()
                    .modelFile(this.models().getExistingFile(GTBlocks.SEED_SPLICING_STATION.getId()))
                    .rotationY((int) (state.getValue(SeedSplicingStationBlock.FACING).toYRot()))
                    .build();
        });

        simpleBlockItem(GTBlocks.SEED_SPLICING_STATION.get(), models().getExistingFile(GTBlocks.SEED_SPLICING_STATION.getId()));
    }
}
