package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockRendererDispatcher.class)
public class BlockRendererDispatcherMixin {

    @Inject(method = "renderBlockDamage", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void injectRenderBlockDamage(BlockState state,
                                         BlockPos pos,
                                         IBlockDisplayReader blockReader,
                                         MatrixStack matrixStack,
                                         IVertexBuilder vertexBuilder,
                                         IModelData modelData,
                                         CallbackInfo ci) {
        if (state.getBlock() instanceof IBlockRendererProvider) {
            IRenderer renderer = ((IBlockRendererProvider) state.getBlock()).getRenderer(state, pos, blockReader);
            if (renderer != null) {
                renderer.renderBlockDamage(state, pos, blockReader, matrixStack, vertexBuilder, modelData);
            }
            ci.cancel();
        }
    }

    @Inject(method = "renderModel", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void injectRenderModel(BlockState state,
                                  BlockPos pos,
                                  IBlockDisplayReader blockReader,
                                  MatrixStack matrixStack,
                                  IVertexBuilder vertexBuilder,
                                  boolean checkSides,
                                  Random rand,
                                  IModelData modelData,
                                  CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof IBlockRendererProvider) {
            IRenderer renderer = ((IBlockRendererProvider) state.getBlock()).getRenderer(state, pos, blockReader);
            if (renderer != null) {
                cir.setReturnValue(renderer.renderModel(state, pos, blockReader, matrixStack, vertexBuilder, checkSides, rand, modelData));
            }
            cir.setReturnValue(false);
        }
    }
}
