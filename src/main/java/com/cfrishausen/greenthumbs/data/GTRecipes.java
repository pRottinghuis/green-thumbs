package com.cfrishausen.greenthumbs.data;

import com.cfrishausen.greenthumbs.GreenThumbs;
import com.cfrishausen.greenthumbs.data.recipe.GTCropShapelessRecipeBuilder;
import com.cfrishausen.greenthumbs.data.recipe.GTSeedSplicingStationRecipeBuilder;
import com.cfrishausen.greenthumbs.item.custom.GTGenomeCropBlockItem;
import com.cfrishausen.greenthumbs.registries.GTCropSpecies;
import com.cfrishausen.greenthumbs.registries.GTItems;
import com.cfrishausen.greenthumbs.registries.GTRecipeTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class GTRecipes extends RecipeProvider {


    public GTRecipes(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        // Crafting recipes
        GTCropShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GTItems.CARROT_SEEDS.get(), GTCropSpecies.GT_CARROT.get())
                .requires(Items.CARROT)
                .unlockedBy("has_carrot", has(Items.CARROT))
                .save(pWriter);

        GTCropShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GTItems.WHEAT_SEEDS.get(), GTCropSpecies.GT_WHEAT.get())
                .requires(Items.WHEAT_SEEDS)
                .unlockedBy("has_wheat_seeds", has(Items.WHEAT_SEEDS))
                .save(pWriter);

        GTCropShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GTItems.POTATO_SEEDS.get(), GTCropSpecies.GT_POTATO.get())
                .requires(Items.POTATO)
                .unlockedBy("has_potato", has(Items.POTATO))
                .save(pWriter);

        GTCropShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GTItems.BEETROOT_SEEDS.get(), GTCropSpecies.GT_BEETROOT.get())
                .requires(Items.BEETROOT_SEEDS)
                .unlockedBy("has_beetroot_seeds", has(Items.BEETROOT_SEEDS))
                .save(pWriter);

        GTCropShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GTItems.SWEET_BERRY_SEEDS.get(), GTCropSpecies.GT_SWEET_BERRY.get())
                .requires(Items.SWEET_BERRIES)
                .unlockedBy("has_sweet_berries", has(Items.SWEET_BERRIES))
                .save(pWriter);

        GTCropShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GTItems.PUMPKIN_SEEDS.get(), GTCropSpecies.GT_PUMPKIN_STEM.get())
                .requires(Items.PUMPKIN_SEEDS)
                .unlockedBy("has_pumpkin_seeds", has(Items.PUMPKIN_SEEDS))
                .save(pWriter);

        GTCropShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GTItems.MELON_SEEDS.get(), GTCropSpecies.GT_MELON_STEM.get())
                .requires(Items.MELON_SEEDS)
                .unlockedBy("has_melon_seeds", has(Items.MELON_SEEDS))
                .save(pWriter);


        // Seed splicing station recipes
        createSplicingRecipe(GTItems.CARROT_SEEDS, pWriter);
        createSplicingRecipe(GTItems.WHEAT_SEEDS, pWriter);
        createSplicingRecipe(GTItems.POTATO_SEEDS, pWriter);
        createSplicingRecipe(GTItems.BEETROOT_SEEDS, pWriter);
        createSplicingRecipe(GTItems.SWEET_BERRY_SEEDS, pWriter);
        createSplicingRecipe(GTItems.PUMPKIN_SEEDS, pWriter);
        createSplicingRecipe(GTItems.MELON_SEEDS, pWriter);

        createSplicingRecipe(GTItems.CARROT_CUTTING, pWriter);
        createSplicingRecipe(GTItems.WHEAT_CUTTING, pWriter);
        createSplicingRecipe(GTItems.POTATO_CUTTING, pWriter);
        createSplicingRecipe(GTItems.BEETROOT_CUTTING, pWriter);
        createSplicingRecipe(GTItems.SWEET_BERRY_CUTTING, pWriter);
        createSplicingRecipe(GTItems.PUMPKIN_CUTTING, pWriter);
        createSplicingRecipe(GTItems.MELON_CUTTING, pWriter);


    }

    private void createSplicingRecipe(RegistryObject<? extends GTGenomeCropBlockItem> cropItem, Consumer<FinishedRecipe> pWriter) {
        GTSeedSplicingStationRecipeBuilder.recipe(RecipeCategory.MISC, cropItem.get(), GTRecipeTypes.SEED_SPLICING_STATION_SERIALIZER.get())
                .requires(cropItem.get())
                .unlockedBy("has_gt_" + cropItem.getId().getPath(), has(cropItem.get()))
                .save(pWriter, new ResourceLocation(GreenThumbs.ID, "seed_splicing_" + cropItem.getId().getPath()));
    }
}
