package com.cfrishausen.greenthumbs.client;

import com.cfrishausen.greenthumbs.client.model.block.GTWheatBakedModel;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import jdk.jfr.FlightRecorder;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.Event;
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
            ItemBlockRenderTypes.setRenderLayer(GTBlocks.GT_WHEAT.get(), RenderType.cutout());
        });
    }


    // Event used to copy baked models from minecraft to this mod
    public static void registerBakedModels(ModelEvent.ModifyBakingResult event) {
        Map<ResourceLocation, BakedModel> models = event.getModels();
        ArrayList<BakedModel> bakedModels = new ArrayList<>();
        // Get baked model for each age of crop
        for (int age = 0; age <= 7; age++) {
            ResourceLocation location = BlockModelShaper.stateToModelLocation(Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, age));
            bakedModels.add(models.get(location));
        }
        // Create custom baked model class with each age baked model collected
        BakedModel bakedModel = new GTWheatBakedModel(bakedModels.toArray(new BakedModel[0]));
        // Replace our model in register of baked models
        models.put(BlockModelShaper.stateToModelLocation(GTBlocks.GT_WHEAT.get().defaultBlockState()), bakedModel);
    }
}
