package com.lowdragmc.lowdraglib.core.mixins.accessor;

import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.gui.elements.DrawableNineSliceTexture;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.ShapelessIcon;
import mezz.jei.ingredients.RegisteredIngredients;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RecipeLayout.class,remap = false)
public interface RecipeLayoutAccessor {

    @Accessor("LOGGER")
    Logger getLogger();

    @Accessor
    int getIngredientCycleOffset();

    @Accessor
    RegisteredIngredients getRegisteredIngredients();

    @Accessor
    DrawableNineSliceTexture getRecipeBorder();

    @Accessor
    ShapelessIcon getShapelessIcon();

}
