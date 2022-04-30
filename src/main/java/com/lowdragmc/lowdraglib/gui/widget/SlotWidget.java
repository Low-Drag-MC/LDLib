package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.ingredient.IIngredientSlot;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SlotWidget extends Widget implements IIngredientSlot {

    protected final Slot slotReference;
    protected final boolean canTakeItems;
    protected final boolean canPutItems;
    public boolean isPlayerInventory;
    public boolean isPlayerHotBar;
    public boolean drawOverlay = true;
    public boolean drawHoverTips = true;

    protected Runnable changeListener;
    protected BiConsumer<SlotWidget, List<ITextComponent>> onAddedTooltips;
    protected Function<ItemStack, ItemStack> itemHook;

    public SlotWidget(IInventory inventory, int slotIndex, int xPosition, int yPosition, boolean canTakeItems, boolean canPutItems) {
        super(new Position(xPosition, yPosition), new Size(18, 18));
        this.canTakeItems = canTakeItems;
        this.canPutItems = canPutItems;
        this.slotReference = createSlot(inventory, slotIndex);
    }

    public SlotWidget(IItemHandler itemHandler, int slotIndex, int xPosition, int yPosition, boolean canTakeItems, boolean canPutItems) {
        super(new Position(xPosition, yPosition), new Size(18, 18));
        this.canTakeItems = canTakeItems;
        this.canPutItems = canPutItems;
        this.slotReference = createSlot(itemHandler, slotIndex);
    }

    public SlotWidget setItemHook(Function<ItemStack, ItemStack> itemHook) {
        this.itemHook = itemHook;
        return this;
    }

    protected Slot createSlot(IInventory inventory, int index) {
        return new WidgetSlot(inventory, index, 0, 0);
    }

    protected Slot createSlot(IItemHandler itemHandler, int index) {
        return new WidgetSlotItemHandler(itemHandler, index, 0, 0);
    }

    public SlotWidget setOnAddedTooltips(BiConsumer<SlotWidget, List<ITextComponent>> onAddedTooltips) {
        this.onAddedTooltips = onAddedTooltips;
        return this;
    }

    public SlotWidget setDrawHoverTips(boolean drawHoverTips) {
        this.drawHoverTips = drawHoverTips;
        return this;
    }

    public SlotWidget setDrawOverlay(boolean drawOverlay) {
        this.drawOverlay = drawOverlay;
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInForeground(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        if (drawHoverTips && isMouseOverElement(mouseX, mouseY) && isActive()) {
            ItemStack stack = slotReference.getItem();
            if (!stack.isEmpty()) {
                setHoverTooltips(getToolTips(gui.getModularUIGui().getTooltipFromItem(stack)));
                super.drawInForeground(mStack, mouseX, mouseY, partialTicks);
            }
        }
        if (!drawOverlay) return;
        if (isActive()) {
            if (isMouseOverElement(mouseX, mouseY)) {
                RenderSystem.colorMask(true, true, true, false);
                DrawerHelper.drawSolidRect(mStack,getPosition().x + 1, getPosition().y + 1, 16, 16, 0x80FFFFFF);
                RenderSystem.colorMask(true, true, true, true);
            }
        } else {
            RenderSystem.colorMask(true, true, true, false);
            DrawerHelper.drawSolidRect(mStack,getPosition().x + 1, getPosition().y + 1, 16, 16, 0xbf000000);
            RenderSystem.colorMask(true, true, true, true);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(mStack, mouseX, mouseY, partialTicks);
        Position pos = getPosition();
        ItemStack itemStack = getRealStack(slotReference.getItem());
        ModularUIGuiContainer modularUIGui = gui == null ? null : gui.getModularUIGui();
        if (itemStack.isEmpty() && modularUIGui!= null && modularUIGui.getQuickCrafting() && modularUIGui.getQuickCraftSlots().contains(slotReference)) { // draw split
            int splitSize = modularUIGui.getQuickCraftSlots().size();
            itemStack = gui.entityPlayer.inventory.getCarried();
            if (!itemStack.isEmpty() && splitSize > 1 && Container.canItemQuickReplace(slotReference, itemStack, true)) {
                itemStack = itemStack.copy();
                Container.getQuickCraftSlotCount(modularUIGui.getQuickCraftSlots(), modularUIGui.dragSplittingLimit, itemStack, slotReference.getItem().isEmpty() ? 0 : slotReference.getItem().getCount());
                int k = Math.min(itemStack.getMaxStackSize(), slotReference.getMaxStackSize(itemStack));
                if (itemStack.getCount() > k) {
                    itemStack.setCount(k);
                }
            }
        }
        if (!itemStack.isEmpty()) {
            DrawerHelper.drawItemStack(mStack, itemStack, pos.x+ 1, pos.y + 1, null);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOverElement(mouseX, mouseY) && gui != null && (canPutItems || canTakeItems)) {
            ModularUIGuiContainer modularUIGui = gui.getModularUIGui();
            boolean last = modularUIGui.getQuickCrafting();
            InputMappings.Input mouseKey = InputMappings.Type.MOUSE.getOrCreate(button);
            gui.getModularUIGui().superMouseClicked(mouseX, mouseY, button);
            if (last != modularUIGui.getQuickCrafting()) {
                modularUIGui.dragSplittingButton = button;
                if (button == 0) {
                    modularUIGui.dragSplittingLimit = 0;
                }
                else if (button == 1) {
                    modularUIGui.dragSplittingLimit = 1;
                }
                else if (Minecraft.getInstance().options.keyPickItem.isActiveAndMatches(mouseKey)) {
                    modularUIGui.dragSplittingLimit = 2;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isMouseOverElement(mouseX, mouseY) && gui != null) {
            gui.getModularUIGui().superMouseReleased(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isMouseOverElement(mouseX, mouseY) && gui != null) {
            gui.getModularUIGui().superMouseDragged(mouseX, mouseY, button, dragX, dragY);
            return true;
        }
        return false;
    }

    @Override
    protected void onPositionUpdate() {
        if (gui != null) {
            Position position = getPosition();
            slotReference.x = position.x + 1 - gui.getGuiLeft();
            slotReference.y = position.y + 1 - gui.getGuiTop();
        }
    }

    public SlotWidget setChangeListener(Runnable changeListener) {
        this.changeListener = changeListener;
        return this;
    }

    public SlotWidget(IItemHandlerModifiable itemHandler, int slotIndex, int xPosition, int yPosition) {
        this(itemHandler, slotIndex, xPosition, yPosition, true, true);
    }

    public SlotWidget(IInventory inventory, int slotIndex, int xPosition, int yPosition) {
        this(inventory, slotIndex, xPosition, yPosition, true, true);
    }

    public SlotWidget setBackgroundTexture(IGuiTexture backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
        return this;
    }

    public boolean canPutStack(ItemStack stack) {
        return isEnabled() && canPutItems;
    }

    public boolean canTakeStack(PlayerEntity player) {
        return isEnabled() && canTakeItems;
    }

    public boolean isEnabled() {
        return this.isActive() && isVisible();
    }

    public boolean canMergeSlot(ItemStack stack) {
        return isEnabled();
    }

    public void onSlotChanged() {
        if (gui == null) return;
        gui.holder.markAsDirty();
    }

    public ItemStack slotClick(int dragType, ClickType clickTypeIn, PlayerEntity player) {
        return null;
    }

    public final Slot getHandle() {
        return slotReference;
    }

    public Widget setLocationInfo(boolean isPlayerInventory, boolean isPlayerHotBar) {
        this.isPlayerHotBar = isPlayerHotBar;
        this.isPlayerInventory = isPlayerInventory;
        return this;
    }

    public ItemStack onItemTake(PlayerEntity player, ItemStack stack, boolean simulate) {
        return stack;
    }

    private List<ITextComponent> getToolTips(List<ITextComponent> list) {
        if (this.onAddedTooltips != null) {
            this.onAddedTooltips.accept(this, list);
        }
        return list;
    }

    @Override
    public Object getIngredientOverMouse(double mouseX, double mouseY) {
        if (isMouseOverElement(mouseX, mouseY)) {
            return getRealStack(getHandle().getItem());
        }
        return null;
    }

    public ItemStack getRealStack(ItemStack itemStack) {
        if (itemHook != null) return itemHook.apply(itemStack);
        return  itemStack;
    }


    protected class WidgetSlot extends Slot {

        public WidgetSlot(IInventory inventory, int index, int xPosition, int yPosition) {
            super(inventory, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return SlotWidget.this.canPutStack(stack) && super.mayPlace(stack);
        }

        @Override
        public boolean mayPickup(@Nonnull PlayerEntity playerIn) {
            return SlotWidget.this.canTakeStack(playerIn) && super.mayPickup(playerIn);
        }

        @Override
        public void set(@Nonnull ItemStack stack) {
            if(!SlotWidget.this.canPutStack(stack)) return;
            super.set(stack);
            if (changeListener != null) {
                changeListener.run();
            }
        }

        @Nonnull
        @Override
        public final ItemStack onTake(@Nonnull PlayerEntity thePlayer, @Nonnull ItemStack stack) {
            return onItemTake(thePlayer, super.onTake(thePlayer, stack), false);
        }

        @Override
        public void setChanged() {
            SlotWidget.this.onSlotChanged();
        }

        @Override
        public boolean isActive() {
            return SlotWidget.this.isEnabled();
        }

    }

    protected class WidgetSlotItemHandler extends SlotItemHandler  {

        public WidgetSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return SlotWidget.this.canPutStack(stack) && super.mayPlace(stack);
        }

        @Override
        public boolean mayPickup(PlayerEntity playerIn) {
            return SlotWidget.this.canTakeStack(playerIn) && super.mayPickup(playerIn);
        }

        @Override
        public void set(@Nonnull ItemStack stack) {
            if(!SlotWidget.this.canPutStack(stack)) return;
            super.set(stack);
            if (changeListener != null) {
                changeListener.run();
            }
        }

        @Nonnull
        @Override
        public final ItemStack onTake(@Nonnull PlayerEntity thePlayer, @Nonnull ItemStack stack) {
            return onItemTake(thePlayer, super.onTake(thePlayer, stack), false);
        }

        @Override
        public void setChanged() {
            SlotWidget.this.onSlotChanged();
        }

        @Override
        public boolean isActive() {
            return SlotWidget.this.isEnabled();
        }

    }
}
