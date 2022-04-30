package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.mojang.blaze3d.matrix.MatrixStack;

import java.awt.Color;

public class ColorBorderTexture implements IGuiTexture{
    public int color;
    public int border;

    public ColorBorderTexture(int border, int color) {
        this.color = color;
        this.border = border;
    }

    public ColorBorderTexture(int border, Color color) {
        this.color = color.getRGB();
        this.border = border;
    }

    public ColorBorderTexture setBorder(int border) {
        this.border = border;
        return this;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    @Override
    public void draw(MatrixStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        DrawerHelper.drawBorder(stack, (int)x, (int)y, width, height, color, border);
    }
}
