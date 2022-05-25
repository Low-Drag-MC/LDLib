package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockRenderDispatcher.class)
public class BlockRendererDispatcherMixin {

    @Inject(method = "renderBreakingTexture(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraftforge/client/model/data/IModelData;)V", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void injectRenderBlockDamage(BlockState state,
                                         BlockPos pos,
                                         BlockAndTintGetter blockReader,
                                         PoseStack poseStack,
                                         VertexConsumer vertexBuilder,
                                         IModelData modelData, CallbackInfo ci) {
        if (state.getBlock() instanceof IBlockRendererProvider) {
            IRenderer renderer = ((IBlockRendererProvider) state.getBlock()).getRenderer(state, pos, blockReader);
            if (renderer != null) {
                renderer.renderBlockDamage(state, pos, blockReader, poseStack, vertexBuilder, modelData);
            }
            ci.cancel();
        }
    }

    @Inject(method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;Lnet/minecraftforge/client/model/data/IModelData;)Z", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void injectRenderModel(BlockState state, BlockPos pos,
                                  BlockAndTintGetter blockReader,
                                  PoseStack poseStack, VertexConsumer vertexBuilder,
                                  boolean checkSides, Random rand,
                                  IModelData modelData,
                                  CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof IBlockRendererProvider) {
            IRenderer renderer = ((IBlockRendererProvider) state.getBlock()).getRenderer(state, pos, blockReader);
            if (renderer != null) {
                cir.setReturnValue(renderer.renderModel(state, pos, blockReader, poseStack, vertexBuilder, checkSides, rand, modelData));
            }
            cir.setReturnValue(false);
        }
    }
}
