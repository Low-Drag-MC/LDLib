package com.lowdragmc.lowdraglib.client.renderer.impl;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.client.model.ModelFactory;
import com.lowdragmc.lowdraglib.client.model.custommodel.CustomBakedModel;
import com.lowdragmc.lowdraglib.client.renderer.IItemRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class IModelRenderer implements IRenderer {

    protected static final Set<ResourceLocation> CACHE = new HashSet<>();

    public final ResourceLocation modelLocation;
    public final boolean useCustomBakedModel;
    @OnlyIn(Dist.CLIENT)
    protected BakedModel itemModel;
    @OnlyIn(Dist.CLIENT)
    protected Map<Direction, BakedModel> blockModels;

    protected IModelRenderer() {
        modelLocation = null;
        useCustomBakedModel = false;
    }

    public IModelRenderer(ResourceLocation modelLocation) {
        this(modelLocation, false);
    }

    public IModelRenderer(ResourceLocation modelLocation, boolean useCustomBakedModel) {
        this.modelLocation = modelLocation;
        this.useCustomBakedModel = useCustomBakedModel;
        if (LDLMod.isClient()) {
            blockModels = new ConcurrentHashMap<>();
            if (isRaw()) {
                registerTextureSwitchEvent();
                CACHE.add(modelLocation);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    public TextureAtlasSprite getParticleTexture() {
        BakedModel model = getItemBakedModel();
        if (model == null) {
            return IRenderer.super.getParticleTexture();
        }
        return model.getParticleIcon(EmptyModelData.INSTANCE);
    }

    @Override
    public boolean isRaw() {
        return !CACHE.contains(modelLocation);
    }

    @OnlyIn(Dist.CLIENT)
    protected UnbakedModel getModel() {
        return ModelFactory.getUnBakedModel(modelLocation);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderItem(ItemStack stack,
                           ItemTransforms.TransformType transformType,
                           boolean leftHand, PoseStack matrixStack,
                           MultiBufferSource buffer, int combinedLight,
                           int combinedOverlay, BakedModel model) {
        IItemRendererProvider.disabled.set(true);
        model = getItemBakedModel(stack);
        if (model != null) {
            Minecraft.getInstance().getItemRenderer().render(stack, transformType, leftHand, matrixStack, buffer, combinedLight, combinedOverlay, model);
        }
        IItemRendererProvider.disabled.set(false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<BakedQuad> renderModel(BlockAndTintGetter level, BlockPos pos, BlockState state, Direction side, Random rand, IModelData modelData) {
        BakedModel ibakedmodel = getBlockBakedModel(pos, level);
        if (ibakedmodel == null) return Collections.emptyList();
        if (ibakedmodel instanceof CustomBakedModel && !((CustomBakedModel) ibakedmodel).shouldRenderInLayer(state, rand)) return Collections.emptyList();
        return ibakedmodel.getQuads(state, side, rand, modelData);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    protected BakedModel getItemBakedModel() {
        if (itemModel == null) {
            itemModel = getModel().bake(
                    ForgeModelBakery.instance(),
                    ForgeModelBakery.defaultTextureGetter(),
                    BlockModelRotation.X0_Y0,
                    modelLocation);
        }
        return itemModel;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    protected BakedModel getItemBakedModel(ItemStack itemStack) {
        return getItemBakedModel();
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    protected BakedModel getBlockBakedModel(BlockPos pos, BlockAndTintGetter blockAccess) {
        BlockState blockState = blockAccess.getBlockState(pos);
        Direction frontFacing = Direction.NORTH;
        if (blockState.hasProperty(BlockStateProperties.FACING)) {
            frontFacing = blockState.getValue(BlockStateProperties.FACING);
        } else if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            frontFacing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        }
        return blockModels.computeIfAbsent(frontFacing, facing -> {
            BakedModel model = getModel().bake(
                    ForgeModelBakery.instance(),
                    ForgeModelBakery.defaultTextureGetter(),
                    ModelFactory.getRotation(facing),
                    modelLocation);
            return model == null ? null : useCustomBakedModel ? new CustomBakedModel(model) : model;
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTextureSwitchEvent(TextureStitchEvent.Pre event) {
        itemModel = null;
        blockModels.clear();
        UnbakedModel model = getModel();
        for (Material material : model.getMaterials(ModelFactory::getUnBakedModel, new HashSet<>())) {
            event.addSprite(material.texture());
        }
    }

}
