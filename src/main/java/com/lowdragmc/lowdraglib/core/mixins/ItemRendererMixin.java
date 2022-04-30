package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.client.renderer.IItemRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    public void injectRenderItem(ItemStack stack,
                                 ItemCameraTransforms.TransformType transformType,
                                 boolean leftHand,
                                 MatrixStack matrixStack,
                                 IRenderTypeBuffer buffer,
                                 int combinedLight,
                                 int combinedOverlay,
                                 IBakedModel model,
                                 CallbackInfo ci){
        if (stack.getItem() instanceof IItemRendererProvider && !IItemRendererProvider.disabled.get()) {
            IRenderer renderer =((IItemRendererProvider) stack.getItem()).getRenderer(stack);
            renderer.renderItem(stack, transformType, leftHand, matrixStack, buffer, combinedLight, combinedOverlay, model);
            ci.cancel();
        }
    }
}
