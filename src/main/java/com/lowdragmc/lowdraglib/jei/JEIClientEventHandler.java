package com.lowdragmc.lowdraglib.jei;

import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author KilaBash
 * @date: 2022/04/30
 * @implNote JEIClientEventHandler
 */
public class JEIClientEventHandler {


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRecipesUpdatedEventEvent(RecipesUpdatedEvent event) {
        JEIPlugin.setupInputHandler();
    }

    @SubscribeEvent
    public static void onMouseClickedEventPre(ScreenEvent.MouseClickedEvent.Pre event) {
        if (event.getScreen() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getScreen())) {
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
    public static void onMouseReleasedEventPre(ScreenEvent.MouseReleasedEvent.Pre event) {
        if (event.getScreen() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getScreen())) {
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
    public static void onMouseDragEventPre(ScreenEvent.MouseDragEvent.Pre event) {
        if (event.getScreen() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getScreen())) {
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
    public static void onMouseScrollEventPre(ScreenEvent.MouseScrollEvent.Pre event) {
        if (event.getScreen() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getScreen())) {
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
    public static void onKeyboardKeyPressedEventPre(ScreenEvent.KeyboardKeyPressedEvent event) {
        if (event.getScreen() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getScreen())) {
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
    public static void onKeyboardKeyReleasedEventEventPre(ScreenEvent.KeyboardKeyReleasedEvent event) {
        if (event.getScreen() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getScreen())) {
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
    public static void onKeyboardCharTypedEventEventPre(ScreenEvent.KeyboardCharTypedEvent event) {
        if (event.getScreen() instanceof RecipesGui) {
            for (RecipeLayout<?> recipeLayout : JEIPlugin.getRecipeLayouts((RecipesGui) event.getScreen())) {
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
