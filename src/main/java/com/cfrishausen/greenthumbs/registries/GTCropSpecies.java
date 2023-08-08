package com.cfrishausen.greenthumbs.registries;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.crop.species.BasicCrop;
import com.cfrishausen.greenthumbs.crop.species.BeetrootCrop;
import com.cfrishausen.greenthumbs.crop.species.RootCrop;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;

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

    public static ICropSpecies getSpecies(ResourceLocation key) {
        return CROP_SPECIES_REGISTRY.get().getValue(key);
    }
}
