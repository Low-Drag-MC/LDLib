package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;


import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TextTexture implements IGuiTexture{
    public String text;
    public int color;
    public int backgroundColor;
    public int width;
    public boolean dropShadow;
    public TextType type;
    public Supplier<String> supplier;
    @OnlyIn(Dist.CLIENT)
    private List<String> texts;

    public TextTexture(String text, int color) {
        this.color = color;
        this.type = TextType.NORMAL;
        if (FMLEnvironment.dist == Dist.CLIENT) {
            this.text = I18n.get(text);
            texts = Collections.singletonList(this.text);
        }
    }

    public TextTexture(String text) {
        this(text, -1);
        setDropShadow(true);
    }

    public TextTexture setSupplier(Supplier<String> supplier) {
        this.supplier = supplier;
        return this;
    }

    @Override
    public void updateTick() {
        if (supplier != null) {
            updateText(supplier.get());
        }
    }

    public void updateText(String text) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            this.text = I18n.get(text);
            texts = Collections.singletonList(this.text);
            setWidth(this.width);
        }
    }

    public TextTexture setBackgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    public TextTexture setColor(int color) {
        this.color = color;
        return this;
    }

    public TextTexture setDropShadow(boolean dropShadow) {
        this.dropShadow = dropShadow;
        return this;
    }

    public TextTexture setWidth(int width) {
        this.width = width;
        if (FMLEnvironment.dist == Dist.CLIENT) {
            if (this.width > 0) {
                texts = Minecraft.getInstance()
                        .font.getSplitter()
                        .splitLines(text, width, Style.EMPTY)
                        .stream().map(ITextProperties::getString)
                        .collect(Collectors.toList());
                if (texts.size() == 0) {
                    texts = Collections.singletonList(text);
                }
            } else {
                texts = Collections.singletonList(text);
            }
        }
        return this;
    }

    public TextTexture setType(TextType type) {
        this.type = type;
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(MatrixStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        if (backgroundColor != 0) {
            DrawerHelper.drawSolidRect(stack, (int) x, (int) y, width, height, backgroundColor);
        }
        stack.pushPose();
        stack.translate(0, 0, 400);
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        int textH = fontRenderer.lineHeight;
        if (type == TextType.NORMAL) {
            textH *= texts.size();
            for (int i = 0; i < texts.size(); i++) {
                String resultText = texts.get(i);
                int textW = fontRenderer.width(resultText);
                float _x = x + (width - textW) / 2f;
                float _y = y + (height - textH) / 2f + i * fontRenderer.lineHeight;
                if (dropShadow) {
                    fontRenderer.drawShadow(stack, resultText, _x, _y, color);
                } else {
                    fontRenderer.draw(stack, resultText, _x, _y, color);
                }
            }
        } else if (type == TextType.HIDE) {
            String resultText = texts.get(0) + (texts.size() > 1 ? ".." : "");
            int textW = fontRenderer.width(resultText);
            float _x = x + (width - textW) / 2f;
            float _y = y + (height - textH) / 2f;
            if (dropShadow) {
                fontRenderer.drawShadow(stack, resultText, _x, _y, color);
            } else {
                fontRenderer.draw(stack, resultText, _x, _y, color);
            }
        } else if (type == TextType.ROLL) {
            int i = 0;
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                i = (int) (Math.abs(System.currentTimeMillis() / 1000) % texts.size());
            }
            String resultText = texts.get(i);
            int textW = fontRenderer.width(resultText);
            float _x = x + (width - textW) / 2f;
            float _y = y + (height - textH) / 2f;
            if (dropShadow) {
                fontRenderer.drawShadow(stack, resultText, _x, _y, color);
            } else {
                fontRenderer.draw(stack, resultText, _x, _y, color);
            }
        } else if (type == TextType.LEFT) {
            textH *= texts.size();
            for (int i = 0; i < texts.size(); i++) {
                String resultText = texts.get(i);
                float _y = y + (height - textH) / 2f + i * fontRenderer.lineHeight;
                if (dropShadow) {
                    fontRenderer.drawShadow(stack, resultText, x, _y, color);
                } else {
                    fontRenderer.draw(stack, resultText, x, _y, color);
                }
            }
        } else if (type == TextType.RIGHT) {
            textH *= texts.size();
            for (int i = 0; i < texts.size(); i++) {
                String resultText = texts.get(i);
                int textW = fontRenderer.width(resultText);
                float _y = y + (height - textH) / 2f + i * fontRenderer.lineHeight;
                if (dropShadow) {
                    fontRenderer.drawShadow(stack, resultText, x + width - textW, _y, color);
                } else {
                    fontRenderer.draw(stack, resultText, x + width - textW, _y, color);
                }
            }
        }
        stack.popPose();
        GlStateManager._color4f(1, 1, 1, 1);
    }

    public enum TextType{
        NORMAL,
        HIDE,
        ROLL,
        LEFT,
        RIGHT
    }
}
