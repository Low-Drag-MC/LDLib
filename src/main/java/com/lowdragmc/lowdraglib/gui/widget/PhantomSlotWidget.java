package com.lowdragmc.lowdraglib.gui.widget;

import com.google.common.collect.Lists;
import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigSetter;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.ingredient.IGhostIngredientTarget;
import com.lowdragmc.lowdraglib.gui.ingredient.Target;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

@RegisterUI(name = "phantom_item_slot", group = "container")
public class PhantomSlotWidget extends SlotWidget implements IGhostIngredientTarget, IConfigurableWidget {

    @Configurable
    private boolean clearSlotOnRightClick;

    public PhantomSlotWidget() {
        super();
    }

    public PhantomSlotWidget(IItemHandlerModifiable itemHandler, int slotIndex, int xPosition, int yPosition) {
        super(itemHandler, slotIndex, xPosition, yPosition, true, true);
    }

    public PhantomSlotWidget setClearSlotOnRightClick(boolean clearSlotOnRightClick) {
        this.clearSlotOnRightClick = clearSlotOnRightClick;
        return this;
    }

    @ConfigSetter(field = "canTakeItems")
    public void setCanTakeItems(boolean v) {
        // you cant modify it
    }

    @ConfigSetter(field = "canPutItems")
    public void setCanPutItems(boolean v) {
        // you cant modify it
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (slotReference != null && isMouseOverElement(mouseX, mouseY) && gui != null) {
            if (isClientSideWidget && !gui.getModularUIContainer().getCarried().isEmpty()) {
                slotReference.set(gui.getModularUIContainer().getCarried());
            } else if (button == 1 && clearSlotOnRightClick && !slotReference.getItem().isEmpty()) {
                slotReference.set(ItemStack.EMPTY);
                writeClientAction(2, buf -> {
                });
            } else {
                gui.getModularUIGui().superMouseClicked(mouseX, mouseY, button);
            }
            return true;
        }
        return false;
    }

    @Override
    public ItemStack slotClick(int dragType, ClickType clickTypeIn, Player player) {
        if (slotReference != null && gui != null) {
            ItemStack stackHeld = gui.getModularUIContainer().getCarried();
            return slotClickPhantom(slotReference, dragType, clickTypeIn, stackHeld);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Target> getPhantomTargets(Object ingredient) {
        if (!(ingredient instanceof ItemStack)) {
            return Collections.emptyList();
        }
        Rect2i rectangle = toRectangleBox();
        return Lists.newArrayList(new Target() {
            @Nonnull
            @Override
            public Rect2i getArea() {
                return rectangle;
            }

            @Override
            public void accept(@Nonnull Object ingredient) {
                if (slotReference != null && ingredient instanceof ItemStack) {
                    MouseHandler mouseHelper = Minecraft.getInstance().mouseHandler;
                    long id = Minecraft.getInstance().getWindow().getWindow();
                    int mouseButton = mouseHelper.isLeftPressed() ? 0 : mouseHelper.isRightPressed() ? 1 : 2;
                    boolean shiftDown = InputConstants.isKeyDown(id, GLFW.GLFW_KEY_LEFT_SHIFT) || InputConstants.isKeyDown(id, GLFW.GLFW_KEY_LEFT_SHIFT);
                    ClickType clickType = shiftDown ? ClickType.QUICK_MOVE : ClickType.PICKUP;
                    slotClickPhantom(slotReference, mouseButton, clickType, (ItemStack) ingredient);
                    writeClientAction(1, buffer -> {
                        buffer.writeItem((ItemStack) ingredient);
                        buffer.writeVarInt(mouseButton);
                        buffer.writeBoolean(shiftDown);
                    });
                }
            }
        });
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        if (slotReference != null && id == 1) {
            ItemStack stackHeld;
            stackHeld = buffer.readItem();
            int mouseButton = buffer.readVarInt();
            boolean shiftKeyDown = buffer.readBoolean();
            ClickType clickType = shiftKeyDown ? ClickType.QUICK_MOVE : ClickType.PICKUP;
            slotClickPhantom(slotReference, mouseButton, clickType, stackHeld);
        } else if (slotReference != null && id == 2) {
            slotReference.set(ItemStack.EMPTY);
        }
    }

    public static ItemStack slotClickPhantom(Slot slot, int mouseButton, ClickType clickTypeIn, ItemStack stackHeld) {
        ItemStack stack = ItemStack.EMPTY;

        ItemStack stackSlot = slot.getItem();
        if (!stackSlot.isEmpty()) {
            stack = stackSlot.copy();
        }

        if (mouseButton == 2) {
            fillPhantomSlot(slot, ItemStack.EMPTY, mouseButton);
        } else if (mouseButton == 0 || mouseButton == 1) {

            if (stackSlot.isEmpty()) {
                if (!stackHeld.isEmpty() ) {
                    fillPhantomSlot(slot, stackHeld, mouseButton);
                }
            } else if (stackHeld.isEmpty()) {
                adjustPhantomSlot(slot, mouseButton, clickTypeIn);
            } else  {
                if (!areItemsEqual(stackSlot, stackHeld)) {
                    adjustPhantomSlot(slot, mouseButton, clickTypeIn);
                }
                fillPhantomSlot(slot, stackHeld, mouseButton);
            }
        } else if (mouseButton == 5) {
            if (!slot.hasItem()) {
                fillPhantomSlot(slot, stackHeld, mouseButton);
            }
        }
        return stack;
    }

    private static void adjustPhantomSlot(Slot slot, int mouseButton, ClickType clickTypeIn) {
        ItemStack stackSlot = slot.getItem();
        int stackSize;
        if (clickTypeIn == ClickType.QUICK_MOVE) {
            stackSize = mouseButton == 0 ? (stackSlot.getCount() + 1) / 2 : stackSlot.getCount() * 2;
        } else {
            stackSize = mouseButton == 0 ? stackSlot.getCount() - 1 : stackSlot.getCount() + 1;
        }

        if (stackSize > slot.getMaxStackSize()) {
            stackSize = slot.getMaxStackSize();
        }

        stackSlot.setCount(stackSize);

        slot.set(stackSlot);
    }

    private static void fillPhantomSlot(Slot slot, ItemStack stackHeld, int mouseButton) {
        if (stackHeld.isEmpty()) {
            slot.set(ItemStack.EMPTY);
            return;
        }

        int stackSize = mouseButton == 0 ? stackHeld.getCount() : 1;
        if (stackSize > slot.getMaxStackSize()) {
            stackSize = slot.getMaxStackSize();
        }
        ItemStack phantomStack = stackHeld.copy();
        phantomStack.setCount(stackSize);

        slot.set(phantomStack);
    }

    public static boolean areItemsEqual(ItemStack itemStack1, ItemStack itemStack2) {
        return ItemStack.matches(itemStack1, itemStack2);
    }
}