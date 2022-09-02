package com.lowdragmc.lowdraglib.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.deprecated.gui.recipes.RecipeLayoutLegacyAdapter;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

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
    public void setRecipe(IRecipeLayout recipeLayout, T recipe, IIngredients ingredients) {
        if (recipeLayout instanceof RecipeLayoutLegacyAdapter<?> layout) {
            RecipeLayout<?> rl = ObfuscationReflectionHelper.getPrivateValue(RecipeLayoutLegacyAdapter.class, layout, "recipeLayout");
            if (rl != null) {
                recipe.setRecipeLayout(rl.getPosX(), rl.getPosY());
            }
        }
    }
}
