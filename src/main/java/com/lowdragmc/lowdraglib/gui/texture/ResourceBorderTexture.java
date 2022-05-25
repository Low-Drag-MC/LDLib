package com.lowdragmc.lowdraglib.gui.texture;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ResourceBorderTexture extends ResourceTexture {
    public static final ResourceBorderTexture BORDERED_BACKGROUND = new ResourceBorderTexture("ldlib:textures/gui/bordered_background.png", 195, 136, 4, 4);
    public static final ResourceBorderTexture BORDERED_BACKGROUND_BLUE = new ResourceBorderTexture("ldlib:textures/gui/bordered_background_blue.png", 195, 136, 4, 4);
    public static final ResourceBorderTexture BUTTON_COMMON = new ResourceBorderTexture("ldlib:textures/gui/button_common.png", 198, 18, 1, 1);
    public static final ResourceBorderTexture BAR = new ResourceBorderTexture("ldlib:textures/gui/button_common.png", 180, 20, 1, 1);

    public final int pixelCornerWidth;
    public final int pixelCornerHeight;
    public final int pixelImageWidth;
    public final int pixelImageHeight;

    public ResourceBorderTexture(String imageLocation, int imageWidth, int imageHeight, int cornerWidth, int cornerHeight) {
        super(imageLocation);
        this.pixelImageWidth = imageWidth;
        this.pixelImageHeight = imageHeight;
        this.pixelCornerWidth = cornerWidth;
        this.pixelCornerHeight = cornerHeight;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawSubArea(PoseStack stack, float x, float y, int width, int height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        //compute relative sizes
        float cornerWidth = pixelCornerWidth * 1f / pixelImageWidth;
        float cornerHeight = pixelCornerHeight * 1f / pixelImageHeight;
        //draw up corners
        super.drawSubArea(stack, x, y, pixelCornerWidth, pixelCornerHeight, 0, 0, cornerWidth, cornerHeight);
        super.drawSubArea(stack, x + width - pixelCornerWidth, y, pixelCornerWidth, pixelCornerHeight, 1 - cornerWidth, 0, cornerWidth, cornerHeight);
        //draw down corners
        super.drawSubArea(stack, x, y + height - pixelCornerHeight, pixelCornerWidth, pixelCornerHeight, 0, 1 - cornerHeight, cornerWidth, cornerHeight);
        super.drawSubArea(stack, x + width - pixelCornerWidth, y + height - pixelCornerHeight, pixelCornerWidth, pixelCornerHeight, 1 - cornerWidth, 1 - cornerHeight, cornerWidth, cornerHeight);
        //draw horizontal connections
        super.drawSubArea(stack, x + pixelCornerWidth, y, width - 2 * pixelCornerWidth, pixelCornerHeight,
                cornerWidth, 0, 1 - 2 * cornerWidth, cornerHeight);
        super.drawSubArea(stack, x + pixelCornerWidth, y + height - pixelCornerHeight, width - 2 * pixelCornerWidth, pixelCornerHeight,
                cornerWidth, 1 - cornerHeight, 1 - 2 * cornerWidth, cornerHeight);
        //draw vertical connections
        super.drawSubArea(stack, x, y + pixelCornerHeight, pixelCornerWidth, height - 2 * pixelCornerHeight,
                0, cornerHeight, cornerWidth, 1 - 2 * cornerHeight);
        super.drawSubArea(stack, x + width - pixelCornerWidth, y + pixelCornerHeight, pixelCornerWidth, height - 2 * pixelCornerHeight,
                1 - cornerWidth, cornerHeight, cornerWidth, 1 - 2 * cornerHeight);
        //draw central body
        super.drawSubArea(stack, x + pixelCornerWidth, y + pixelCornerHeight,
                width - 2 * pixelCornerWidth, height - 2 * pixelCornerHeight,
                cornerWidth, cornerHeight, 1 - 2 * cornerWidth, 1 - 2 * cornerHeight);
    }
}
