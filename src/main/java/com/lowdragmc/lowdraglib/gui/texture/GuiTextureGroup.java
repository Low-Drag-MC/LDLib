package com.lowdragmc.lowdraglib.gui.texture;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GuiTextureGroup implements IGuiTexture{
    public IGuiTexture[] textures;

    public GuiTextureGroup(IGuiTexture... textures) {
        this.textures = textures;
    }

    public GuiTextureGroup setTextures(IGuiTexture[] textures) {
        this.textures = textures;
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(MatrixStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        for (IGuiTexture texture : textures) {
            texture.draw(stack, mouseX,mouseY,  x, y, width, height);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void updateTick() {
        for (IGuiTexture texture : textures) {
            texture.updateTick();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawSubArea(MatrixStack stack, float x, float y, int width, int height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        for (IGuiTexture texture : textures) {
            texture.drawSubArea(stack, x, y, width, height, drawnU, drawnV, drawnWidth, drawnHeight);
        }
    }
}
