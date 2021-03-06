package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class LabelWidget extends Widget {

    protected final Supplier<String> textSupplier;
    private String lastTextValue = "";
    private int color;
    private boolean drop;

    public LabelWidget(int xPosition, int yPosition, String text) {
        this(xPosition, yPosition, ()->text);
        setDrop(true);
        setTextColor(-1);
    }

    public LabelWidget(int xPosition, int yPosition, Supplier<String> text) {
        super(new Position(xPosition, yPosition), Size.ZERO);
        this.textSupplier = text;
    }

    public LabelWidget setTextColor(int color) {
        this.color = color;
        return this;
    }

    public LabelWidget setDrop(boolean drop) {
        this.drop = drop;
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    private void updateSize() {
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        String resultText = lastTextValue;
        setSize(new Size(fontRenderer.width(resultText), fontRenderer.lineHeight));
        if (uiAccess != null) {
            uiAccess.notifySizeChange();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(matrixStack, mouseX, mouseY, partialTicks);
        String suppliedText = I18n.get(textSupplier.get());
        if (!suppliedText.equals(lastTextValue)) {
            this.lastTextValue = suppliedText;
            updateSize();
        }
        String[] split = suppliedText.split("\n");
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        Position position = getPosition();
        for (int i = 0; i < split.length; i++) {
            int y = position.y + (i * (fontRenderer.lineHeight + 2));
            if (drop) {
                fontRenderer.drawShadow(matrixStack, split[i], position.x, y, color);
            } else {
                fontRenderer.draw(matrixStack, split[i], position.x, y, color);
            }
        }
    }

}
