package com.cfrishausen.greenthumbs.event;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

public class EventHandler {

    public static void register() {
        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forge = MinecraftForge.EVENT_BUS;
        forge.addListener(EventHandler::createNewRegistry);
    }

    public static void createNewRegistry(NewRegistryEvent event) {
        event.create(new RegistryBuilder<ICropSpecies>().setName(new ResourceLocation(GreenThumbs.ID, "crop_type")));
    }
}
