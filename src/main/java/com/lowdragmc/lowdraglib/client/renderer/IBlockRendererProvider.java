package com.lowdragmc.lowdraglib.client.renderer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlock;

import javax.annotation.Nullable;

/**
 * Author: KilaBash
 * Date: 2022/04/21
 * Description: 
 */
public interface IBlockRendererProvider extends IForgeBlock {

    @Nullable
    IRenderer getRenderer(BlockState state, BlockPos pos, BlockAndTintGetter blockReader);

    default int getLightingMap(BlockAndTintGetter world, BlockState state, BlockPos pos) {
        if (state.emissiveRendering(world, pos)) {
            return 15728880;
        } else {
            int i = world.getBrightness(LightLayer.SKY, pos);
            int j = world.getBrightness(LightLayer.BLOCK, pos);
            int k = state.getLightEmission(world, pos);
            if (j < k) {
                j = k;
            }

            return i << 20 | j << 4;
        }
    }

}
