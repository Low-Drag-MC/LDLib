package com.lowdragmc.lowdraglib.jei;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.gui.recipes.RecipeLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ModularWrapper<T extends Widget> extends ModularUIGuiContainer {
    protected T widget;
    public ModularWrapper(T widget) {
        super(new ModularUI(widget.getSize().width, widget.getSize().height, IUIHolder.EMPTY, Minecraft.getInstance().player).widget(widget),  -1);
        modularUI.initWidgets();
        this.minecraft = Minecraft.getInstance();
        this.itemRenderer = minecraft.getItemRenderer();
        this.font = minecraft.font;
        this.widget = widget;
    }

    private static int lastTick;
    private RecipeLayout<?> layout;

    public T getWidget() {
        return widget;
    }

    public void setRecipeLayout(RecipeLayout<?> layout) {
        modularUI.initWidgets();
        this.layout = layout;
        this.width = minecraft.getWindow().getGuiScaledWidth();
        this.height = minecraft.getWindow().getGuiScaledHeight();
        modularUI.updateScreenSize(this.width, this.height);
        Position displayOffset = new Position(modularUI.getGuiLeft(), layout.getPosY());
        modularUI.mainGroup.setParentPosition(displayOffset);
//        this.menu.slots.clear();
    }

    public void draw(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (minecraft.player.tickCount != lastTick) {
            updateScreen();
            lastTick = minecraft.player.tickCount;
        }
        matrixStack.translate(-layout.getPosX(), -layout.getPosY(),0);
        render(matrixStack, mouseX + layout.getPosX(), mouseY + layout.getPosY(), minecraft.getDeltaFrameTime());
        matrixStack.translate(layout.getPosX(), layout.getPosY(),0);
    }

    public void updateScreen() {
        modularUI.mainGroup.updateScreen();
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.hoveredSlot = null;

        RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();

        tooltipTexts = null;

        modularUI.mainGroup.drawInBackground(matrixStack, mouseX, mouseY, partialTicks);
        modularUI.mainGroup.drawInForeground(matrixStack, mouseX, mouseY, partialTicks);

        if (tooltipTexts != null && tooltipTexts.size() > 0) {
            DrawerHelper.drawHoveringText(matrixStack, ItemStack.EMPTY, tooltipTexts, mouseX, mouseY, width, height, 300);
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    @Override
    public void superMouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.superMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void superMouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        super.superMouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public void superMouseReleased(double mouseX, double mouseY, int state) {
        super.superMouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean superKeyPressed(int keyCode, int scanCode, int modifiers) {
        return super.superKeyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean superMouseScrolled(double mouseX, double mouseY, double wheelDelta) {
        return super.superMouseScrolled(mouseX, mouseY, wheelDelta);
    }

    @Override
    public boolean superKeyReleased(int keyCode, int scanCode, int modifiers) {
        return super.superKeyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean superCharTyped(char codePoint, int modifiers) {
        return super.superCharTyped(codePoint, modifiers);
    }

    @Override
    public void superMouseMoved(double mouseX, double mouseY) {
        super.superMouseMoved(mouseX, mouseY);
    }

}
