package com.cfrishausen.greenthumbs.genetics.integration;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.data.recipe.SeedSplicingStationRecipe;
import com.cfrishausen.greenthumbs.registries.GTRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import mezz.jei.api.recipe.RecipeType;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEIGreenThumbsPlugin implements IModPlugin {
    public static RecipeType<SeedSplicingStationRecipe> SPLICING_TYPE = new RecipeType<>(SeedSplicingStationRecipeCategory.UID, SeedSplicingStationRecipe.class);
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(GreenThumbs.ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SeedSplicingStationRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        List<SeedSplicingStationRecipe> recipesSplicing = rm.getAllRecipesFor(GTRecipeTypes.SEED_SPLICING_STATION_TYPE.get());
        registration.addRecipes(SPLICING_TYPE, recipesSplicing);
    }
}
