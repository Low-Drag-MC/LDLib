package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.input.ClickedIngredient;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.IShowsRecipeFocuses;
import net.minecraft.client.Minecraft;

import java.util.List;

public class MultiblockInfoRecipeFocusShower implements IShowsRecipeFocuses {
    @Override
    public IClickedIngredient<?> getIngredientUnderMouse(double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof ModularUIGuiContainer) {
            Object result = ((ModularUIGuiContainer) mc.screen).modularUI.mainGroup.getIngredientOverMouse(mouseX, mouseY);
            if (result != null) {
                return ClickedIngredient.create(result, null);
            }
        } else if (mc.screen instanceof RecipesGui){
            List<RecipeLayout<?>> recipeLayouts = JEIPlugin.getRecipeLayouts((RecipesGui) mc.screen);
            for (RecipeLayout<?> recipeLayout : recipeLayouts) {
                if (recipeLayout.isMouseOver(mouseX, mouseY)) {
                    Object wrapper = recipeLayout.getRecipe();
                    if (wrapper instanceof ModularWrapper) {
                        Object result = ((ModularWrapper) wrapper).modularUI.mainGroup.getIngredientOverMouse(mouseX, mouseY);
                        if (result != null) {
                            return ClickedIngredient.create(result, null); 
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean canSetFocusWithMouse() {
        return false;
    }
}
