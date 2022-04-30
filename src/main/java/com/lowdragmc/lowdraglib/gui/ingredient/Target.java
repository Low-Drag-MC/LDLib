package com.lowdragmc.lowdraglib.gui.ingredient;

import net.minecraft.client.renderer.Rectangle2d;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public interface Target extends Consumer<Object> {
    @Nonnull
    Rectangle2d getArea();
    void accept(Object var1);
}