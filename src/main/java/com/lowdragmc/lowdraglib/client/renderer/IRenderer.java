package com.lowdragmc.lowdraglib.client.renderer;

import com.lowdragmc.lowdraglib.client.ClientProxy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import java.util.Random;

public interface IRenderer {
    IRenderer EMPTY = new IRenderer() {
        @Override
        public void renderItem(ItemStack stack,
                               ItemTransforms.TransformType transformType,
                               boolean leftHand, PoseStack matrixStack,
                               MultiBufferSource buffer, int combinedLight,
                               int combinedOverlay, BakedModel model) {

        }

        @Override
        public boolean renderModel(BlockState state, BlockPos pos,
                                   BlockAndTintGetter blockReader,
                                   PoseStack matrixStack,
                                   VertexConsumer vertexBuilder,
                                   boolean checkSides, Random rand,
                                   IModelData modelData) {
            return false;
        }
    };

    @OnlyIn(Dist.CLIENT)
    void renderItem(ItemStack stack,
                    ItemTransforms.TransformType transformType,
                    boolean leftHand, PoseStack matrixStack,
                    MultiBufferSource buffer, int combinedLight,
                    int combinedOverlay, BakedModel model);

    @OnlyIn(Dist.CLIENT)
    default void renderBlockDamage(BlockState state, BlockPos pos,
                                   BlockAndTintGetter blockReader,
                                   PoseStack poseStack,
                                   VertexConsumer vertexBuilder, IModelData modelData) {

    }

    @OnlyIn(Dist.CLIENT)
    boolean renderModel(BlockState state, BlockPos pos,
                        BlockAndTintGetter blockReader,
                        PoseStack poseStack, VertexConsumer vertexBuilder,
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

    @OnlyIn(Dist.CLIENT)
    default boolean hasTESR(BlockEntity BlockEntity) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    default boolean isGlobalRenderer(BlockEntity BlockEntity) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    default void render(BlockEntity BlockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

    }

    @OnlyIn(Dist.CLIENT)
    @Nonnull
    default TextureAtlasSprite getParticleTexture() {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(MissingTextureAtlasSprite.getLocation());
    }
}
