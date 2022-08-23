package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import com.lowdragmc.lowdraglib.jei.RecipeLayoutWrapper;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;

import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.ingredients.RegisteredIngredients;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RecipeLayout.class, remap = false)
public abstract class JeiRecipeLayoutMixin {

    @SuppressWarnings({"unchecked"})
    @Inject(method = "create", at = @At(value = "HEAD"), cancellable = true)
    private static <T> void injectCreate(
            int index,
            IRecipeCategory<T> recipeCategory,
            T recipe,
            IFocusGroup focuses,
            RegisteredIngredients registeredIngredients,
            IModIdHelper modIdHelper,
            int posX,
            int posY,
            CallbackInfoReturnable<@Nullable RecipeLayout<T>> cir
    ) {
        if (recipe instanceof ModularWrapper<?> wrapper && recipeCategory != null) {
            IRecipeCategory<ModularWrapper<?>> category = (IRecipeCategory<ModularWrapper<?>>) recipeCategory;
            RecipeLayout<T> recipeLayoutWrapper = (RecipeLayout<T>) RecipeLayoutWrapper.createWrapper(index, category, wrapper, focuses, registeredIngredients, posX, posY);
            cir.setReturnValue(recipeLayoutWrapper);
        }
    }

}
