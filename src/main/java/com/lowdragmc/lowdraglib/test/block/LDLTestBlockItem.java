package com.lowdragmc.lowdraglib.test.block;

import com.lowdragmc.lowdraglib.client.renderer.IItemRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Author: KilaBash
 * Date: 2022/04/22
 * Description:
 */
public class LDLTestBlockItem extends BlockItem implements IItemRendererProvider {

    public LDLTestBlockItem(LDLTestBlock pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    @Nonnull
    public IRenderer getRenderer(ItemStack stack) {
        return ((LDLTestBlock)getBlock()).getRenderer(null, null, null);
    }
}
