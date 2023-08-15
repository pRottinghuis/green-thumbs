package com.cfrishausen.greenthumbs.client;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.client.model.block.GTBakedModel;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.state.CropState;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import com.cfrishausen.greenthumbs.registries.GTCropSpecies;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientHandler {
    public static void registerEvents() {
        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        mod.addListener(ClientHandler::clientSetup);
        mod.addListener(ClientHandler::registerBakedModels);
        mod.addListener(ClientHandler::registerColorHandlersEvent);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(GTBlocks.GT_CROP_BLOCK.get(), RenderType.cutout());
        });
    }

    // Event used to copy baked models from minecraft to this mod
    public static void registerBakedModels(ModelEvent.ModifyBakingResult event) {
        Map<ResourceLocation, BakedModel> eventModels = event.getModels();

        Map<CropState, BakedModel> statesToModel = GTCropSpecies.CROP_SPECIES_REGISTRY.get().getValues().stream().map(ICropSpecies::getModelMap).flatMap(map -> {
            return map.entrySet().stream();
        }).map(entry -> {
            return Pair.of(entry.getKey(), eventModels.get(entry.getValue()));
        }).filter(pair -> {
            // if failed to get model filter and warn
            if (pair.second() == null) {
                GreenThumbs.LOGGER.warn("Failed to get model for {}", pair.first());
                return false;
            }
            return true;
        }).collect(Collectors.toMap(Pair::first, Pair::second));

        // Stores models for each crop species
        GTBakedModel gtBakedModel = new GTBakedModel();
        gtBakedModel.addModels(statesToModel);
        eventModels.put(BlockModelShaper.stateToModelLocation(GTBlocks.GT_CROP_BLOCK.get().defaultBlockState()), gtBakedModel);
    }

    public static void registerColorHandlersEvent (RegisterColorHandlersEvent.Block colorHandlersEvent) {
        colorHandlersEvent.register(new CropColor(), GTBlocks.GT_CROP_BLOCK.get());
    }
}
