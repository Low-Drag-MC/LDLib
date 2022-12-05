package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@RegisterUI(name = "group_texture")
public class GuiTextureGroup extends TransformTexture{

    @Configurable(collapse = false)
    public IGuiTexture[] textures;

    public GuiTextureGroup() {
        this(ResourceBorderTexture.BORDERED_BACKGROUND, new ResourceTexture());
    }

    public GuiTextureGroup(IGuiTexture... textures) {
        this.textures = textures;
    }

    public GuiTextureGroup setTextures(IGuiTexture[] textures) {
        this.textures = textures;
        return this;
    }

    @Override
    public GuiTextureGroup setColor(int color) {
        for (IGuiTexture texture : textures) {
            texture.setColor(color);
        }
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void drawInternal(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
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
    protected void drawSubAreaInternal(PoseStack stack, float x, float y, int width, int height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        for (IGuiTexture texture : textures) {
            texture.drawSubArea(stack, x, y, width, height, drawnU, drawnV, drawnWidth, drawnHeight);
        }
    }
}
