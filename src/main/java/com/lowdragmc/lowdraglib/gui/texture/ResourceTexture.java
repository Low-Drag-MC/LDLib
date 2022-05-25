package com.lowdragmc.lowdraglib.gui.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX;

public class ResourceTexture implements IGuiTexture {

    public final ResourceLocation imageLocation;

    public final float offsetX;
    public final float offsetY;

    public final float imageWidth;
    public final float imageHeight;


    public ResourceTexture(ResourceLocation imageLocation, float offsetX, float offsetY, float width, float height) {
        this.imageLocation = imageLocation;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    public ResourceTexture(String imageLocation) {
        this(new ResourceLocation(imageLocation), 0, 0, 1, 1);
    }

    public ResourceTexture getSubTexture(float offsetX, float offsetY, float width, float height) {
        return new ResourceTexture(imageLocation,
                this.offsetX + (imageWidth * offsetX),
                this.offsetY + (imageHeight * offsetY),
                this.imageWidth * width,
                this.imageHeight * height);
    }

    public ResourceTexture getSubTexture(double offsetX, double offsetY, double width, double height) {
        return new ResourceTexture(imageLocation,
                this.offsetX + (float)(imageWidth * offsetX),
                this.offsetY + (float)(imageHeight * offsetY),
                this.imageWidth * (float) width,
                this.imageHeight * (float)height);
    }

    @OnlyIn(Dist.CLIENT)
    public void draw(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        drawSubArea(stack, x, y, width, height, 0, 0, 1, 1);
    }
    
    @OnlyIn(Dist.CLIENT)
    public void drawSubArea(PoseStack stack, float x, float y, int width, int height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        //sub area is just different width and height
        float imageU = this.offsetX + (this.imageWidth * drawnU);
        float imageV = this.offsetY + (this.imageHeight * drawnV);
        float imageWidth = this.imageWidth * drawnWidth;
        float imageHeight = this.imageHeight * drawnHeight;
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, imageLocation);
        Matrix4f matrix4f = stack.last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, POSITION_TEX);
        bufferbuilder.vertex(matrix4f, x, y + height, 0).uv(imageU, imageV + imageHeight).endVertex();
        bufferbuilder.vertex(matrix4f, x + width, y + height, 0).uv(imageU + imageWidth, imageV + imageHeight).endVertex();
        bufferbuilder.vertex(matrix4f, x + width, y, 0).uv(imageU + imageWidth, imageV).endVertex();
        bufferbuilder.vertex(matrix4f, x, y, 0).uv(imageU, imageV).endVertex();
        tessellator.end();
    }

}
