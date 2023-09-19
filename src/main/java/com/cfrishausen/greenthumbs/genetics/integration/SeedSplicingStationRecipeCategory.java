package com.cfrishausen.greenthumbs.genetics.integration;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.data.recipe.SeedSplicingStationRecipe;
import com.cfrishausen.greenthumbs.registries.GTBlocks;
import com.cfrishausen.greenthumbs.registries.GTItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class SeedSplicingStationRecipeCategory implements IRecipeCategory<SeedSplicingStationRecipe> {

    public static final ResourceLocation UID = new ResourceLocation(GreenThumbs.ID, "seed_splicing");
    public static final ResourceLocation TEXTURE = new ResourceLocation(GreenThumbs.ID, "textures/gui/container/jei_seed_splicing_station_gui.png");

    private final IDrawable background;
    private final IDrawable icon;

    public SeedSplicingStationRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 95);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(GTBlocks.SEED_SPLICING_STATION.get()));
    }

    @Override
    public RecipeType<SeedSplicingStationRecipe> getRecipeType() {
        return JEIGreenThumbsPlugin.SPLICING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(GTItems.SEED_SPLICING_STATION.get().getDescriptionId());
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SeedSplicingStationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 24, 22).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 24, 58).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 134, 40).addIngredients(Ingredient.of(recipe.getResultItem(null)));
    }
}
