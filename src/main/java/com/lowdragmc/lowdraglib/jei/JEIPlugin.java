package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIJeiHandler;
import mezz.jei.Internal;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.input.IShowsRecipeFocuses;
import mezz.jei.input.InputHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * @author KilaBash
 * @date 2022/04/30
 * @implNote jei plugin
 */
@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static Field fieldRecipeLayouts;
    static {
        try {
            fieldRecipeLayouts = RecipesGui.class.getDeclaredField("recipeLayouts");
            fieldRecipeLayouts.setAccessible(true);
        } catch (NoSuchFieldException e) {
            LDLMod.LOGGER.error(e);
        }
    }
    
    public static IJeiRuntime jeiRuntime;
    private static final ModularUIJeiHandler modularUIGuiHandler = new ModularUIJeiHandler();

    public JEIPlugin() {
        LDLMod.LOGGER.debug("LDLMod JEI Plugin created");
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static List<RecipeLayout<?>> getRecipeLayouts(RecipesGui recipesGui) {
        try {
            return (List<RecipeLayout<?>>) fieldRecipeLayouts.get(recipesGui);
        } catch (IllegalAccessException e) {
            LDLMod.LOGGER.error(e);
        }
        return Collections.emptyList();
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        JEIPlugin.jeiRuntime = jeiRuntime;
    }
    
    @Override
    public void registerGuiHandlers(@Nonnull IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(ModularUIGuiContainer.class, modularUIGuiHandler);
        registration.addGenericGuiContainerHandler(ModularUIGuiContainer.class, modularUIGuiHandler);
    }

    @Override
    public void registerAdvanced(@Nonnull IAdvancedRegistration registration) {
    }


    public static void setupInputHandler() {
        try {
            Field inputHandlerField = Internal.class.getDeclaredField("inputHandler");
            inputHandlerField.setAccessible(true);
            InputHandler inputHandler = (InputHandler) inputHandlerField.get(null);
            List<IShowsRecipeFocuses> showsRecipeFocuses = ObfuscationReflectionHelper.getPrivateValue(InputHandler.class, inputHandler, "showsRecipeFocuses");
            assert showsRecipeFocuses != null;
            showsRecipeFocuses.add(0, new MultiblockInfoRecipeFocusShower());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(LDLMod.MODID, "jei_plugin");
    }
}
