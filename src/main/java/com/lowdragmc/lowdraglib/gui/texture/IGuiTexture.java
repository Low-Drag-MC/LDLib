package com.lowdragmc.lowdraglib.gui.texture;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IGuiTexture {

    @Deprecated
    default void draw(MatrixStack stack, float x, float y, int width, int height) {
        draw(stack, 0, 0, x, y, width, height);
    }
    
    void draw(MatrixStack stack, int mouseX, int mouseY, float x, float y, int width, int height);
    
    default void updateTick() { }
    
    IGuiTexture EMPTY = (stack, mouseX, mouseY, x, y, width, height) -> {};

    default void drawSubArea(MatrixStack stack, float x, float y, int width, int height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        draw(stack, x, y, width, height);
    }
}
