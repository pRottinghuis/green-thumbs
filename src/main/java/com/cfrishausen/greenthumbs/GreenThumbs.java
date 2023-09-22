package com.cfrishausen.greenthumbs;

import com.cfrishausen.greenthumbs.client.ClientHandler;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.NBTTags;
import com.cfrishausen.greenthumbs.event.EventHandler;
import com.cfrishausen.greenthumbs.registries.*;
import com.cfrishausen.greenthumbs.screen.SeedSplicingStationScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        GTLootFunctions.LOOT_FUNCTION_TYPE.register(modEventBus);
        GTMenuTypes.register(modEventBus);
        GTRecipeTypes.register(modEventBus);

        EventHandler.register();

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientHandler::registerEvents);

        modEventBus.addListener(GreenThumbs::addCreativeTab);
    }

    @Mod.EventBusSubscriber(modid = ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            MenuScreens.register(GTMenuTypes.SEED_SPLICING_STATION_MENU.get(), SeedSplicingStationScreen::new);
        }
    }

    private static void addCreativeTab(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(ID, ID), builder -> {
            builder.icon(() -> new ItemStack(GTItems.CARROT_SEEDS.get()));
            builder.title(Component.literal("Green Thumbs"));
            builder.displayItems((params, output) -> {

                // Other items
                //output.accept(GTItems.GT_DEBUG_STICK.get());
                output.accept(GTItems.SEED_SPLICING_STATION.get());

                // Seeds
                output.accept(getStackWithTag(GTItems.WHEAT_SEEDS.get(), GTCropSpecies.GT_WHEAT.get()));
                output.accept(getStackWithTag(GTItems.BEETROOT_SEEDS.get(), GTCropSpecies.GT_BEETROOT.get()));
                output.accept(getStackWithTag(GTItems.CARROT_SEEDS.get(), GTCropSpecies.GT_CARROT.get()));
                output.accept(getStackWithTag(GTItems.POTATO_SEEDS.get(), GTCropSpecies.GT_POTATO.get()));
                output.accept(getStackWithTag(GTItems.SWEET_BERRY_SEEDS.get(), GTCropSpecies.GT_SWEET_BERRY.get()));
                output.accept(getStackWithTag(GTItems.PUMPKIN_SEEDS.get(), GTCropSpecies.GT_PUMPKIN_STEM.get()));
                output.accept(getStackWithTag(GTItems.MELON_SEEDS.get(), GTCropSpecies.GT_MELON_STEM.get()));

                // Cuttings
                output.accept(getStackWithTag(GTItems.WHEAT_CUTTING.get(), GTCropSpecies.GT_WHEAT.get()));
                output.accept(getStackWithTag(GTItems.BEETROOT_CUTTING.get(), GTCropSpecies.GT_BEETROOT.get()));
                output.accept(getStackWithTag(GTItems.CARROT_CUTTING.get(), GTCropSpecies.GT_CARROT.get()));
                output.accept(getStackWithTag(GTItems.POTATO_CUTTING.get(), GTCropSpecies.GT_POTATO.get()));
                output.accept(getStackWithTag(GTItems.SWEET_BERRY_CUTTING.get(), GTCropSpecies.GT_SWEET_BERRY.get()));
                output.accept(getStackWithTag(GTItems.PUMPKIN_CUTTING.get(), GTCropSpecies.GT_PUMPKIN_STEM.get()));
                output.accept(getStackWithTag(GTItems.MELON_CUTTING.get(), GTCropSpecies.GT_MELON_STEM.get()));
            });
        });
    }

    public static ItemStack getStackWithTag(Item item, ICropSpecies species) {
        ItemStack stack = new ItemStack(item);
        stack.setTag(NBTTags.defaultSpeciesTag(species));
        return stack;
    }
}
