package com.cfrishausen.greenthumbs.registries;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.BasicCrop;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class GTCropSpecies {
    public static final DeferredRegister<ICropSpecies> CROP_SPECIES = DeferredRegister.create(new ResourceLocation(GreenThumbs.ID, "crop_species_registry"), GreenThumbs.ID);
    public static final Supplier<IForgeRegistry<ICropSpecies>> CROP_SPECIES_REGISTRY = GTCropSpecies.CROP_SPECIES.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<ICropSpecies> GT_CARROTS = CROP_SPECIES.register("gt_carrots",
            () -> new BasicCrop(GTItems.GT_CARROT_SEEDS.get(), Items.CARROT));

    public static final RegistryObject<ICropSpecies> GT_WHEAT = CROP_SPECIES.register("gt_wheat",
            () -> new BasicCrop(GTItems.GT_WHEAT_SEEDS.get(), Items.WHEAT_SEEDS));
}
