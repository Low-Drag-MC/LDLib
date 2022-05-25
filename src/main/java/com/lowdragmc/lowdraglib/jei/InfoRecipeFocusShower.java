package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.input.ClickedIngredient;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.IRecipeFocusSource;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class InfoRecipeFocusShower implements IRecipeFocusSource {
    @Nonnull
    @Override
    public Optional<IClickedIngredient<?>> getIngredientUnderMouse(double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
//        if (mc.screen instanceof ModularUIGuiContainer) {
//            Object result = ((ModularUIGuiContainer) mc.screen).modularUI.mainGroup.getIngredientOverMouse(mouseX, mouseY);
//            if (result != null) {
//                return Optional.of(new ClickedIngredient(result, null, false, false));
//            }
//        } else if (mc.screen instanceof RecipesGui){
//            List<RecipeLayout<?>> recipeLayouts = JEIPlugin.getRecipeLayouts((RecipesGui) mc.screen);
//            for (RecipeLayout<?> recipeLayout : recipeLayouts) {
//                if (recipeLayout.isMouseOver(mouseX, mouseY)) {
//                    Object wrapper = recipeLayout.getRecipe();
//                    if (wrapper instanceof ModularWrapper) {
//                        Object result = ((ModularWrapper<?>) wrapper).modularUI.mainGroup.getIngredientOverMouse(mouseX, mouseY);
//                        if (result != null) {
//
//                            return ClickedIngredient.create(result, null);
//                        }
//                    }
//                }
//            }
//        }
        return Optional.empty();
    }

}
