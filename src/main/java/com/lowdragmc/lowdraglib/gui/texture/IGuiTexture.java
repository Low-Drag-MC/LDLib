package com.lowdragmc.lowdraglib.gui.texture;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IGuiTexture {

    @OnlyIn(Dist.CLIENT)
    @Deprecated
    default void draw(MatrixStack stack, float x, float y, int width, int height) {
        draw(stack, 0, 0, x, y, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    void draw(MatrixStack stack, int mouseX, int mouseY, float x, float y, int width, int height);
    
    default void updateTick() { }
    
    IGuiTexture EMPTY = new IGuiTexture() {
        @OnlyIn(Dist.CLIENT)
        @Override
        public void draw(MatrixStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {

        }
    };

    @OnlyIn(Dist.CLIENT)
    default void drawSubArea(MatrixStack stack, float x, float y, int width, int height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        draw(stack, x, y, width, height);
    }
}
