package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author KilaBash
 * @date 2022/05/28
 * @implNote TODO
 */
@Mixin(BlockModelShapes.class)
public class BlockModelShaperMixin {
    @Inject(method = "stateToModelLocation(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/renderer/model/ModelResourceLocation;", at = @At(value = "HEAD"), cancellable = true)
    private static void injectStateToModelLocation(ResourceLocation pLocation, BlockState pState, CallbackInfoReturnable<ModelResourceLocation> cir) {
        if (pState.getBlock() instanceof IBlockRendererProvider) {
            cir.setReturnValue(new ModelResourceLocation(new ResourceLocation("ldlib:renderer_model"), ""));
        }
    }
}
