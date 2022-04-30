package com.lowdragmc.lowdraglib.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.recipes.RecipeLayout;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date: 2022/04/30
 * @implNote ModularUIRecipeCategory
 */
public abstract class ModularUIRecipeCategory<T extends ModularWrapper<?>> implements IRecipeCategory<T> {

    @Override
    public void draw(@Nonnull ModularWrapper recipe, @Nonnull MatrixStack matrixStack, double mouseX, double mouseY) {
        recipe.draw(matrixStack, (int) mouseX, (int) mouseY);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout iRecipeLayout, @Nonnull ModularWrapper modularWrapper, @Nonnull IIngredients iIngredients) {
        modularWrapper.setRecipeLayout((RecipeLayout<?>) iRecipeLayout);
    }
}
