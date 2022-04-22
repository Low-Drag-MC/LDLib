package com.lowdragmc.lowdraglib.client.renderer;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Author: KilaBash
 * Date: 2022/04/21
 * Description: 
 */
public interface IItemRendererProvider {
    @Nonnull
    IRenderer getRenderer(ItemStack stack);
}
