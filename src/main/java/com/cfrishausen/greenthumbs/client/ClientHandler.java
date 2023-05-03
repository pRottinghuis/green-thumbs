package com.cfrishausen.greenthumbs.client;

import com.cfrishausen.greenthumbs.client.model.block.GTWheatBakedModel;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import com.cfrishausen.greenthumbs.registries.GTCropSpecies;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
import java.util.Map;

public class ClientHandler {
    public static void registerEvents() {
        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        mod.addListener(ClientHandler::clientSetup);
        mod.addListener(ClientHandler::registerBakedModels);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(GTBlocks.GT_CROP_BLOCK.get(), RenderType.cutout());
        });
    }

    // Event used to copy baked models from minecraft to this mod
    public static void registerBakedModels(ModelEvent.ModifyBakingResult event) {
        Map<ResourceLocation, BakedModel> models = event.getModels();
        ArrayList<BakedModel> bakedModels = new ArrayList<>();
        copyCropBaked(models, bakedModels, GTCropSpecies.GT_CARROTS.get(), Blocks.CARROTS);
        copyCropBaked(models, bakedModels, GTCropSpecies.GT_WHEAT.get(), Blocks.WHEAT);
    }

    /**
     * Helper to copy all 7 age models from minecraft block to green thumbs block
     * @param cropSpecies What crop species needs to receive model.
     * @param block What minecraft crop needs to copy over.
     */
    private static void copyCropBaked(Map<ResourceLocation, BakedModel> models, ArrayList<BakedModel> bakedModels, ICropSpecies cropSpecies, Block block) {
        // Get baked model for each age of crop
        for (int age = 0; age <= 7; age++) {
            ResourceLocation location = BlockModelShaper.stateToModelLocation(block.defaultBlockState().setValue(BlockStateProperties.AGE_7, age));
            bakedModels.add(models.get(location));
        }
        // Create custom baked model class with each age baked model collected tied to a crop type name
        BakedModel bakedModel = new GTWheatBakedModel(GTCropSpecies.CROP_SPECIES_REGISTRY.get().getKey(cropSpecies).toString(), bakedModels.toArray(new BakedModel[0]));
        // Replace our model in register of baked models
        models.put(BlockModelShaper.stateToModelLocation(GTBlocks.GT_CROP_BLOCK.get().defaultBlockState()), bakedModel);
    }
}
