package com.lowdragmc.lowdraglib.gui.modular;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketUIWidgetUpdate;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ModularUIGuiContainer extends ContainerScreen<ModularUIContainer> {

    public final ModularUI modularUI;
    public Widget lastFocus;
    public boolean focused;
    public int dragSplittingLimit;
    public int dragSplittingButton;
    public List<ITextComponent> tooltipTexts;

    public ModularUIGuiContainer(ModularUI modularUI, int windowId) {
        super(new ModularUIContainer(modularUI, windowId), modularUI.entityPlayer.inventory, new StringTextComponent("modularUI"));
        this.modularUI = modularUI;
        modularUI.setModularUIGui(this);
    }

    public void setHoverTooltip(List<ITextComponent> tooltipTexts) {
        if (this.tooltipTexts != null) return;
        this.tooltipTexts = tooltipTexts;
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
    public void tick() {
        super.tick();
        if (modularUI.holder.isInvalid()) {
            modularUI.entityPlayer.closeContainer();
        }
        modularUI.mainGroup.updateScreen();
    }

    public void handleWidgetUpdate(SPacketUIWidgetUpdate packet) {
        if (packet.windowId == getMenu().containerId) {
            int updateId = packet.updateData.readVarInt();
            modularUI.mainGroup.readUpdateInfo(updateId, packet.updateData);
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.hoveredSlot = null;
        
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();

        tooltipTexts = null;

        DrawerHelper.drawGradientRect(matrixStack, 0, 0, this.width, this.height, -1072689136, -804253680);
        modularUI.mainGroup.drawInBackground(matrixStack, mouseX, mouseY, partialTicks);
        modularUI.mainGroup.drawInForeground(matrixStack, mouseX, mouseY, partialTicks);

        if (tooltipTexts != null && tooltipTexts.size() > 0) {
            DrawerHelper.drawHoveringText(matrixStack, ItemStack.EMPTY, tooltipTexts, mouseX, mouseY, width, height, 300);
        }

        RenderSystem.pushMatrix();
        RenderSystem.translatef(leftPos, topPos, 0.0F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableRescaleNormal();
        RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F); // light map
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawForeground(this, matrixStack, mouseX, mouseY));

        renderItemStackOnMouse(mouseX, mouseY);
        renderReturningItemStack();
        
        RenderSystem.popMatrix();
        RenderSystem.enableDepthTest();
    }

    public void setHoveredSlot(Slot hoveredSlot) {
        this.hoveredSlot = hoveredSlot;
    }

    private void renderItemStackOnMouse(int mouseX, int mouseY) {
        if (minecraft == null || minecraft.player == null) return;
        ItemStack draggedStack = this.draggingItem;
        PlayerInventory playerinventory = this.minecraft.player.inventory;
        ItemStack itemstack = draggedStack.isEmpty() ? playerinventory.getCarried() : draggedStack;
        if (!itemstack.isEmpty()) {
            int k2 = draggedStack.isEmpty() ? 8 : 16;
            String s = null;
            if (!draggedStack.isEmpty() && this.isSplittingStack) {
                itemstack = itemstack.copy();
                itemstack.setCount(MathHelper.ceil((float)itemstack.getCount() / 2.0F));
            } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
                itemstack = itemstack.copy();
                itemstack.setCount(this.quickCraftingRemainder);
                if (itemstack.isEmpty()) {
                    s = "" + TextFormatting.YELLOW + "0";
                }
            }
            this.renderFloatingItem(itemstack, mouseX - leftPos - 8, mouseY - topPos - k2, s);
        }
        
    }

    public void renderFloatingItem(ItemStack stack, int pX, int pY, @Nullable String text) {
        RenderSystem.translatef(0.0F, 0.0F, 32.0F);
        this.setBlitOffset(200); // zlevel
        this.itemRenderer.blitOffset = 200.0F;
        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
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
        if (lastFocus != null) lastFocus.setFocus(false);
        focused = true;
        lastFocus = widget;
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
        return modularUI.mainGroup.mouseDragged(mouseX, mouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int pButton) {
        return modularUI.mainGroup.mouseReleased(mouseX, mouseY, pButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (modularUI.mainGroup.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double wheelDelta) {
        return modularUI.mainGroup.mouseWheelMove(mouseX, mouseY, wheelDelta);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return modularUI.mainGroup.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return modularUI.mainGroup.charTyped(codePoint, modifiers);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
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
    protected void renderBg(@Nonnull MatrixStack pMatrixStack, float pPartialTicks, int pX, int pY) {
        
    }
}
