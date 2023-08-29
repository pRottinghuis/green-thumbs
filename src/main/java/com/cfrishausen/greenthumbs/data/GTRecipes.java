package com.cfrishausen.greenthumbs.data;

import com.cfrishausen.greenthumbs.registries.GTCropSpecies;
import com.cfrishausen.greenthumbs.registries.GTItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class GTRecipes extends RecipeProvider {


    public GTRecipes(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
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

        GTCropShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GTItems.PUMPKIN_CUTTING.get(), GTCropSpecies.GT_PUMPKIN_STEM.get())
                .requires(Items.PUMPKIN_SEEDS)
                .unlockedBy("has_pumpkin_seeds", has(Items.PUMPKIN_SEEDS))
                .save(pWriter);
    }
}
