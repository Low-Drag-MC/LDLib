package com.lowdragmc.lowdraglib.jei;

import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author KilaBash
 * @date: 2022/04/30
 * @implNote JEIClientEventHandler
 */
public class JEIClientEventHandler {

    @SubscribeEvent
    public static void onMouseClickedEventPre(GuiScreenEvent.MouseClickedEvent.Pre event) {
        if (event.getGui() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getGui())) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton())) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMouseReleasedEventPre(GuiScreenEvent.MouseReleasedEvent.Pre event) {
        if (event.getGui() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getGui())) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).mouseReleased(event.getMouseX(), event.getMouseY(), event.getButton())) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMouseDragEventPre(GuiScreenEvent.MouseDragEvent.Pre event) {
        if (event.getGui() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getGui())) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).mouseDragged(event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY())) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onMouseScrollEventPre(GuiScreenEvent.MouseScrollEvent.Pre event) {
        if (event.getGui() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getGui())) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDelta())) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKeyboardKeyPressedEventPre(GuiScreenEvent.KeyboardKeyPressedEvent event) {
        if (event.getGui() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getGui())) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).keyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers())) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKeyboardKeyReleasedEventEventPre(GuiScreenEvent.KeyboardKeyReleasedEvent event) {
        if (event.getGui() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getGui())) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).keyReleased(event.getKeyCode(), event.getScanCode(), event.getModifiers())) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKeyboardCharTypedEventEventPre(GuiScreenEvent.KeyboardCharTypedEvent event) {
        if (event.getGui() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getGui())) {
                Object recipe = recipeLayout.getRecipe();
                if (recipe instanceof ModularWrapper) {
                    if (((ModularWrapper<?>) recipe).charTyped(event.getCodePoint(), event.getModifiers())) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
