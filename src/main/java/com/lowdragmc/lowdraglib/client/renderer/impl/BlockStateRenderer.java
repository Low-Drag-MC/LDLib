package com.lowdragmc.lowdraglib.client.renderer.impl;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.utils.FacadeBlockAndTintGetter;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.lowdragmc.lowdraglib.utils.FacadeBlockWorld;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockStateRenderer implements IRenderer {

    public final BlockInfo blockInfo;
    @OnlyIn(Dist.CLIENT)
    private BakedModel itemModel;

    protected BlockStateRenderer() {
        blockInfo = null;
    }

    public BlockStateRenderer(BlockState state) {
        this(BlockInfo.fromBlockState(state == null ? Blocks.BARRIER.defaultBlockState() : state));
    }

    public BlockStateRenderer(BlockInfo blockInfo) {
        this.blockInfo = blockInfo == null ? new BlockInfo(Blocks.BARRIER) : blockInfo;
        if (LDLMod.isClient()) {
            registerTextureSwitchEvent();
        }
    }

    public BlockState getState(BlockState blockState) {
        BlockState state = blockInfo.getBlockState();
        if (blockState.hasProperty(BlockStateProperties.FACING) && state.hasProperty(BlockStateProperties.FACING)) {
            state = state.setValue(BlockStateProperties.FACING, blockState.getValue(BlockStateProperties.FACING));
        } else if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)&& state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, blockState.getValue(BlockStateProperties.HORIZONTAL_FACING));
        }
        return state;
    }

    public BlockInfo getBlockInfo() {
        return blockInfo;
    }

    @OnlyIn(Dist.CLIENT)
    protected BakedModel getItemModel(ItemStack renderItem) {
        if (itemModel == null) {
            itemModel = Minecraft.getInstance().getItemRenderer().getModel(renderItem, null, null, 0);
        }
        return itemModel;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    public TextureAtlasSprite getParticleTexture() {
        ItemStack renderItem = getBlockInfo().getItemStackForm();
        BakedModel model = getItemModel(renderItem);
        if (model == null) {
            return IRenderer.super.getParticleTexture();
        }
        return model.getParticleIcon(EmptyModelData.INSTANCE);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderItem(ItemStack stack, ItemTransforms.TransformType transformType, boolean leftHand, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel model) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack renderItem = getBlockInfo().getItemStackForm();
        itemRenderer.render(renderItem, transformType, leftHand, matrixStack, buffer, combinedLight, combinedOverlay, getItemModel(renderItem));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderBlockDamage(BlockState state, BlockPos pos,
                                  BlockAndTintGetter blockReader,
                                  PoseStack poseStack,
                                  VertexConsumer vertexBuilder,
                                  IModelData modelData) {
        state = getState(state);
        if (state.getRenderShape() != RenderShape.INVISIBLE && ItemBlockRenderTypes.canRenderInLayer(state, MinecraftForgeClient.getRenderType())) {
            BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
            blockReader = new FacadeBlockAndTintGetter(blockReader, pos, state, getBlockEntity(blockReader, pos));
            brd.renderBreakingTexture(state, pos, blockReader, poseStack, vertexBuilder, modelData);
        }

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean renderModel(BlockState state, BlockPos pos,
                               BlockAndTintGetter blockReader,
                               PoseStack matrixStack,
                               VertexConsumer vertexBuilder, boolean checkSides,
                               Random rand, IModelData modelData) {
        state = getState(state);
        if (state.getRenderShape() != RenderShape.INVISIBLE && ItemBlockRenderTypes.canRenderInLayer(state, MinecraftForgeClient.getRenderType())) {
            BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
            blockReader = new FacadeBlockAndTintGetter(blockReader, pos, state, getBlockEntity(blockReader, pos));
            return brd.renderBatched(state, pos, blockReader, matrixStack, vertexBuilder, checkSides, rand, modelData);
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public BlockEntity getBlockEntity(BlockAndTintGetter world, BlockPos pos) {
        BlockInfo blockInfo = getBlockInfo();
        BlockEntity tile = blockInfo.getBlockEntity(pos);
        if (tile != null && world instanceof Level) {
            try {
                tile.setLevel(new FacadeBlockWorld((Level) world, pos, getState(world.getBlockState(pos)), tile));
            } catch (Throwable throwable) {
                blockInfo.setHasBlockEntity(false);
            }
        }
        return tile;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasTESR(BlockEntity tileEntity) {
        tileEntity = getBlockEntity(tileEntity.getLevel(), tileEntity.getBlockPos());
        if (tileEntity == null) {
            return false;
        }
        return Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(tileEntity) != null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isGlobalRenderer(BlockEntity tileEntity) {
        tileEntity = getBlockEntity(tileEntity.getLevel(), tileEntity.getBlockPos());
        if (tileEntity == null) return false;
        BlockEntityRenderer<BlockEntity> tesr = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(tileEntity);
        if (tesr != null) {
            return tesr.shouldRenderOffScreen(tileEntity);
        }
        return false;
    }

    @Override
    public void render(BlockEntity tileEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        tileEntity = getBlockEntity(tileEntity.getLevel(), tileEntity.getBlockPos());
        if (tileEntity == null) return;
        BlockEntityRenderer<BlockEntity> tesr = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(tileEntity);
        if (tesr != null) {
            try {
                tesr.render(tileEntity, partialTicks, stack, buffer, combinedLight, combinedOverlay);
            } catch (Exception e){
                getBlockInfo().setHasBlockEntity(false);
            }
        }
    }

    @Override
    public void onTextureSwitchEvent(TextureStitchEvent.Pre event) {
        itemModel = null;
    }
}
