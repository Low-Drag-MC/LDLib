package com.lowdragmc.lowdraglib.rei;

import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.impl.client.gui.screen.AbstractDisplayViewingScreen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class REIClientEventHandler {

    @SubscribeEvent
    public static void onMouseClickedEventPre(ScreenEvent.MouseClickedEvent.Pre event) {
        if (event.getScreen() instanceof AbstractDisplayViewingScreen displayScreen) {
            for (Display display : ReiUtils.getCurrentCategoryDisplays(displayScreen)) {
                Object recipe = DisplayRegistry.getInstance().getDisplayOrigin(display);
                if (recipe instanceof ModularWrapper<?> modularWrapper) {
                    event.setCanceled(modularWrapper.mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton()));
                }
            }
        }
    }


    @SubscribeEvent
    public static void onMouseReleasedEventPre(ScreenEvent.MouseReleasedEvent.Pre event) {
        if (event.getScreen() instanceof AbstractDisplayViewingScreen displayScreen) {
            for (Display display : ReiUtils.getCurrentCategoryDisplays(displayScreen)) {
                Object recipe = DisplayRegistry.getInstance().getDisplayOrigin(display);
                if (recipe instanceof ModularWrapper<?> modularWrapper) {
                    event.setCanceled(modularWrapper.mouseReleased(event.getMouseX(), event.getMouseY(), event.getButton()));
                }
            }
        }
    }


    @SubscribeEvent
    public static void onMouseDragEventPre(ScreenEvent.MouseDragEvent.Pre event) {
        if (event.getScreen() instanceof AbstractDisplayViewingScreen displayScreen) {
            for (Display display : ReiUtils.getCurrentCategoryDisplays(displayScreen)) {
                Object recipe = DisplayRegistry.getInstance().getDisplayOrigin(display);
                if (recipe instanceof ModularWrapper<?> modularWrapper) {
                    if (modularWrapper.mouseDragged(event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY())) {
                        //
                        event.setCanceled(true);
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public static void onMouseScrollEventPre(ScreenEvent.MouseScrollEvent.Pre event) {
        if (event.getScreen() instanceof AbstractDisplayViewingScreen displayScreen) {
            for (Display display : ReiUtils.getCurrentCategoryDisplays(displayScreen)) {
                Object recipe = DisplayRegistry.getInstance().getDisplayOrigin(display);
                if (recipe instanceof ModularWrapper<?> modularWrapper) {
                    if (modularWrapper.mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDelta())) {
                        //
                        event.setCanceled(true);
                    }
                }
            }
        }

    }

    @SubscribeEvent
    public static void onKeyboardKeyPressedEventPre(ScreenEvent.KeyboardKeyPressedEvent event) {
        if (event.getScreen() instanceof AbstractDisplayViewingScreen displayScreen) {
            for (Display display : ReiUtils.getCurrentCategoryDisplays(displayScreen)) {
                Object recipe = DisplayRegistry.getInstance().getDisplayOrigin(display);
                if (recipe instanceof ModularWrapper<?> modularWrapper) {
                    event.setCanceled(modularWrapper.keyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKeyboardKeyReleasedEventEventPre(ScreenEvent.KeyboardKeyReleasedEvent event) {
        if (event.getScreen() instanceof AbstractDisplayViewingScreen displayScreen) {
            for (Display display : ReiUtils.getCurrentCategoryDisplays(displayScreen)) {
                Object recipe = DisplayRegistry.getInstance().getDisplayOrigin(display);
                if (recipe instanceof ModularWrapper<?> modularWrapper) {
                    event.setCanceled(modularWrapper.keyReleased(event.getKeyCode(), event.getScanCode(), event.getModifiers()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKeyboardCharTypedEventEventPre(ScreenEvent.KeyboardCharTypedEvent event) {
        if (event.getScreen() instanceof AbstractDisplayViewingScreen displayScreen) {
            for (Display display : ReiUtils.getCurrentCategoryDisplays(displayScreen)) {
                Object recipe = DisplayRegistry.getInstance().getDisplayOrigin(display);
                if (recipe instanceof ModularWrapper<?> modularWrapper) {
                    event.setCanceled(modularWrapper.charTyped(event.getCodePoint(), event.getModifiers()));
                }
            }
        }
    }
}
