package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.client.renderer.ATESRRendererProvider;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityRendererDispatcher.class)
public class TileEntityRendererDispatcherMixin {
    @Final @Shadow public static TileEntityRendererDispatcher instance;

    @Inject(method = "getRenderer", at = @At(value = "RETURN"), cancellable = true)
    private <T extends TileEntity> void injectGetRenderer(T tileEntity, CallbackInfoReturnable<TileEntityRenderer<T>> cir) {
        TileEntityRenderer<T> renderer = cir.getReturnValue();
        if (renderer instanceof ATESRRendererProvider && !((ATESRRendererProvider<T>) renderer).hasRenderer(tileEntity)) {
            cir.setReturnValue(null);
        }
    }

}
