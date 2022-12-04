package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote WidgetDraggingTexture
 */
public class WidgetDraggingTexture implements IGuiTexture{
    private final Widget widget;
    private final int centerX;
    private final int centerY;

    public WidgetDraggingTexture(Widget widget) {
        this.widget = widget;
        this.centerX = widget.getPosition().x + widget.getSize().width / 2;
        this.centerY = widget.getPosition().y + widget.getSize().height / 2;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        int xOffset = mouseX - this.centerX;
        int yOffset = mouseY - this.centerY;
        float particleTick = Minecraft.getInstance().getFrameTime();
        stack.pushPose();
        stack.translate(xOffset, yOffset, 0 );
        widget.drawInBackground(stack, this.centerX, this.centerY, particleTick);
        widget.drawInForeground(stack, this.centerX, this.centerY, particleTick);
        stack.popPose();
    }

}
