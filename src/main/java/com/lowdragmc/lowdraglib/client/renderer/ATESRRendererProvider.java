package com.lowdragmc.lowdraglib.client.renderer;


import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: KilaBash
 * Date: 2022/04/21
 * Description: 
 */
@OnlyIn(Dist.CLIENT)
public class ATESRRendererProvider<T extends TileEntity> extends TileEntityRenderer<T> {

    public ATESRRendererProvider(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(T te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        IRenderer renderer = getRenderer(te);
        if (renderer != null && !renderer.isRaw()) {
            renderer.render(te, partialTicks, stack, buffer, combinedLight, combinedOverlay);
        }
    }

    @Nullable
    public IRenderer getRenderer(@Nonnull T tileEntity) {
        World world = tileEntity.getLevel();
        if (world != null) {
            BlockState state = world.getBlockState(tileEntity.getBlockPos());
            if (state.getBlock() instanceof IBlockRendererProvider) {
                return ((IBlockRendererProvider) state.getBlock()).getRenderer(state, tileEntity.getBlockPos(), world);
            }
        }
        return null;
    }

    public boolean hasRenderer(T tileEntity) {
        IRenderer renderer = getRenderer(tileEntity);
        return renderer != null && renderer.hasTESR(tileEntity);
    }

    @Override
    public boolean shouldRenderOffScreen(@Nonnull T tileEntity) {
        IRenderer renderer = getRenderer(tileEntity);
        if (renderer != null) {
            return renderer.isGlobalRenderer(tileEntity);
        }
        return false;
    }
    
}

