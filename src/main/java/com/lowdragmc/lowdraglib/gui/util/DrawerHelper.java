package com.lowdragmc.lowdraglib.gui.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class DrawerHelper {

    @OnlyIn(Dist.CLIENT)
    public static void drawFluidTexture(MatrixStack mStack, float xCoord, float yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, float zLevel) {
        float uMin = textureSprite.getU0();
        float uMax = textureSprite.getU1();
        float vMin = textureSprite.getV0();
        float vMax = textureSprite.getV1();
        uMax = uMax - maskRight / 16f * (uMax - uMin);
        vMax = vMax - maskTop / 16f * (vMax - vMin);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        Matrix4f mat = mStack.last().pose();
        buffer.vertex(mat, xCoord, yCoord + 16, zLevel).uv(uMin, vMax).endVertex();
        buffer.vertex(mat, xCoord + 16 - maskRight, yCoord + 16, zLevel).uv(uMax, vMax).endVertex();
        buffer.vertex(mat, xCoord + 16 - maskRight, yCoord + maskTop, zLevel).uv(uMax, vMin).endVertex();
        buffer.vertex(mat, xCoord, yCoord + maskTop, zLevel).uv(uMin, vMin).endVertex();
        tessellator.end();
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawFluidForGui(MatrixStack mStack, FluidStack contents, int tankCapacity, int startX, int startY, int widthT, int heightT) {
        ResourceLocation LOCATION_BLOCKS_TEXTURE = AtlasTexture.LOCATION_BLOCKS;
        FluidAttributes fluid = contents.getFluid().getAttributes();
        ResourceLocation fluidStill = fluid.getStillTexture();
        TextureAtlasSprite fluidStillSprite = Minecraft.getInstance().getTextureAtlas(LOCATION_BLOCKS_TEXTURE).apply(fluidStill);
        int fluidColor = fluid.getColor(contents);
        int scaledAmount = contents.getAmount() * heightT / tankCapacity;
        if (contents.getAmount() > 0 && scaledAmount < 1) {
            scaledAmount = 1;
        }
        if (scaledAmount > heightT || contents.getAmount() == tankCapacity) {
            scaledAmount = heightT;
        }
        RenderSystem.enableBlend();
        Minecraft.getInstance().textureManager.bind(LOCATION_BLOCKS_TEXTURE);

        int i = (fluidColor & 0xFF0000) >> 16;
        int j = (fluidColor & 0xFF00) >> 8;
        int k = (fluidColor & 0xFF);
        RenderSystem.color4f(i / 255.0f, j / 255.0f, k / 255.0f, 1);

        final int xTileCount = widthT / 16;
        final int xRemainder = widthT - xTileCount * 16;
        final int yTileCount = scaledAmount / 16;
        final int yRemainder = scaledAmount - yTileCount * 16;

        final int yStart = startY + heightT;

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int width = xTile == xTileCount ? xRemainder : 16;
                int height = yTile == yTileCount ? yRemainder : 16;
                int x = startX + xTile * 16;
                int y = yStart - (yTile + 1) * 16;
                if (width > 0 && height > 0) {
                    int maskTop = 16 - height;
                    int maskRight = 16 - width;

                    drawFluidTexture(mStack, x, y, fluidStillSprite, maskTop, maskRight, 0);
                }
            }
        }
        RenderSystem.enableBlend();
    }


    @OnlyIn(Dist.CLIENT)
    public static void drawHoveringText(MatrixStack mStack, @Nonnull final ItemStack stack, List<? extends ITextProperties> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth) {
        Minecraft mc = Minecraft.getInstance();
        FontRenderer fontRenderer = stack.getItem().getFontRenderer(stack);
        GuiUtils.drawHoveringText(stack, mStack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, fontRenderer == null ?  mc.font : fontRenderer);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawBorder(MatrixStack mStack, int x, int y, int width, int height, int color, int border) {
        drawSolidRect(mStack,x - border, y - border, width + 2 * border, border, color);
        drawSolidRect(mStack,x - border, y + height, width + 2 * border, border, color);
        drawSolidRect(mStack,x - border, y, border, height, color);
        drawSolidRect(mStack,x + width, y, border, height, color);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawStringSized(MatrixStack mStack, String text, float x, float y, int color, boolean dropShadow, float scale, boolean center) {
        mStack.pushPose();
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        double scaledTextWidth = center ? fontRenderer.width(text) * scale : 0.0;
        mStack.translate(x - scaledTextWidth / 2.0, y, 0.0f);
        mStack.scale(scale, scale, scale);
        if (dropShadow) {
            fontRenderer.drawShadow(mStack, text, 0, 0, color);
        } else {
            fontRenderer.draw(mStack, text, 0, 0, color);
        }
        mStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawStringFixedCorner(MatrixStack mStack, String text, float x, float y, int color, boolean dropShadow, float scale) {
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        float scaledWidth = fontRenderer.width(text) * scale;
        float scaledHeight = fontRenderer.lineHeight * scale;
        drawStringSized(mStack, text, x - scaledWidth, y - scaledHeight, color, dropShadow, scale, false);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawText(MatrixStack mStack, String text, float x, float y, float scale, int color) {
        drawText(mStack, text, x, y, scale, color, false);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawText(MatrixStack mStack, String text, float x, float y, float scale, int color, boolean shadow) {
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        RenderSystem.disableBlend();
        mStack.pushPose();
        mStack.scale(scale, scale, 0f);
        float sf = 1 / scale;
        if (shadow) {
            fontRenderer.drawShadow(mStack, text, x * sf, y * sf, color);
        } else {
            fontRenderer.draw(mStack, text, x * sf, y * sf, color);
        }
        mStack.popPose();
        RenderSystem.enableBlend();
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawItemStack(MatrixStack mStack, ItemStack itemStack, int x, int y, @Nullable String altTxt) {

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(mStack.last().pose());
//        RenderSystem.translatef(0, 0, -400);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableRescaleNormal();
        RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F); // light map
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();

        itemRenderer.blitOffset = 200f;
        FontRenderer font = itemStack.getItem().getFontRenderer(itemStack);
        itemRenderer.renderAndDecorateItem(itemStack, x, y);
        itemRenderer.renderGuiItemDecorations(font == null ? mc.font : font, itemStack, x, y, altTxt);
        itemRenderer.blitOffset = 0;

        RenderSystem.depthMask(false);
        RenderSystem.disableRescaleNormal();
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.popMatrix();
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.enableAlphaTest();
    }

    @OnlyIn(Dist.CLIENT)
    public static List<ITextComponent> getItemToolTip(ItemStack itemStack) {
        Minecraft mc = Minecraft.getInstance();
        return itemStack.getTooltipLines(mc.player, mc.options.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawSolidRect(MatrixStack mStack, int x, int y, int width, int height, int color) {
        AbstractGui.fill(mStack, x, y, x + width, y + height, color);
        RenderSystem.enableBlend();
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawRectShadow(MatrixStack mStack, int x, int y, int width, int height, int distance) {
        drawGradientRect(mStack, x + distance, y + height, width - distance, distance, 0x4f000000, 0, false);
        drawGradientRect(mStack, x + width, y + distance, distance, height - distance, 0x4f000000, 0, true);

        float startAlpha = (float) (0x4f) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        x += width;
        y += height;
        Matrix4f mat = mStack.last().pose();
        buffer.vertex(mat, x, y, 0).color(0, 0, 0, startAlpha).endVertex();
        buffer.vertex(mat, x, y + distance, 0).color(0, 0, 0, 0).endVertex();
        buffer.vertex(mat, x + distance, y + distance, 0).color(0, 0, 0, 0).endVertex();

        buffer.vertex(mat, x, y, 0).color(0, 0, 0, startAlpha).endVertex();
        buffer.vertex(mat, x + distance, y + distance, 0).color(0, 0, 0, 0).endVertex();
        buffer.vertex(mat, x + distance, y, 0).color(0, 0, 0, 0).endVertex();
        tessellator.end();
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawGradientRect(MatrixStack mStack, int x, int y, int width, int height, int startColor, int endColor) {
        drawGradientRect(mStack, x, y, width, height, startColor, endColor, false);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawGradientRect(MatrixStack mStack, float x, float y, float width, float height, int startColor, int endColor, boolean horizontal) {
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        Matrix4f mat = mStack.last().pose();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        if (horizontal) {
            buffer.vertex(mat,x + width, y, 0).color(endRed, endGreen, endBlue, endAlpha).endVertex();
            buffer.vertex(mat,x, y, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
            buffer.vertex(mat,x, y + height, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
            buffer.vertex(mat,x + width, y + height, 0).color(endRed, endGreen, endBlue, endAlpha).endVertex();
            tessellator.end();
        } else {
            buffer.vertex(mat,x + width, y, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
            buffer.vertex(mat,x, y, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
            buffer.vertex(mat,x, y + height, 0).color(endRed, endGreen, endBlue, endAlpha).endVertex();
            buffer.vertex(mat,x + width, y + height, 0).color(endRed, endGreen, endBlue, endAlpha).endVertex();
            tessellator.end();
        }
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    @OnlyIn(Dist.CLIENT)
    public static void setColor(int color) { // ARGB
        RenderSystem.color4f((color >> 16 & 255) / 255.0F,
                (color >> 8 & 255) / 255.0F,
                (color & 255) / 255.0F,
                (color >> 24 & 255) / 255.0F);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawCircle(MatrixStack mStack, float x, float y, float r, int color, int segments) {
        if (color == 0) return;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Matrix4f mat = mStack.last().pose();
        setColor(color);
        bufferbuilder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION);
        for (int i = 0; i < segments; i++) {
            bufferbuilder.vertex(mat, x + r * (float) Math.cos(-2 * Math.PI * i / segments), y + r * (float) Math.sin(-2 * Math.PI * i / segments), 0).endVertex();
        }
        tessellator.end();
        RenderSystem.enableTexture();
        RenderSystem.color4f(1,1,1,1);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawSector(MatrixStack mStack, float x, float y, float r, int color, int segments, int from, int to) {
        if (from > to || from < 0 || color == 0) return;
        if(to > segments) to = segments;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        setColor(color);
        Matrix4f mat = mStack.last().pose();
        bufferbuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);
        for (int i = from; i < to; i++) {
            bufferbuilder.vertex(mat, x + r * (float) Math.cos(-2 * Math.PI * i / segments), y + r * (float) Math.sin(-2 * Math.PI * i / segments), 0).endVertex();
            bufferbuilder.vertex(mat,x + r * (float) Math.cos(-2 * Math.PI * (i + 1) / segments), y + r * (float) Math.sin(-2 * Math.PI * (i + 1) / segments), 0).endVertex();
            bufferbuilder.vertex(mat,x, y, 0).endVertex();
        }
        tessellator.end();
        RenderSystem.enableTexture();
        RenderSystem.color4f(1, 1, 1, 1);
    }

    public static void drawTorus(MatrixStack mStack, float x, float y, float outer, float inner, int color, int segments, int from, int to) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        setColor(color);
        Matrix4f mat = mStack.last().pose();
        bufferbuilder.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION);
        for (int i = from; i <= to; i++) {
            float angle = (i / (float) segments) * 3.14159f * 2.0f;
            bufferbuilder.vertex(mat,x + inner * (float) Math.cos(-angle), y + inner * (float) Math.sin(-angle), 0).endVertex();
            bufferbuilder.vertex(mat,x + outer * (float) Math.cos(-angle), y + outer * (float) Math.sin(-angle), 0).endVertex();
        }
        tessellator.end();
        RenderSystem.enableTexture();
        RenderSystem.color4f(1, 1, 1, 1);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawLines(MatrixStack mStack, List<Vector2f> points, int startColor, int endColor, float width) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.lineWidth(width);
        Matrix4f mat = mStack.last().pose();
        if (startColor == endColor) {
            setColor(startColor);
            bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
            for (Vector2f point : points) {
                bufferbuilder.vertex(mat, point.x, point.y, 0).endVertex();
            }
        } else {
            float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
            float startRed = (float) (startColor >> 16 & 255) / 255.0F;
            float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
            float startBlue = (float) (startColor & 255) / 255.0F;
            float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
            float endRed = (float) (endColor >> 16 & 255) / 255.0F;
            float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
            float endBlue = (float) (endColor & 255) / 255.0F;
            bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            int size = points.size();

            for (int i = 0; i < size; i++) {
                float p = i * 1.0f / size;
                bufferbuilder.vertex(mat, points.get(i).x, points.get(i).y, 0)
                        .color(startRed + (endRed - startRed) * p,
                                startGreen + (endGreen - startGreen) * p,
                                startBlue + (endBlue - startBlue) * p,
                                startAlpha + (endAlpha - startAlpha) * p)
                        .endVertex();
            }
        }
        tessellator.end();
        RenderSystem.enableTexture();
        RenderSystem.color4f(1, 1, 1, 1);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawTextureRect(MatrixStack mStack, float x, float y, float width, float height) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        Matrix4f mat = mStack.last().pose();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(mat, x, y + height, 0).uv(0, 0).endVertex();
        buffer.vertex(mat, x + width, y + height, 0).uv(1, 0).endVertex();
        buffer.vertex(mat, x + width, y, 0).uv(1, 1).endVertex();
        buffer.vertex(mat, x, y, 0).uv(0, 1).endVertex();
        tessellator.end();
    }

}
