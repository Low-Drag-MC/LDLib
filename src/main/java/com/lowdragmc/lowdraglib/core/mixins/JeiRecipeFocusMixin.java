package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.jei.JEIPlugin;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import mezz.jei.gui.GuiScreenHelper;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.input.ClickedIngredient;
import mezz.jei.input.GuiContainerWrapper;
import mezz.jei.input.IClickedIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author KilaBash
 * @date 2022/05/26
 * @implNote TODO
 */
@Mixin(GuiContainerWrapper.class)
public abstract class JeiRecipeFocusMixin {

    @Shadow @Final private GuiScreenHelper guiScreenHelper;

    @Inject(method = "getIngredientUnderMouse(DD)Ljava/util/stream/Stream;", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private void injectIngredientUnderMouse(double mouseX, double mouseY, CallbackInfoReturnable<Stream<IClickedIngredient<?>>> cir) {
        Screen guiScreen = Minecraft.getInstance().screen;
        if (guiScreen instanceof RecipesGui) {
            List<RecipeLayout<?>> recipeLayouts = JEIPlugin.getRecipeLayouts((RecipesGui) guiScreen);
            for (RecipeLayout<?> recipeLayout : recipeLayouts) {
                if (recipeLayout.isMouseOver(mouseX, mouseY)) {
                    Object wrapper = recipeLayout.getRecipe();
                    if (wrapper instanceof ModularWrapper modularWrapper) {
                        cir.setReturnValue(Stream.concat(cir.getReturnValue(), this.guiScreenHelper.getPluginsIngredientUnderMouse(modularWrapper, mouseX, mouseY)));
                    }
                }
            }
        }
    }

}
