package com.cfrishausen.greenthumbs;

import com.cfrishausen.greenthumbs.client.ClientHandler;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.NBTTags;
import com.cfrishausen.greenthumbs.event.EventHandler;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import com.cfrishausen.greenthumbs.registries.GTBlockEntities;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import com.cfrishausen.greenthumbs.registries.GTCropSpecies;
import com.cfrishausen.greenthumbs.registries.GTItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
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

        modEventBus.addListener(GreenThumbs::addCreativeTab);
    }

    private static void addCreativeTab(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(ID, ID), builder -> {
            builder.icon(() -> new ItemStack(GTItems.CARROT_SEEDS.get()));
            builder.title(Component.literal("Green Thumbs"));
            builder.displayItems((params, output) -> {
                output.accept(GTItems.GT_DEBUG_STICK.get());
                output.accept(getStackWithTag(GTItems.WHEAT_SEEDS.get(), GTCropSpecies.GT_WHEAT.get()));
                output.accept(getStackWithTag(GTItems.BEETROOT_SEEDS.get(), GTCropSpecies.GT_BEETROOT.get()));
                output.accept(getStackWithTag(GTItems.CARROT_SEEDS.get(), GTCropSpecies.GT_CARROT.get()));
                output.accept(getStackWithTag(GTItems.POTATO_SEEDS.get(), GTCropSpecies.GT_POTATO.get()));
            });
        });
    }

    private static ItemStack getStackWithTag(Item item, ICropSpecies species) {
        ItemStack stack = new ItemStack(item);
        CompoundTag infoTag = new CompoundTag();
        CompoundTag saveTag = new CompoundTag();

        infoTag.put(NBTTags.INFO_TAG, saveTag);
        saveTag.put(NBTTags.GENOME_TAG, species.defineGenome().writeTag());
        saveTag.putInt(NBTTags.AGE_TAG, 0);
        saveTag.putString(NBTTags.CROP_SPECIES_TAG, GTCropSpecies.CROP_SPECIES_REGISTRY.get().getKey(species).toString());
        stack.setTag(infoTag);
        return stack;
    }
}
