package com.lowdragmc.lowdraglib.rei;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModularDisplay<T extends Widget> implements Display {
    protected Supplier<T> widget;
    protected List<EntryIngredient> inputs;
    protected List<EntryIngredient> outputs;
    protected final CategoryIdentifier<?> category;

    public ModularDisplay(Supplier<T> widget, CategoryIdentifier<?> category) {
        this.widget = widget;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.category = category;

        List<Widget> flatVisibleWidgetCollection = getFlatWidgetCollection(widget.get());
        for (Widget w : flatVisibleWidgetCollection) {
            if (w instanceof IRecipeIngredientSlot slot) {
                var io = slot.getIngredientIo();
                Object ingredient = slot.getJEIIngredient();
                if (ingredient instanceof EntryStack<?> entryType) {
                    if (io == IngredientIO.INPUT) {
                        inputs.add(EntryIngredient.builder().add(entryType).build());
                    } else if (io == IngredientIO.OUTPUT) {
                        outputs.add(EntryIngredient.builder().add(entryType).build());
                    }
                }
            }
        }
    }

    public List<Widget> getFlatWidgetCollection(T widgetIn) {
        List<Widget> widgetList = new ArrayList<>();
        if (widgetIn instanceof WidgetGroup group) {
            for (Widget widget : group.widgets) {
                widgetList.add(widget);
                if (widget instanceof WidgetGroup) {
                    widgetList.addAll(((WidgetGroup) widget).getContainedWidgets(true));
                }
            }
        } else {
            widgetList.add(widgetIn);
        }
        return widgetList;
    }

    @OnlyIn(Dist.CLIENT)
    public List<me.shedaniel.rei.api.client.gui.widgets.Widget> createWidget(Rectangle bounds) {
        List<me.shedaniel.rei.api.client.gui.widgets.Widget> list = new ArrayList<>();
        var widget = this.widget.get();
        var modular = new ModularWrapper<>(widget);
        modular.setRecipeWidget(bounds.getX() + 4, bounds.getY() + 4);

        list.add(Widgets.createRecipeBase(bounds));
        list.add(new ModularWrapperWidget(modular));

        List<Widget> flatVisibleWidgetCollection = getFlatWidgetCollection(widget);
        for (Widget w : flatVisibleWidgetCollection) {
            if (w instanceof IRecipeIngredientSlot slot) {
                list.add(new ModularSlotEntryWidget(slot));
            }
        }

        return list;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return outputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return category;
    }
}
