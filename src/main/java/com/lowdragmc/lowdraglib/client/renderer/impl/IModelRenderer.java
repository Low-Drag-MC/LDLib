package com.lowdragmc.lowdraglib.client.renderer.impl;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.client.model.ModelFactory;
import com.lowdragmc.lowdraglib.client.renderer.IItemRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class IModelRenderer implements IRenderer {

//    protected static final Set<ResourceLocation> CACHE = new HashSet<>();

    public final ResourceLocation modelLocation;
    @OnlyIn(Dist.CLIENT)
    protected IBakedModel itemModel;
    @OnlyIn(Dist.CLIENT)
    protected Map<Direction, IBakedModel> blockModels;

    protected IModelRenderer() {
        modelLocation = null;
    }

    public IModelRenderer(ResourceLocation modelLocation) {
        this.modelLocation = modelLocation;
        if (FMLEnvironment.dist == Dist.CLIENT) {
            registerTextureSwitchEvent();
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected IUnbakedModel getModel() {
        return ModelFactory.getUnBakedModel(modelLocation);
    }

    @Override
    public void renderItem(ItemStack stack,
                           ItemCameraTransforms.TransformType transformType,
                           boolean leftHand, MatrixStack matrixStack,
                           IRenderTypeBuffer buffer, int combinedLight,
                           int combinedOverlay, IBakedModel model) {
        IItemRendererProvider.disabled.set(true);
        Minecraft.getInstance().getItemRenderer().render(stack, transformType, leftHand, matrixStack, buffer, combinedLight, combinedOverlay, getItemBakedModel());
        IItemRendererProvider.disabled.set(false);
    }

    @Override
    public void renderBlockDamage(BlockState state, BlockPos pos,
                                  IBlockDisplayReader blockReader,
                                  MatrixStack matrixStack,
                                  IVertexBuilder vertexBuilder,
                                  IModelData modelData) {
        BlockRendererDispatcher brd = Minecraft.getInstance().getBlockRenderer();
        IBakedModel ibakedmodel = getBlockBakedModel(pos, blockReader);
        brd.getModelRenderer().renderModel(blockReader, ibakedmodel, state, pos, matrixStack, vertexBuilder, true, LDLMod.random, state.getSeed(pos), OverlayTexture.NO_OVERLAY, modelData);
    }

    @Override
    public boolean renderModel(BlockState state, BlockPos pos,
                               IBlockDisplayReader blockReader,
                               MatrixStack matrixStack,
                               IVertexBuilder vertexBuilder, boolean checkSides,
                               Random rand, IModelData modelData) {
        RenderType layer = MinecraftForgeClient.getRenderLayer();
        if (layer != RenderType.cutoutMipped()) return false;
        BlockRendererDispatcher brd = Minecraft.getInstance().getBlockRenderer();
        IBakedModel ibakedmodel = getBlockBakedModel(pos, blockReader);
        return brd.getModelRenderer().renderModel(blockReader, ibakedmodel, state, pos, matrixStack, vertexBuilder, checkSides, rand, state.getSeed(pos), OverlayTexture.NO_OVERLAY, modelData);
    }

    @OnlyIn(Dist.CLIENT)
    protected IBakedModel getItemBakedModel() {
        if (itemModel == null) {
            itemModel = getModel().bake(
                    ModelLoader.instance(),
                    ModelLoader.defaultTextureGetter(),
                    ModelRotation.X0_Y0,
                    modelLocation);
        }
        return itemModel;
    }

    @OnlyIn(Dist.CLIENT)
    protected IBakedModel getBlockBakedModel(BlockPos pos, IBlockDisplayReader blockAccess) {
        BlockState blockState = blockAccess.getBlockState(pos);
        Direction frontFacing = Direction.NORTH;
        if (blockState.hasProperty(BlockStateProperties.FACING)) {
            frontFacing = blockState.getValue(BlockStateProperties.FACING);
        } else if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            frontFacing = blockState.getValue(BlockStateProperties.FACING);
        }
        return blockModels.computeIfAbsent(frontFacing, facing -> getModel().bake(
                ModelLoader.instance(),
                ModelLoader.defaultTextureGetter(),
                ModelFactory.getRotation(facing),
                modelLocation));
    }

    @Override
    public void onTextureSwitchEvent(TextureStitchEvent.Pre event) {
        itemModel = null;
        blockModels.clear();
        IUnbakedModel model = getModel();
        for (RenderMaterial material : model.getMaterials(ModelFactory::getUnBakedModel, new HashSet<>())) {
            event.addSprite(material.texture());
        }
    }

}
