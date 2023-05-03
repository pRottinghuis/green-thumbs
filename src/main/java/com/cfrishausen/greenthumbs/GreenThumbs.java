package com.cfrishausen.greenthumbs;

import com.cfrishausen.greenthumbs.client.ClientHandler;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.event.EventHandler;
import com.cfrishausen.greenthumbs.registries.GTBlockEntities;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import com.cfrishausen.greenthumbs.registries.GTCropSpecies;
import com.cfrishausen.greenthumbs.registries.GTItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(GreenThumbs.ID)
public class GreenThumbs
{
    public static final String ID = "greenthumbs";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    public GreenThumbs()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        GTBlocks.BLOCKS.register(modEventBus);
        GTItems.ITEMS.register(modEventBus);
        GTBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        GTCropSpecies.CROP_SPECIES.register(modEventBus);

        EventHandler.register();

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientHandler::registerEvents);
    }
}
