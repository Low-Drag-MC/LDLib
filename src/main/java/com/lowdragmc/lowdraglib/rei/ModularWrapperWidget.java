package com.lowdragmc.lowdraglib.rei;

import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ModularWrapperWidget extends WidgetWithBounds {

    private final ModularWrapper<?> modularWrapper;
    private Rectangle bounds;
    private int left;
    private int top;

    public ModularWrapperWidget(ModularWrapper<?> modularWrapper, Rectangle bounds) {
        this.modularWrapper = modularWrapper;
        this.bounds = bounds;
        left = bounds.x;
        top = bounds.y;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        modularWrapper.setRecipeWidget(left + 4, top + 4);
        poseStack.pushPose();
        {
            poseStack.translate(left + 4, top + 4, 0);
            modularWrapper.draw(poseStack, mouseX - left - 4, mouseY - top - 4);
        }
        poseStack.popPose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return Collections.emptyList();
    }
}
