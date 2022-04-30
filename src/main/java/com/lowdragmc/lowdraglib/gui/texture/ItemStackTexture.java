package com.lowdragmc.lowdraglib.gui.texture;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
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
        
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableRescaleNormal();
        RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F); // light map
        RenderSystem.enableDepthTest();

        mStack.scale(width / 16f, height / 16f, 0.0001f);
        mStack.translate(x * 16 / width, y * 16 / height, 0);
        
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();

        itemRenderer.blitOffset = 200f;
        itemRenderer.renderAndDecorateItem(itemStack[index], 0, 0);
        itemRenderer.blitOffset = 0;

        RenderSystem.disableRescaleNormal();
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.enableAlphaTest();

        mStack.popPose();


    }
}
