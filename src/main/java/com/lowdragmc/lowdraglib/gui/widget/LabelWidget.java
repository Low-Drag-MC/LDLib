package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigSetter;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberColor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

@Configurable(name = "ldlib.gui.editor.register.widget.label", collapse = false)
@RegisterUI(name = "label")
public class LabelWidget extends Widget implements IConfigurableWidget {

    protected Supplier<String> textSupplier;

    @Configurable(name = "ldlib.gui.editor.name.text")
    private String lastTextValue = "";

    @Configurable
    @NumberColor
    private int color;

    @Configurable
    private boolean dropShadow;

    public LabelWidget() {
        this(0, 0, "label");
    }

    public LabelWidget(int xPosition, int yPosition, String text) {
        this(xPosition, yPosition, ()->text);
        setDropShadow(true);
        setTextColor(-1);
    }

    public LabelWidget(int xPosition, int yPosition, Supplier<String> text) {
        super(new Position(xPosition, yPosition), new Size(10, 10));
        this.textSupplier = text;
        if (isRemote()) {
            updateSize();
        }
    }

    @ConfigSetter(field = "lastTextValue")
    public void setText(String text) {
        textSupplier = () -> text;
    }

    public LabelWidget setTextColor(int color) {
        this.color = color;
        return this;
    }

    public LabelWidget setDropShadow(boolean dropShadow) {
        this.dropShadow = dropShadow;
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    private void updateSize() {
        Font fontRenderer = Minecraft.getInstance().font;
        setSize(new Size(fontRenderer.width(LocalizationUtils.format(lastTextValue)), fontRenderer.lineHeight));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
        String latest = textSupplier.get();
        if (!latest.equals(lastTextValue)) {
            this.lastTextValue = latest;
            updateSize();
        }
        String suppliedText = LocalizationUtils.format(latest);
        String[] split = suppliedText.split("\n");
        Font fontRenderer = Minecraft.getInstance().font;
        Position position = getPosition();
        for (int i = 0; i < split.length; i++) {
            int y = position.y + (i * (fontRenderer.lineHeight + 2));
            if (dropShadow) {
                fontRenderer.drawShadow(poseStack, split[i], position.x, y, color);
            } else {
                fontRenderer.draw(poseStack, split[i], position.x, y, color);
            }
        }
    }

}
