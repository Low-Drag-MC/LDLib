package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TextBoxWidget extends Widget {

    // config
    public List<String> content;
    public int space = 1;
    public int fontSize = 9;
    public int fontColor = 0xff000000;
    public boolean isShadow = false;
    public boolean isCenter = false;

    private transient List<String> textLines;

    public TextBoxWidget(int x, int y, int width, List<String> content) {
        super(x, y, width, 0);
        this.content = content;
        this.calculate();
    }

    public TextBoxWidget setContent(List<String> content) {
        this.content = content;
        this.calculate();
        return this;
    }

    public TextBoxWidget setSpace(int space) {
        this.space = space;
        this.calculate();
        return this;
    }

    public TextBoxWidget setFontSize(int fontSize) {
        this.fontSize = fontSize;
        this.calculate();
        return this;
    }

    public TextBoxWidget setFontColor(int fontColor) {
        this.fontColor = fontColor;
        this.calculate();
        return this;
    }

    public TextBoxWidget setShadow(boolean shadow) {
        isShadow = shadow;
        this.calculate();
        return this;
    }

    public TextBoxWidget setCenter(boolean center) {
        isCenter = center;
        this.calculate();
        return this;
    }

    protected void calculate() {
        if (isRemote()) {
            this.textLines = new ArrayList<>();
            Font font = Minecraft.getInstance().font;
            this.space = Math.max(space, 0);
            this.fontSize = Math.max(fontSize, 1);
            int wrapWidth = getSize().width * font.lineHeight / fontSize;
            if (content != null) {
                for (String textLine : content) {
                    this.textLines.addAll(font.getSplitter()
                            .splitLines(textLine, wrapWidth, Style.EMPTY)
                            .stream().map(FormattedText::getString).toList());
                }
            }
            this.setSize(new Size(this.getSize().width, this.textLines.size() * (fontSize + space)));
        }
    }

    @Override
    public void drawInBackground(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(matrixStack, mouseX, mouseY, partialTicks);
        if (!textLines.isEmpty()) {
            Position position = getPosition();
            Size size = getSize();
            Font font = Minecraft.getInstance().font;
            float scale = fontSize * 1.0f / font.lineHeight;
            matrixStack.pushPose();
            matrixStack.scale(scale, scale, 1);
            matrixStack.translate(position.x / scale, position.y / scale, 0);
            float x = 0;
            float y = 0;
            float ySpace = font.lineHeight + space / scale;
            for (String textLine : textLines) {
                if (isCenter) {
                    x = (size.width / scale - font.width(textLine)) / 2;
                }
                if (isShadow) {
                    font.draw(matrixStack, textLine, x, y, fontColor);
                } else {
                    font.drawShadow(matrixStack, textLine, x, y, fontColor);
                }
                y += ySpace;
            }
            matrixStack.popPose();
        }
    }
}