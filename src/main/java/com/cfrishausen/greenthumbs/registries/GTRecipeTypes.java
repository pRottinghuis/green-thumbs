package com.cfrishausen.greenthumbs.registries;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.data.recipe.SeedSplicingStationRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GTRecipeTypes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, GreenThumbs.ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, GreenThumbs.ID);

    public static final RegistryObject<RecipeSerializer<SeedSplicingStationRecipe>> SEED_SPLICING_STATION_SERIALIZER = SERIALIZERS.register("seed_splicing", () -> SeedSplicingStationRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeType<SeedSplicingStationRecipe>> SEED_SPLICING_STATION_TYPE = RECIPE_TYPES.register("seed_splicing", () -> SeedSplicingStationRecipe.Type.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);

    }
}
