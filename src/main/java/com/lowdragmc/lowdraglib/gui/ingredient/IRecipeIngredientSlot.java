package com.lowdragmc.lowdraglib.gui.ingredient;

import com.lowdragmc.lowdraglib.jei.IngredientIO;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IRecipeIngredientSlot {

    Object getIngredientOverMouse(double mouseX, double mouseY);

    @NotNull
    Object getContent();

    Object getJEIIngredient();

    int getPosX();

    int getPosY();

    default IngredientIO getIngredientIo(){
        return IngredientIO.RENDER_ONLY;
    }

}
