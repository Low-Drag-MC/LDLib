package com.lowdragmc.lowdraglib.core.mixins.accessor;

import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.ingredients.RecipeSlot;
import mezz.jei.ingredients.RegisteredIngredients;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RecipeSlot.class,remap = false)
public interface RecipeSlotAccessor {
    @Accessor("rect")
    void setArea(ImmutableRect2i rect);

    @Accessor
    RegisteredIngredients getRegisteredIngredients();
}
