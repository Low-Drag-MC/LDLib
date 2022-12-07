package com.lowdragmc.lowdraglib.gui.widget;


import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberColor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@Configurable(name = "ldlib.gui.editor.register.widget.image", collapse = false)
@RegisterUI(name = "image")
public class ImageWidget extends Widget implements IConfigurableWidget {

    @Configurable(name = "ldlib.gui.editor.name.border")
    @NumberRange(range = {-100, 100})
    private int border;
    @Configurable(name = "ldlib.gui.editor.name.border_color")
    @NumberColor
    private int borderColor = -1;

    public ImageWidget() {
        this(0, 0, 50, 50, new ResourceTexture());
    }

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

