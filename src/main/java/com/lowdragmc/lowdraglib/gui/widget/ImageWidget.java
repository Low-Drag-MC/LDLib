package com.lowdragmc.lowdraglib.gui.widget;


import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class ImageWidget extends Widget {

    private int border;
    private int borderColor;

    public ImageWidget(int xPosition, int yPosition, int width, int height) {
        super(xPosition, yPosition, width, height);
    }

    public ImageWidget(int xPosition, int yPosition, int width, int height, IGuiTexture area) {
        this(xPosition, yPosition, width, height);
        setImage(area);
    }

    public ImageWidget setImage(IGuiTexture area) {
        setBackground(area);
        return this;
    }

    public IGuiTexture getImage() {
        return backgroundTexture;
    }

    public ImageWidget setBorder(int border, int color) {
        this.border = border;
        this.borderColor = color;
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(matrixStack, mouseX, mouseY, partialTicks);
        Position position = getPosition();
        Size size = getSize();
        if (border > 0) {
            DrawerHelper.drawBorder(matrixStack, position.x, position.y, size.width, size.height, borderColor, border);
        }
    }
}

