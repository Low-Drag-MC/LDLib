package com.lowdragmc.lowdraglib.client.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;

import javax.annotation.Nonnull;

/**
 * Author: KilaBash
 * Date: 2022/04/21
 * Description: 
 */
public interface IBlockRendererProvider {

    @Nonnull
    IRenderer getRenderer(BlockState state, BlockPos pos, IBlockDisplayReader blockReader);

}
