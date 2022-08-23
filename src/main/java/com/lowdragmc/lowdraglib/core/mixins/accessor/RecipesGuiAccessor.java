package com.lowdragmc.lowdraglib.core.mixins.accessor;

import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = RecipesGui.class,remap = false)
public interface RecipesGuiAccessor {
    @Accessor
    List<RecipeLayout<?>> getRecipeLayouts();
}
