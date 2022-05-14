package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStackTexture implements IGuiTexture{
    private final ItemStack[] itemStack;
    private int index = 0;
    private int ticks = 0;

    public ItemStackTexture(ItemStack... itemStacks) {
        this.itemStack = itemStacks;
    }

    public ItemStackTexture(Item... items) {
        this.itemStack = new ItemStack[items.length];
        for(int i = 0; i < items.length; i++) {
            itemStack[i] = new ItemStack(items[i]);
        }
    }

    @Override
    public void updateTick() {
        if(itemStack.length > 1 && ++ticks % 20 == 0)
            if(++index == itemStack.length)
                index = 0;
    }

    @Override
    public void draw(MatrixStack mStack, int mouseX, int mouseY, float x, float y, int width, int height) {
        if (itemStack.length == 0) return;
        mStack.pushPose();
        mStack.scale(width / 16f, height / 16f, (width + height) / 32f);
        mStack.translate(x * 16 / width, y * 16 / height, 0);
        DrawerHelper.drawItemStack(mStack, itemStack[index], 0, 0, null);
        mStack.popPose();
    }
}
