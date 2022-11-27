package com.lowdragmc.lowdraglib.rei;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.impl.client.gui.widget.EntryWidget;

public class ModularSlotEntryWidget extends EntryWidget {

    private final IRecipeIngredientSlot slot;

    public ModularSlotEntryWidget(IRecipeIngredientSlot slot) {
        super(new Rectangle(slot.self().getPosition().x, slot.self().getPosition().y, slot.self().getSize().width, slot.self().getSize().height));
        this.slot = slot;
        if (slot.getIngredientIo() == IngredientIO.INPUT) {
            markIsInput();
        } else if (slot.getIngredientIo() == IngredientIO.OUTPUT) {
            markIsOutput();
        } else {
            unmarkInputOrOutput();
        }
    }


    @Override
    public EntryStack<?> getCurrentEntry() {
        return warpEntryStack();
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {

    }

    @Override
    protected void drawBackground(PoseStack matrices, int mouseX, int mouseY, float delta) {

    }

    @Override
    public boolean containsMouse(double mouseX, double mouseY) {
        return slot.self().isVisible() && slot.self().isMouseOverElement(mouseX, mouseY);
    }

    @Override
    public Rectangle getInnerBounds() {
        var bounds = getBounds();
        return new Rectangle(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2);
    }

    private EntryStack<?> warpEntryStack() {
        var ingredient = slot.getJEIIngredient();
        if (ingredient instanceof EntryStack<?> entryStack) {
            return entryStack;
        }
        return EntryStack.empty();
    }

}
