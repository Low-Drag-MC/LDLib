package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.core.mixins.accessor.RecipesGuiAccessor;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIJeiHandler;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.gui.recipes.RecipesGui;
import mezz.jei.common.gui.recipes.layout.RecipeLayout;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author KilaBash
 * @date 2022/04/30
 * @implNote jei plugin
 */
@JeiPlugin
public class JEIPlugin implements IModPlugin {
    
    public static IJeiRuntime jeiRuntime;
    private static final ModularUIJeiHandler modularUIGuiHandler = new ModularUIJeiHandler();

    public JEIPlugin() {
        LDLMod.LOGGER.debug("LDLMod JEI Plugin created");
    }

    @Nonnull
    public static List<RecipeLayout<?>> getRecipeLayouts(RecipesGui recipesGui) {
        return ((RecipesGuiAccessor)recipesGui).getRecipeLayouts();
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
        //:P
    }

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(LDLMod.MODID, "jei_plugin");
    }
}
