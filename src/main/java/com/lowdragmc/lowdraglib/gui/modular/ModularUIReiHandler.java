package com.lowdragmc.lowdraglib.gui.modular;

import dev.architectury.event.CompoundEventResult;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZonesProvider;
import me.shedaniel.rei.api.client.registry.screen.FocusedStackProvider;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.client.gui.screens.Screen;

import java.util.Collection;

/**
 * @author KilaBash
 * @date 2022/11/27
 * @implNote ModularUIREIHandler
 */
public class ModularUIReiHandler implements ExclusionZonesProvider<ModularUIGuiContainer>, FocusedStackProvider {

    @Override
    public Collection<Rectangle> provide(ModularUIGuiContainer screen) {
        return screen.getGuiExtraAreas().stream().map(rect2 -> new Rectangle(rect2.getX(), rect2.getY(), rect2.getWidth(), rect2.getHeight())).toList();
    }

    @Override
    public CompoundEventResult<EntryStack<?>> provide(Screen screen, Point mouse) {
        if (screen instanceof ModularUIGuiContainer containerScreen) {
            var target = containerScreen.modularUI.mainGroup.getIngredientOverMouse(mouse.getX(), mouse.getY());
            if (target instanceof EntryStack<?> entryStack) {
                return CompoundEventResult.interruptTrue(entryStack);
            } else if (target instanceof EntryIngredient entryStacks && entryStacks.size() > 0) {
                return CompoundEventResult.interruptTrue(entryStacks.get(0));
            }
        }
        return CompoundEventResult.pass();
    }
}
