package com.lowdragmc.lowdraglib.gui.modular;

import com.lowdragmc.lowdraglib.gui.ingredient.Target;
import dev.architectury.event.CompoundEventResult;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZonesProvider;
import me.shedaniel.rei.api.client.registry.screen.FocusedStackProvider;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
            }
        }
        return CompoundEventResult.pass();
    }
}
