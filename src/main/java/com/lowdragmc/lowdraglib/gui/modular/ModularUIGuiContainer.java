package com.lowdragmc.lowdraglib.gui.modular;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketUIWidgetUpdate;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ModularUIGuiContainer extends AbstractContainerScreen<ModularUIContainer> {

    public final ModularUI modularUI;
    public Widget lastFocus;
    public boolean focused;
    public int dragSplittingLimit;
    public int dragSplittingButton;
    protected List<Component> tooltipTexts;
    protected Font tooltipFont;
    protected ItemStack tooltipStack = ItemStack.EMPTY;
    protected TooltipComponent tooltipComponent;

    public ModularUIGuiContainer(ModularUI modularUI, int windowId) {
        super(new ModularUIContainer(modularUI, windowId), modularUI.entityPlayer.getInventory(), new TextComponent("modularUI"));
        this.modularUI = modularUI;
        modularUI.setModularUIGui(this);
    }

    public void setHoverTooltip(List<Component> tooltipTexts, ItemStack tooltipStack, @Nullable Font tooltipFont, @Nullable TooltipComponent tooltipComponent) {
        if (this.tooltipTexts != null) return;
        this.tooltipTexts = tooltipTexts;
        this.tooltipStack = tooltipStack;
        this.tooltipFont = tooltipFont;
        this.tooltipComponent = tooltipComponent;
    }

    @Override
    public void init() {
        getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
        this.imageWidth = modularUI.getWidth();
        this.imageHeight = modularUI.getHeight();
        super.init();
        this.modularUI.updateScreenSize(width, height);
    }

    @Override
    public void removed() {
        super.removed();
        getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (modularUI.holder.isInvalid()) {
            modularUI.entityPlayer.closeContainer();
        }
        modularUI.mainGroup.updateScreen();
        modularUI.addTick();
    }

    public void handleWidgetUpdate(SPacketUIWidgetUpdate packet) {
        if (packet.windowId == getMenu().containerId) {
            int updateId = packet.updateData.readVarInt();
            modularUI.mainGroup.readUpdateInfo(updateId, packet.updateData);
        }
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.hoveredSlot = null;
        
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        tooltipTexts = null;
        tooltipFont = null;
        tooltipStack = ItemStack.EMPTY;
        tooltipComponent = null;

        DrawerHelper.drawGradientRect(poseStack, 0, 0, this.width, this.height, -1072689136, -804253680);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundDrawnEvent(this, poseStack));

        modularUI.mainGroup.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ContainerScreenEvent.DrawBackground(this, poseStack, mouseX, mouseY));

        modularUI.mainGroup.drawInForeground(poseStack, mouseX, mouseY, partialTicks);

        if (tooltipTexts != null && tooltipTexts.size() > 0) {
            poseStack.translate(0, 0, 200);
            renderTooltip(poseStack, tooltipTexts, Optional.ofNullable(tooltipComponent), mouseX, mouseY, tooltipFont, tooltipStack);
            poseStack.translate(0, 0, -200);
        }

        RenderSystem.depthMask(true);

        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(leftPos, topPos, 0.0D);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.hoveredSlot = null;

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ContainerScreenEvent.DrawForeground(this, poseStack, mouseX, mouseY));

        renderItemStackOnMouse(mouseX, mouseY);
        renderReturningItemStack();

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();
    }

    public void setHoveredSlot(Slot hoveredSlot) {
        this.hoveredSlot = hoveredSlot;
    }

    private void renderItemStackOnMouse(int mouseX, int mouseY) {
        if (minecraft == null || minecraft.player == null) return;
        ItemStack draggedStack = this.draggingItem;
        ItemStack itemstack = draggedStack.isEmpty() ? getMenu().getCarried() : draggedStack;
        if (!itemstack.isEmpty()) {
            int k2 = draggedStack.isEmpty() ? 8 : 16;
            String s = null;
            if (!draggedStack.isEmpty() && this.isSplittingStack) {
                itemstack = itemstack.copy();
                itemstack.setCount((int) Math.ceil((float)itemstack.getCount() / 2.0F));
            } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
                itemstack = itemstack.copy();
                itemstack.setCount(this.quickCraftingRemainder);
                if (itemstack.isEmpty()) {
                    s = "" + ChatFormatting.YELLOW + "0";
                }
            }
            this.renderFloatingItem(itemstack, mouseX - leftPos - 8, mouseY - topPos - k2, s);
        }
        
    }

    public void renderFloatingItem(ItemStack stack, int pX, int pY, @Nullable String text) {
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.translate(0.0D, 0.0D, 232.0D);
        RenderSystem.applyModelViewMatrix();
        this.setBlitOffset(200);
        this.itemRenderer.blitOffset = 200.0F;
        net.minecraft.client.gui.Font font = net.minecraftforge.client.RenderProperties.get(stack).getFont(stack);
        if (font == null) font = this.font;
        this.itemRenderer.renderAndDecorateItem(stack, pX, pY);
        this.itemRenderer.renderGuiItemDecorations(font, stack, pX, pY - (this.draggingItem.isEmpty() ? 0 : 8), text);
        this.setBlitOffset(0);
        this.itemRenderer.blitOffset = 0.0F;
    }

    private void renderReturningItemStack() {
        if (!this.snapbackItem.isEmpty()) {
            float f = (float)(Util.getMillis() - this.snapbackTime) / 100.0F;
            if (f >= 1.0F) {
                f = 1.0F;
                this.snapbackItem = ItemStack.EMPTY;
            }

            int l2 = this.snapbackEnd.x - this.snapbackStartX;
            int i3 = this.snapbackEnd.y - this.snapbackStartY;
            int l1 = this.snapbackStartX + (int)((float)l2 * f);
            int i2 = this.snapbackStartY + (int)((float)i3 * f);
            this.renderFloatingItem(this.snapbackItem, l1, i2, null);
        }
    }

    public boolean switchFocus(@Nonnull Widget widget) {
        if (focused) return false;
        if (lastFocus == widget) return false;
        Widget l = lastFocus;
        focused = true;
        lastFocus = widget;
        if (l != null) l.setFocus(false);
        return true;
    }

    public Set<Slot> getQuickCraftSlots() {
        return this.quickCraftSlots;
    }

    public boolean getQuickCrafting() {
        return this.isQuickCrafting;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int pButton) {
        focused = false;
        return modularUI.mainGroup.mouseClicked(mouseX, mouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int pButton, double pDragX, double pDragY) {
        focused = false;
        return modularUI.mainGroup.mouseDragged(mouseX, mouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int pButton) {
        focused = false;
        return modularUI.mainGroup.mouseReleased(mouseX, mouseY, pButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        focused = false;
        if (modularUI.mainGroup.keyPressed(keyCode, scanCode, modifiers)) {
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double wheelDelta) {
        focused = false;
        return modularUI.mainGroup.mouseWheelMove(mouseX, mouseY, wheelDelta);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        focused = false;
        return modularUI.mainGroup.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        focused = false;
        return modularUI.mainGroup.charTyped(codePoint, modifiers);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        focused = false;
        modularUI.mainGroup.mouseMoved(mouseX, mouseY);
    }

    public void superMouseClicked(double mouseX, double mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (Exception ignored) { }
    }

    public void superMouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    public void superMouseReleased(double mouseX, double mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    public boolean superKeyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean superMouseScrolled(double mouseX, double mouseY, double wheelDelta) {
        return super.mouseScrolled(mouseX, mouseY, wheelDelta);
    }

    public boolean superKeyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    public boolean superCharTyped(char codePoint, int modifiers) {
        return super.charTyped(codePoint, modifiers);
    }

    public void superMouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    protected void renderBg(@Nonnull PoseStack pPoseStack, float pPartialTicks, int pX, int pY) {
        
    }

    public List<Rect2i> getGuiExtraAreas() {
        return modularUI.mainGroup.getGuiExtraAreas(modularUI.mainGroup.toRectangleBox(), new ArrayList<>());
    }
}
