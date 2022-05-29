package com.lowdragmc.lowdraglib.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.mojang.datafixers.util.Pair;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 * @author KilaBash
 * @date 2022/05/28
 * @implNote LDLModel, use vanilla way to improve model rendering
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LDLRendererModel implements IModelGeometry<LDLRendererModel> {

    public static final LDLRendererModel INSTANCE = new LDLRendererModel();

    private LDLRendererModel() {}

    @Override
    public Collection<? extends IModelGeometryPart> getParts() {
        return IModelGeometry.super.getParts();
    }

    @Override
    public Optional<? extends IModelGeometryPart> getPart(String name) {
        return IModelGeometry.super.getPart(name);
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery,
                            Function<RenderMaterial, TextureAtlasSprite> spriteGetter,
                            IModelTransform modelTransform,
                            ItemOverrideList overrides,
                            ResourceLocation modelLocation) {
        return new RendererBakedModel();
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner,
                                                  Function<ResourceLocation, IUnbakedModel> modelGetter,
                                                  Set<Pair<String, String>> missingTextureErrors) {
        return Collections.emptyList();
    }

    public static final class RendererBakedModel implements IBakedModel {

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pSide, Random pRand) {
            return Collections.emptyList();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return false;
        }

        @Override
        public boolean isGui3d() {
            return true;
        }

        @Override
        public boolean usesBlockLight() {
            return false;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(MissingTextureSprite.getLocation());
        }

        @Override
        public ItemOverrideList getOverrides() {
            return ItemOverrideList.EMPTY;
        }

        // forge

        public static final ModelProperty<IRenderer> IRENDERER = new ModelProperty<>();
        public static final ModelProperty<IBlockDisplayReader> WORLD = new ModelProperty<>();
        public static final ModelProperty<BlockPos> POS = new ModelProperty<>();
        public static final ModelProperty<IModelData> MODEL_DATA = new ModelProperty<>();

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state,
                                        @Nullable Direction side,
                                        @Nonnull Random rand,
                                        @Nonnull IModelData extraData) {
            if (extraData instanceof ModelDataMap) {
                IRenderer renderer = extraData.getData(IRENDERER);
                IBlockDisplayReader world = extraData.getData(WORLD);
                BlockPos pos = extraData.getData(POS);
                IModelData modelData = extraData.getData(MODEL_DATA);
                if (renderer != null) {
                    return renderer.renderModel(world, pos, state, side, rand, modelData);
                }
            }
            return Collections.emptyList();
        }

        @Nonnull
        @Override
        public IModelData getModelData(@Nonnull IBlockDisplayReader world,
                                       @Nonnull BlockPos pos,
                                       @Nonnull BlockState state,
                                       @Nonnull IModelData tileData) {
            if (state.getBlock() instanceof IBlockRendererProvider) {
                IBlockRendererProvider rendererProvider = (IBlockRendererProvider) state.getBlock();
                IRenderer renderer = rendererProvider.getRenderer(state, pos, world);
                if (renderer != null) {
                    tileData = new ModelDataMap.Builder()
                            .withInitial(IRENDERER, renderer)
                            .withInitial(WORLD, world)
                            .withInitial(POS, pos)
                            .withInitial(MODEL_DATA, tileData)
                            .build();
                }
            }
            return tileData;
        }

        @Override
        public TextureAtlasSprite getParticleTexture(@Nonnull IModelData extraData) {
            if (extraData instanceof ModelDataMap) {
                IRenderer renderer = extraData.getData(IRENDERER);
                if (renderer != null) {
                    return renderer.getParticleTexture();
                }
            }
            return IBakedModel.super.getParticleTexture(extraData);
        }

    }

    public static final class Loader implements IModelLoader<LDLRendererModel> {

        public static final LDLRendererModel.Loader INSTANCE = new LDLRendererModel.Loader();

        private Loader() {}


        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {

        }

        @Override
        public LDLRendererModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
            return LDLRendererModel.INSTANCE;
        }
    }
}
