package com.lowdragmc.lowdraglib.gui.ingredient;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.jei.IngredientIO;

import javax.annotation.Nullable;

public interface IRecipeIngredientSlot extends IIngredientSlot{

    default Widget self() {
        return (Widget) this;
    }

    @Nullable
    @Override
    default Object getIngredientOverMouse(double mouseX, double mouseY) {
        if (self().isMouseOverElement(mouseX, mouseY)) {
            return getJEIIngredient();
        }
        return null;
    }

    @Nullable
    Object getJEIIngredient();

    default IngredientIO getIngredientIO(){
        return IngredientIO.RENDER_ONLY;
    }

}
