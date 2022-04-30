package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "getLightColor(Lnet/minecraft/world/IBlockDisplayReader;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I",  at = @At(value = "HEAD"), cancellable = true)
    private static void injectShouldRenderFace(IBlockDisplayReader world,
                                               BlockState state,
                                               BlockPos pos,
                                               CallbackInfoReturnable<Integer> cir) {
        if (state.getBlock() instanceof IBlockRendererProvider) {
            cir.setReturnValue(((IBlockRendererProvider)state.getBlock()).getLightingMap(world, state, pos));
        }
    }

}
