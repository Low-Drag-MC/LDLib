package com.lowdragmc.lowdraglib.client.renderer;


import com.lowdragmc.lowdraglib.client.ClientProxy;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Random;

public interface IRenderer {

    IRenderer EMPTY = new IRenderer() {
        @Override
        @OnlyIn(Dist.CLIENT)
        public void renderItem(ItemStack stack,
                               ItemCameraTransforms.TransformType transformType,
                               boolean leftHand, MatrixStack matrixStack,
                               IRenderTypeBuffer buffer, int combinedLight,
                               int combinedOverlay, IBakedModel model) {

        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void renderBlockDamage(BlockState state, BlockPos pos,
                                      IBlockDisplayReader blockReader,
                                      MatrixStack matrixStack,
                                      IVertexBuilder vertexBuilder,
                                      IModelData modelData) {

        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean renderModel(BlockState state, BlockPos pos,
                                   IBlockDisplayReader blockReader,
                                   MatrixStack matrixStack,
                                   IVertexBuilder vertexBuilder,
                                   boolean checkSides, Random rand,
                                   IModelData modelData) {
            return false;
        }
    };

    @OnlyIn(Dist.CLIENT)
    void renderItem(ItemStack stack,
                    ItemCameraTransforms.TransformType transformType,
                    boolean leftHand, MatrixStack matrixStack,
                    IRenderTypeBuffer buffer, int combinedLight,
                    int combinedOverlay, IBakedModel model);

    @OnlyIn(Dist.CLIENT)
    void renderBlockDamage(BlockState state, BlockPos pos,
                           IBlockDisplayReader blockReader,
                           MatrixStack matrixStack,
                           IVertexBuilder vertexBuilder, IModelData modelData);

    @OnlyIn(Dist.CLIENT)
    boolean renderModel(BlockState state, BlockPos pos,
                        IBlockDisplayReader blockReader,
                        MatrixStack matrixStack, IVertexBuilder vertexBuilder,
                        boolean checkSides, Random rand, IModelData modelData);

    @OnlyIn(Dist.CLIENT)
    default void onTextureSwitchEvent(TextureStitchEvent.Pre event) {

    }

    @OnlyIn(Dist.CLIENT)
    default void registerTextureSwitchEvent() {
        ClientProxy.renderers.add(this);
    }

    default boolean isRaw() {
        return false;
    }

    default boolean hasTESR(TileEntity tileEntity) {
        return false;
    }

    default boolean isGlobalRenderer(TileEntity tileEntity) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    default void render(TileEntity tileEntity, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

    }
}