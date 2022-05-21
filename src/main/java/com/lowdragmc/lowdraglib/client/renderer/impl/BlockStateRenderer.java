package com.lowdragmc.lowdraglib.client.renderer.impl;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.client.utils.FacadeBlockDisplayReader;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.lowdragmc.lowdraglib.utils.FacadeBlockWorld;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
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
    private IBakedModel itemModel;

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
    protected IBakedModel getItemModel(ItemStack renderItem) {
        if (itemModel == null) {
            itemModel = Minecraft.getInstance().getItemRenderer().getModel(renderItem, null, null);
        }
        return itemModel;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    public TextureAtlasSprite getParticleTexture() {
        ItemStack renderItem = getBlockInfo().getItemStackForm();
        IBakedModel model = getItemModel(renderItem);
        if (model == null) {
            return IRenderer.super.getParticleTexture();
        }
        return model.getParticleTexture(EmptyModelData.INSTANCE);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, boolean leftHand, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, IBakedModel model) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack renderItem = getBlockInfo().getItemStackForm();
        itemRenderer.render(renderItem, transformType, leftHand, matrixStack, buffer, combinedLight, combinedOverlay, getItemModel(renderItem));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderBlockDamage(BlockState state, BlockPos pos,
                                  IBlockDisplayReader blockReader,
                                  MatrixStack matrixStack,
                                  IVertexBuilder vertexBuilder,
                                  IModelData modelData) {
        state = getState(state);
        if (state.getRenderShape() != BlockRenderType.INVISIBLE && RenderTypeLookup.canRenderInLayer(state, MinecraftForgeClient.getRenderLayer())) {
            BlockRendererDispatcher brd = Minecraft.getInstance().getBlockRenderer();
            blockReader = new FacadeBlockDisplayReader(blockReader, pos, state, blockReader instanceof World ? getTileEntity((World) blockReader, pos) : blockInfo.getTileEntity());
            brd.renderBlockDamage(state, pos, blockReader, matrixStack, vertexBuilder, modelData);
        }

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean renderModel(BlockState state,
                               BlockPos pos,
                               IBlockDisplayReader blockReader,
                               MatrixStack matrixStack,
                               IVertexBuilder vertexBuilder, boolean checkSides,
                               Random rand, IModelData modelData) {
        state = getState(state);
        if (state.getRenderShape() != BlockRenderType.INVISIBLE && RenderTypeLookup.canRenderInLayer(state, MinecraftForgeClient.getRenderLayer())) {
            BlockRendererDispatcher brd = Minecraft.getInstance().getBlockRenderer();
            blockReader = new FacadeBlockDisplayReader(blockReader, pos, state, blockReader instanceof World ? getTileEntity((World) blockReader, pos) : blockInfo.getTileEntity());
            return brd.renderModel(state, pos, blockReader, matrixStack, vertexBuilder, checkSides, rand, modelData);
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public TileEntity getTileEntity(World world, BlockPos pos) {
        BlockInfo blockInfo = getBlockInfo();
        TileEntity tile = blockInfo.getTileEntity();
        if (tile != null && world != null) {
            try {
                tile.setLevelAndPosition(new FacadeBlockWorld(world, pos, getState(world.getBlockState(pos)), tile), pos);
            } catch (Throwable throwable) {
                blockInfo.setTileEntity(null);
            }
        }
        return tile;
    }

    @Override
    public boolean hasTESR(TileEntity tileEntity) {
        tileEntity = getTileEntity(tileEntity.getLevel(), tileEntity.getBlockPos());
        if (tileEntity == null) {
            return false;
        }
        return TileEntityRendererDispatcher.instance.getRenderer(tileEntity) != null;
    }

    @Override
    public boolean isGlobalRenderer(TileEntity tileEntity) {
        tileEntity = getTileEntity(tileEntity.getLevel(), tileEntity.getBlockPos());
        if (tileEntity == null) return false;
        TileEntityRenderer<TileEntity> tesr = TileEntityRendererDispatcher.instance.getRenderer(tileEntity);
        if (tesr != null) {
            return tesr.shouldRenderOffScreen(tileEntity);
        }
        return false;
    }

    @Override
    public void render(TileEntity tileEntity, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        tileEntity = getTileEntity(tileEntity.getLevel(), tileEntity.getBlockPos());
        if (tileEntity == null) return;
        TileEntityRenderer<TileEntity> tesr = TileEntityRendererDispatcher.instance.getRenderer(tileEntity);
        if (tesr != null) {
            try {
                tesr.render(tileEntity, partialTicks, stack, buffer, combinedLight, combinedOverlay);
            } catch (Exception e){
                getBlockInfo().setTileEntity(null);
            }
        }
    }

    @Override
    public void onTextureSwitchEvent(TextureStitchEvent.Pre event) {
        itemModel = null;
    }
}
