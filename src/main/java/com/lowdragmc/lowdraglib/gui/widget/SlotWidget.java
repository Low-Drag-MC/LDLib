package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@RegisterUI(name = "item_slot", group = "container")
public class SlotWidget extends Widget implements IRecipeIngredientSlot, IConfigurableWidget {

    @Nullable
    protected Slot slotReference;
    @Configurable
    protected boolean canTakeItems;
    @Configurable
    protected boolean canPutItems;
    public boolean isPlayerContainer;
    public boolean isPlayerHotBar;
    @Configurable
    public boolean drawHoverOverlay = true;
    @Configurable
    public boolean drawHoverTips = true;

    @Configurable
    protected IGuiTexture overlay;

    protected Runnable changeListener;
    protected BiConsumer<SlotWidget, List<Component>> onAddedTooltips;
    protected Function<ItemStack, ItemStack> itemHook;
    protected IngredientIO ingredientIO = IngredientIO.RENDER_ONLY;

    public SlotWidget() {
        super(new Position(0, 0), new Size(18, 18));
        setBackgroundTexture(new ResourceTexture("ldlib:textures/gui/slot.png"));
        this.canTakeItems = true;
        this.canPutItems = true;
    }

    public SlotWidget(Container inventory, int slotIndex, int xPosition, int yPosition, boolean canTakeItems, boolean canPutItems) {
        super(new Position(xPosition, yPosition), new Size(18, 18));
        this.canTakeItems = canTakeItems;
        this.canPutItems = canPutItems;
        setContainerSlot(inventory, slotIndex);
    }

    public SlotWidget(IItemHandler itemHandler, int slotIndex, int xPosition, int yPosition, boolean canTakeItems, boolean canPutItems) {
        super(new Position(xPosition, yPosition), new Size(18, 18));
        this.canTakeItems = canTakeItems;
        this.canPutItems = canPutItems;
        setHandlerSlot(itemHandler, slotIndex);
    }

    public SlotWidget setItemHook(Function<ItemStack, ItemStack> itemHook) {
        this.itemHook = itemHook;
        return this;
    }

    public SlotWidget setIngredientIO(IngredientIO ingredientIO) {
        this.ingredientIO = ingredientIO;
        return this;
    }

    protected Slot createSlot(Container inventory, int index) {
        return new WidgetSlot(inventory, index, 0, 0);
    }

    protected Slot createSlot(IItemHandler itemHandler, int index) {
        return new WidgetSlotItemHandler(itemHandler, index, 0, 0);
    }

    public SlotWidget setOnAddedTooltips(BiConsumer<SlotWidget, List<Component>> onAddedTooltips) {
        this.onAddedTooltips = onAddedTooltips;
        return this;
    }

    public SlotWidget setDrawHoverTips(boolean drawHoverTips) {
        this.drawHoverTips = drawHoverTips;
        return this;
    }

    public SlotWidget setDrawOverlay(boolean drawOverlay) {
        this.drawHoverOverlay = drawOverlay;
        return this;
    }

    public void setContainerSlot(Container inventory, int slotIndex) {
        updateSlot(createSlot(inventory, slotIndex));
    }

    public void setHandlerSlot(IItemHandler itemHandler, int slotIndex) {
        updateSlot(createSlot(itemHandler, slotIndex));
    }

    protected void updateSlot(Slot slot) {
        if (this.slotReference != null && this.gui != null && !isClientSideWidget) {
            getGui().removeNativeSlot(this.slotReference);
        }
        this.slotReference = slot;
        if (this.gui != null && !isClientSideWidget) {
            getGui().addNativeSlot(this.slotReference, this);
        }
    }

    @Override
    public final void setSize(Size size) {
        // you cant modify size.
    }

    @Override
    public void setGui(ModularUI gui) {
        if (!isClientSideWidget && this.gui != gui) {
            if (this.gui != null && slotReference != null) {
                this.gui.removeNativeSlot(slotReference);
            }
            if (gui != null && slotReference != null) {
                gui.addNativeSlot(slotReference, this);
            }
        }
        super.setGui(gui);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInForeground(@Nonnull PoseStack mStack, int mouseX, int mouseY, float partialTicks) {
        if (slotReference != null && drawHoverTips && isMouseOverElement(mouseX, mouseY) && isActive()) {
            ItemStack stack = slotReference.getItem();
            if (!stack.isEmpty() && gui != null) {
                gui.getModularUIGui().setHoverTooltip(getToolTips(gui.getModularUIGui().getTooltipFromItem(stack)), stack, null, stack.getTooltipImage().orElse(null));
                super.drawInForeground(mStack, mouseX, mouseY, partialTicks);
            }
        }
        if (drawHoverOverlay && isMouseOverElement(mouseX, mouseY)) {
            RenderSystem.colorMask(true, true, true, false);
            DrawerHelper.drawSolidRect(mStack,getPosition().x + 1, getPosition().y + 1, 16, 16, 0x80FFFFFF);
            RenderSystem.colorMask(true, true, true, true);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull PoseStack mStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(mStack, mouseX, mouseY, partialTicks);
        Position pos = getPosition();
        if (slotReference != null)  {
            ItemStack itemStack = getRealStack(slotReference.getItem());
            ModularUIGuiContainer modularUIGui = gui == null ? null : gui.getModularUIGui();
            if (itemStack.isEmpty() && modularUIGui!= null && modularUIGui.getQuickCrafting() && modularUIGui.getQuickCraftSlots().contains(slotReference)) { // draw split
                int splitSize = modularUIGui.getQuickCraftSlots().size();
                itemStack = gui.getModularUIContainer().getCarried();
                if (!itemStack.isEmpty() && splitSize > 1 && AbstractContainerMenu.canItemQuickReplace(slotReference, itemStack, true)) {
                    itemStack = itemStack.copy();
                    AbstractContainerMenu.getQuickCraftSlotCount(modularUIGui.getQuickCraftSlots(), modularUIGui.dragSplittingLimit, itemStack, slotReference.getItem().isEmpty() ? 0 : slotReference.getItem().getCount());
                    int k = Math.min(itemStack.getMaxStackSize(), slotReference.getMaxStackSize(itemStack));
                    if (itemStack.getCount() > k) {
                        itemStack.setCount(k);
                    }
                }
            }
            if (!itemStack.isEmpty()) {
                DrawerHelper.drawItemStack(mStack, itemStack, pos.x+ 1, pos.y + 1, -1, null);
            }
        }
        if (overlay != null) {
            overlay.draw(mStack, mouseX, mouseY, pos.x, pos.y, 18, 18);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (slotReference != null && isMouseOverElement(mouseX, mouseY) && gui != null) {
            var stack = slotReference.getItem();
            if (!(canPutItems && stack.isEmpty() || canTakeItems && !stack.isEmpty())) return false;
            ModularUIGuiContainer modularUIGui = gui.getModularUIGui();
            boolean last = modularUIGui.getQuickCrafting();
            InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(button);
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
            if (slotReference != null) {
                slotReference.x = position.x + 1 - gui.getGuiLeft();
                slotReference.y = position.y + 1 - gui.getGuiTop();
            }
        }
    }

    public SlotWidget setChangeListener(Runnable changeListener) {
        this.changeListener = changeListener;
        return this;
    }

    public SlotWidget(IItemHandlerModifiable itemHandler, int slotIndex, int xPosition, int yPosition) {
        this(itemHandler, slotIndex, xPosition, yPosition, true, true);
    }

    public SlotWidget(Container inventory, int slotIndex, int xPosition, int yPosition) {
        this(inventory, slotIndex, xPosition, yPosition, true, true);
    }

    public SlotWidget setBackgroundTexture(IGuiTexture backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
        return this;
    }

    public boolean canPutStack(ItemStack stack) {
        return isEnabled() && canPutItems;
    }

    public boolean canTakeStack(Player player) {
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

    public ItemStack slotClick(int dragType, ClickType clickTypeIn, Player player) {
        return null;
    }

    @Nullable
    public final Slot getHandle() {
        return slotReference;
    }

    public Widget setLocationInfo(boolean isPlayerContainer, boolean isPlayerHotBar) {
        this.isPlayerHotBar = isPlayerHotBar;
        this.isPlayerContainer = isPlayerContainer;
        return this;
    }

    private List<Component> getToolTips(List<Component> list) {
        if (this.onAddedTooltips != null) {
            this.onAddedTooltips.accept(this, list);
        }
        return list;
    }

    @Override
    public Object getJEIIngredient() {
        if (slotReference == null) return null;
        if (LDLMod.isReiLoaded()) {
            return EntryStacks.of(getRealStack(getHandle().getItem()));
        }
        return getRealStack(getHandle().getItem());
    }

    @Override
    public IngredientIO getIngredientIO() {
        return ingredientIO;
    }

    public ItemStack getRealStack(ItemStack itemStack) {
        if (itemHook != null) return itemHook.apply(itemStack);
        return  itemStack;
    }


    protected class WidgetSlot extends Slot {

        public WidgetSlot(Container inventory, int index, int xPosition, int yPosition) {
            super(inventory, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return SlotWidget.this.canPutStack(stack) && super.mayPlace(stack);
        }

        @Override
        public boolean mayPickup(@Nonnull Player playerIn) {
            return SlotWidget.this.canTakeStack(playerIn) && super.mayPickup(playerIn);
        }

        @Override
        public void set(@Nonnull ItemStack stack) {
//            if(!SlotWidget.this.canPutStack(stack)) return;
            super.set(stack);
            if (changeListener != null) {
                changeListener.run();
            }
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
        public boolean mayPickup(Player playerIn) {
            return SlotWidget.this.canTakeStack(playerIn) && super.mayPickup(playerIn);
        }

        @Override
        public void set(@Nonnull ItemStack stack) {
            super.set(stack);
            if (changeListener != null) {
                changeListener.run();
            }
        }

        @NotNull
        @Override
        public ItemStack remove(int amount) {
            var result = super.remove(amount);
            if (changeListener != null && !getItem().isEmpty()) {
                changeListener.run();
            }
            return result;
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
