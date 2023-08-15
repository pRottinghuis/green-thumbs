package com.cfrishausen.greenthumbs.registries;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.species.*;
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

    public static final RegistryObject<ICropSpecies> GT_CARROT = CROP_SPECIES.register("gt_carrot",
            () -> new RootCrop("carrots", GTItems.CARROT_SEEDS.get(), Items.CARROT, GTItems.CARROT_CUTTING.get()));

    public static final RegistryObject<ICropSpecies> GT_WHEAT = CROP_SPECIES.register("gt_wheat",
            () -> new BasicCrop("wheat", GTItems.WHEAT_SEEDS.get(), Items.WHEAT, GTItems.WHEAT_CUTTING.get()));

    public static final RegistryObject<ICropSpecies> GT_POTATO = CROP_SPECIES.register("gt_potato",
            () -> new RootCrop("potatoes", GTItems.POTATO_SEEDS.get(), Items.POTATO, GTItems.POTATO_CUTTING.get()));

    public static final RegistryObject<ICropSpecies> GT_BEETROOT = CROP_SPECIES.register("gt_beetroot",
            () -> new BeetrootCrop("beetroots", GTItems.BEETROOT_SEEDS.get(), Items.BEETROOT, GTItems.BEETROOT_CUTTING.get()));

    public static final RegistryObject<ICropSpecies> GT_PUMPKIN = GTCropSpecies.CROP_SPECIES.register("gt_pumpkin",
            () -> new StemGrownCrop("pumpkin", GTItems.PUMPKIN_SEEDS.get(), Items.PUMPKIN, GTItems.PUMPKIN_CUTTING.get(),
                    GTCropSpecies.GT_PUMPKIN_STEM, GTCropSpecies.GT_ATTACHED_PUMPKIN_STEM));

    public static final RegistryObject<ICropSpecies> GT_PUMPKIN_STEM = CROP_SPECIES.register("gt_pumpkin_stem",
            () -> new StemCrop("pumpkin_stem", GTItems.PUMPKIN_SEEDS.get(), Items.PUMPKIN, GTItems.PUMPKIN_CUTTING.get(),
                    GT_PUMPKIN));

    public static final RegistryObject<ICropSpecies> GT_ATTACHED_PUMPKIN_STEM = CROP_SPECIES.register("gt_pumpkin_attached_stem",
            () -> new AttachedStemCrop("attached_pumpkin_stem", GTItems.PUMPKIN_SEEDS.get(), Items.PUMPKIN, GTItems.PUMPKIN_CUTTING.get(),
                    GT_PUMPKIN));

    public static ICropSpecies getSpecies(ResourceLocation key) {
        return CROP_SPECIES_REGISTRY.get().getValue(key);
    }
}
