package com.cfrishausen.greenthumbs.data.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class GTSeedSplicingStationRecipeBuilder implements RecipeBuilder {

    private final RecipeCategory category;

    private final Item result;
    private Ingredient ingredient = Ingredient.EMPTY;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    @javax.annotation.Nullable
    private String group;
    private final RecipeSerializer<? extends SeedSplicingStationRecipe> serializer;

    public GTSeedSplicingStationRecipeBuilder(RecipeCategory category, Item result, RecipeSerializer<? extends SeedSplicingStationRecipe> serializer) {
        this.category = category;
        this.result = result;
        this.serializer = serializer;
    }

    public static GTSeedSplicingStationRecipeBuilder recipe(RecipeCategory recipeCategory, ItemLike result, RecipeSerializer<? extends SeedSplicingStationRecipe> serializer) {
        return new GTSeedSplicingStationRecipeBuilder(recipeCategory, result.asItem(), serializer);
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.asItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
        pFinishedRecipeConsumer.accept(new GTSeedSplicingStationRecipeBuilder.Result(pRecipeId, this.group == null ? "" : this.group, this.ingredient, this.result.asItem(), this.advancement, pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"), this.serializer));
    }

    public GTSeedSplicingStationRecipeBuilder requires(ItemLike pItem) {
        this.ingredient = Ingredient.of(pItem);
        return this;
    }

    private void ensureValid(ResourceLocation pId) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pId);
        }
    }

    static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final Ingredient ingredient;
        private final Item result;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<? extends SeedSplicingStationRecipe> serializer;

        public Result(ResourceLocation pId, String pGroup, Ingredient pIngredient, Item pResult, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId, RecipeSerializer<? extends SeedSplicingStationRecipe> pSerializer) {
            this.id = pId;
            this.group = pGroup;
            this.ingredient = pIngredient;
            this.result = pResult;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
            this.serializer = pSerializer;
        }

        public void serializeRecipeData(JsonObject pJson) {
            if (!this.group.isEmpty()) {
                pJson.addProperty("group", this.group);
            }

            JsonArray jsonarray = new JsonArray();
            jsonarray.add(this.ingredient.toJson());
            pJson.add("ingredients", jsonarray);

            pJson.addProperty("result", BuiltInRegistries.ITEM.getKey(this.result).toString());
        }

        public RecipeSerializer<?> getType() {
            return this.serializer;
        }

        /**
         * Gets the ID for the recipe.
         */
        public ResourceLocation getId() {
            return this.id;
        }

        /**
         * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
         */
        @javax.annotation.Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        /**
         * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson}
         * is non-null.
         */
        @javax.annotation.Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
