package com.lowdragmc.lowdraglib.client.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.LightType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Author: KilaBash
 * Date: 2022/04/21
 * Description: 
 */
public interface IBlockRendererProvider {

    @Nullable
    IRenderer getRenderer(BlockState state, BlockPos pos, IBlockDisplayReader blockReader);

    default int getLightingMap(IBlockDisplayReader world, BlockState state, BlockPos pos) {
        if (state.emissiveRendering(world, pos)) {
            return 15728880;
        } else {
            int i = world.getBrightness(LightType.SKY, pos);
            int j = world.getBrightness(LightType.BLOCK, pos);
            int k = state.getLightValue(world, pos);
            if (j < k) {
                j = k;
            }

            return i << 20 | j << 4;
        }
    }
}
