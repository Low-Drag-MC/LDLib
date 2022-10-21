package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemStackTexture implements IGuiTexture{
    public final ItemStack[] itemStack;
    private int index = 0;
    private int ticks = 0;
    private int color = -1;

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
    public ItemStackTexture setColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    public void updateTick() {
        if(itemStack.length > 1 && ++ticks % 20 == 0)
            if(++index == itemStack.length)
                index = 0;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(PoseStack mStack, int mouseX, int mouseY, float x, float y, int width, int height) {
        if (itemStack.length == 0) return;
        mStack.pushPose();
        mStack.scale(width / 16f, height / 16f, (width + height) / 32f);
        //TODO fix scale z offset
        mStack.translate(x * 16 / width, y * 16 / height, -200 * (width + height) / 32f);
        DrawerHelper.drawItemStack(mStack, itemStack[index], 0, 0, color, null);
        mStack.popPose();
    }
}
