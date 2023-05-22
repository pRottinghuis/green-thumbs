package com.cfrishausen.greenthumbs.client;

import com.cfrishausen.greenthumbs.client.model.block.GTBakedModel;
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
        Map<ResourceLocation, BakedModel> eventModels = event.getModels();

        // This baked model stores all crop type models and corresponding age models
        GTBakedModel gtBakedModel = new GTBakedModel();

        copyCropBaked(gtBakedModel, eventModels, GTCropSpecies.GT_CARROT.get(), Blocks.CARROTS);
        copyCropBaked(gtBakedModel, eventModels, GTCropSpecies.GT_WHEAT.get(), Blocks.WHEAT);
        //copyCropBaked(gtBakedModel, eventModels, GTCropSpecies.GT_BEETROOT.get(), Blocks.BEETROOTS);
        copyCropBaked(gtBakedModel, eventModels, GTCropSpecies.GT_POTATO.get(), Blocks.POTATOES);
    }

    /**
     * Helper to copy all ages of a existing minecraft model into ICropSpecies models
     * @param gtBakedModel IDynamicBakedModel that holds all the model info for Green Thumbs crops
     * @param eventModels Registry of existing models
     * @param cropSpecies What species of crop these models need to be made for
     * @param block What Minecraft block to copy models over from
     */
    public static void copyCropBaked(GTBakedModel gtBakedModel, Map<ResourceLocation, BakedModel> eventModels, ICropSpecies cropSpecies, Block block) {
        ArrayList<BakedModel> bakedModels = new ArrayList<>();
        // Get baked model for each age of crop
        for (int age = 0; age <= 7; age++) {
            ResourceLocation location = BlockModelShaper.stateToModelLocation(block.defaultBlockState().setValue(BlockStateProperties.AGE_7, age));
            bakedModels.add(eventModels.get(location));
        }
        //
        gtBakedModel.addModels(GTCropSpecies.CROP_SPECIES_REGISTRY.get().getKey(cropSpecies).toString(), bakedModels.toArray(new BakedModel[0]));

        // Replace/Update our model in register of baked models
        eventModels.put(BlockModelShaper.stateToModelLocation(GTBlocks.GT_CROP_BLOCK.get().defaultBlockState()), gtBakedModel);
    }
}
