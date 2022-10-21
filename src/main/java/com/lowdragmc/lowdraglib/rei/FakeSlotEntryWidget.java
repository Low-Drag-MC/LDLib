package com.lowdragmc.lowdraglib.rei;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryDefinition;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.impl.client.gui.widget.EntryWidget;
import me.shedaniel.rei.jeicompat.JEIPluginDetector;
import net.minecraft.world.item.ItemStack;

public class FakeSlotEntryWidget extends EntryWidget {

    private final Widget widget;
    private Rectangle bounds;

    public FakeSlotEntryWidget(Widget widget) {
        super(new Rectangle(widget.getPosition().x, widget.getPosition().y, widget.getSize().width, widget.getSize().height));
        this.widget = widget;
        this.bounds = super.getBounds();
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
        return widget.isVisible() && widget.isMouseOverElement(mouseX, mouseY);
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public Rectangle getInnerBounds() {
        return new Rectangle(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2);
    }

    public void onPositionUpdate() {
        this.bounds = new Rectangle(
                widget.getPosition().x,
                widget.getPosition().y,
                widget.getSize().width,
                widget.getSize().height
        );
    }

    @SuppressWarnings("unchecked")
    private <T> EntryStack<?> warpEntryStack() {

        if (widget instanceof IRecipeIngredientSlot slot) {
            T ingredient = (T) slot.getJEIIngredient();
            return JEIPluginDetector.unwrapStack(ingredient, (EntryDefinition<T>) JEIPluginDetector.unwrapDefinition(ingredient));
        }
        if (widget instanceof SlotWidget slotWidget) {
            ItemStack stack = slotWidget.getRealStack(slotWidget.getHandle().getItem());
            return EntryStack.of((EntryType<ItemStack>) JEIPluginDetector.unwrapType(stack), stack);
        }

        return EntryStack.empty();
    }

}
