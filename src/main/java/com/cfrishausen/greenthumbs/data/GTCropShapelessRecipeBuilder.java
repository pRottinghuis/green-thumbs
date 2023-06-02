package com.cfrishausen.greenthumbs.data;

import com.cfrishausen.greenthumbs.crop.ICropEntity;
import com.cfrishausen.greenthumbs.crop.ICropSpecies;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import com.cfrishausen.greenthumbs.registries.GTCropSpecies;
import com.cfrishausen.greenthumbs.registries.GTItems;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GTCropShapelessRecipeBuilder extends CraftingRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final Item result;
    private final int count;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private final ICropSpecies cropSpecies;

    @javax.annotation.Nullable
    private String group;

    public GTCropShapelessRecipeBuilder(RecipeCategory recipeCategory, ItemLike result, int count, ICropSpecies cropSpecies) {
        this.category = recipeCategory;
        this.result = result.asItem();
        this.count = count;
        this.cropSpecies = cropSpecies;
    }

    public static GTCropShapelessRecipeBuilder shapeless(RecipeCategory recipeCategory, ItemLike itemLike, ICropSpecies cropSpecies) {
        return new GTCropShapelessRecipeBuilder(recipeCategory, itemLike, 1, cropSpecies);
    }

    public static GTCropShapelessRecipeBuilder shapeless(RecipeCategory recipeCategory, ItemLike result, int count, ICropSpecies cropSpecies) {
        return new GTCropShapelessRecipeBuilder(recipeCategory, result, count, cropSpecies);
    }

    /**
     * Adds an ingredient that can be any item in the given tag.
     */
    public GTCropShapelessRecipeBuilder requires(TagKey<Item> pTag) {
        return this.requires(Ingredient.of(pTag));
    }

    /**
     * Adds an ingredient of the given item.
     */
    public GTCropShapelessRecipeBuilder requires(ItemLike pItem) {
        return this.requires(pItem, 1);
    }

    /**
     * Adds the given ingredient multiple times.
     */
    public GTCropShapelessRecipeBuilder requires(ItemLike pItem, int pQuantity) {
        for(int i = 0; i < pQuantity; ++i) {
            this.requires(Ingredient.of(pItem));
        }

        return this;
    }

    /**
     * Adds an ingredient.
     */
    public GTCropShapelessRecipeBuilder requires(Ingredient pIngredient) {
        return this.requires(pIngredient, 1);
    }

    /**
     * Adds an ingredient multiple times.
     */
    public GTCropShapelessRecipeBuilder requires(Ingredient pIngredient, int pQuantity) {
        for(int i = 0; i < pQuantity; ++i) {
            this.ingredients.add(pIngredient);
        }

        return this;
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
        return this.result;
    }

    // Default saves puts on carrot genome
    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
        pFinishedRecipeConsumer.accept(new GTCropShapelessRecipeBuilder.Result(pRecipeId,
                this.result,
                this.count,
                this.group == null ? "" : this.group,
                determineBookCategory(this.category),
                this.ingredients,
                this.advancement,
                pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"),
                cropSpecies));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void ensureValid(ResourceLocation pId) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pId);
        }
    }

    public static class Result extends CraftingRecipeBuilder.CraftingResult {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final String group;
        private final List<Ingredient> ingredients;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        private final ICropSpecies cropSpecies;

        public Result(ResourceLocation id, Item result, int count, String group, CraftingBookCategory p_249485_, List<Ingredient> ingredients, Advancement.Builder advancement, ResourceLocation advancementId, ICropSpecies cropSpecies) {
            super(p_249485_);
            this.id = id;
            this.result = result;
            this.count = count;
            this.group = group;
            this.ingredients = ingredients;
            this.advancement = advancement;
            this.advancementId = advancementId;
            this.cropSpecies = cropSpecies;
        }

        public void serializeRecipeData(JsonObject pJson) {
            super.serializeRecipeData(pJson);
            if (!this.group.isEmpty()) {
                pJson.addProperty("group", this.group);
            }

            JsonArray jsonarray = new JsonArray();

            for(Ingredient ingredient : this.ingredients) {
                jsonarray.add(ingredient.toJson());
            }

            pJson.add("ingredients", jsonarray);
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
            if (this.count > 1) {
                jsonobject.addProperty("count", this.count);
            }

            // Example format
            // "{\"greenthumbs.Info\": {\"greenthumbs.Genome\": {\"growth-speed\": \"Gg\"}, \"greenthumbs.Age\": 0, \"greenthumbs.CropSpecies\": \"greenthumbs:gt_carrot\"}}"

            // Add nbt section
            String nbtString = "{\"greenthumbs.Info\": {\"greenthumbs.Genome\": {";

            // Add specific genes
            Map<String, String> cropSpeciesGenes = cropSpecies.defineGenome().getGenes();
            for (String geneName : cropSpeciesGenes.keySet()) {
                nbtString += "\"" + geneName + "\"" + ": "; // "growth-speed":
                nbtString += "\"" + cropSpeciesGenes.get(geneName) + "\", "; //"Gg"
            }
            // Remove ending comma and space
            nbtString = nbtString.substring(0, nbtString.length() - 2);

            nbtString += "}, \"greenthumbs.Age\": 0, \"greenthumbs.CropSpecies\": \"";

            // add crop species
            nbtString += GTCropSpecies.CROP_SPECIES_REGISTRY.get().getKey(cropSpecies).toString() + "\"}}";

            jsonobject.addProperty("nbt", nbtString);

            pJson.add("result", jsonobject);
        }

        public RecipeSerializer<?> getType() {
            return RecipeSerializer.SHAPELESS_RECIPE;
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
         * Gets the ID for the advancement associated with this recipe. Should not be null if {getAdvancementJson}
         * is non-null.
         */
        @javax.annotation.Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
