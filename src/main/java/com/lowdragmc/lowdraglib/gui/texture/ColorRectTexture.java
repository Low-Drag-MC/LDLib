package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberColor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.Color;

@RegisterUI(name = "color_rect_texture")
public class ColorRectTexture extends TransformTexture{

    @Configurable
    @NumberColor
    public int color;

    public ColorRectTexture() {
        this(0x4f0ffddf);
    }

    public ColorRectTexture(int color) {
        this.color = color;
    }

    public ColorRectTexture(Color color) {
        this.color = color.getRGB();
    }

    public ColorRectTexture setColor(int color) {
        this.color = color;
        return this;
    }

    public int getColor() {
        return color;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void drawInternal(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        DrawerHelper.drawSolidRect(stack, (int) x, (int) y, width, height, color);
    }
}
