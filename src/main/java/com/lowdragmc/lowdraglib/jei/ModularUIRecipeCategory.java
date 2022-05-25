package com.lowdragmc.lowdraglib.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date: 2022/04/30
 * @implNote ModularUIRecipeCategory
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ModularUIRecipeCategory<T extends ModularWrapper<?>> implements IRecipeCategory<T> {

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        recipe.draw(stack, (int) mouseX, (int) mouseY);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        recipe.setRecipeLayout(0, 0);
    }
}
