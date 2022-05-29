package com.lowdragmc.lowdraglib.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public BakedModel bake(IModelConfiguration owner,
                           ModelBakery bakery,
                           Function<Material, TextureAtlasSprite> spriteGetter,
                           ModelState modelTransform,
                           ItemOverrides overrides,
                           ResourceLocation modelLocation) {
        return new RendererBakedModel();
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner,
                                            Function<ResourceLocation, UnbakedModel> modelGetter,
                                            Set<Pair<String, String>> missingTextureErrors) {
        return Collections.emptyList();
    }

    public static final class RendererBakedModel implements BakedModel {

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
            return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(MissingTextureAtlasSprite.getLocation());
        }

        @Override
        public ItemTransforms getTransforms() {
            return BakedModel.super.getTransforms();
        }

        @Override
        public ItemOverrides getOverrides() {
            return ItemOverrides.EMPTY;
        }

        // forge

        public static final ModelProperty<IRenderer> IRENDERER = new ModelProperty<>();
        public static final ModelProperty<BlockAndTintGetter> WORLD = new ModelProperty<>();
        public static final ModelProperty<BlockPos> POS = new ModelProperty<>();
        public static final ModelProperty<IModelData> MODEL_DATA = new ModelProperty<>();

        @NotNull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state,
                                        @Nullable Direction side,
                                        @NotNull Random rand,
                                        @NotNull IModelData extraData) {
            if (extraData instanceof ModelDataMap) {
                IRenderer renderer = extraData.getData(IRENDERER);
                BlockAndTintGetter world = extraData.getData(WORLD);
                BlockPos pos = extraData.getData(POS);
                IModelData modelData = extraData.getData(MODEL_DATA);
                if (renderer != null) {
                    return renderer.renderModel(world, pos, state, side, rand, modelData);
                }
            }
            return Collections.emptyList();
        }

        @Override
        public boolean useAmbientOcclusion(BlockState state) {
            return BakedModel.super.useAmbientOcclusion(state);
        }

        @NotNull
        @Override
        public IModelData getModelData(@NotNull BlockAndTintGetter level,
                                       @NotNull BlockPos pos,
                                       @NotNull BlockState state,
                                       @NotNull IModelData modelData) {
            if (state.getBlock() instanceof IBlockRendererProvider rendererProvider) {
                IRenderer renderer = rendererProvider.getRenderer(state, pos, level);
                if (renderer != null) {
                    modelData = new ModelDataMap.Builder()
                            .withInitial(IRENDERER, renderer)
                            .withInitial(WORLD, level)
                            .withInitial(POS, pos)
                            .withInitial(MODEL_DATA, modelData)
                            .build();
                }
            }
            return modelData;
        }

        @Override
        public TextureAtlasSprite getParticleIcon(@NotNull IModelData extraData) {
            if (extraData instanceof ModelDataMap) {
                IRenderer renderer = extraData.getData(IRENDERER);
                if (renderer != null) {
                    return renderer.getParticleTexture();
                }
            }
            return BakedModel.super.getParticleIcon(extraData);
        }

    }

    public static final class Loader implements IModelLoader<LDLRendererModel> {

        public static final LDLRendererModel.Loader INSTANCE = new LDLRendererModel.Loader();

        private Loader() {}

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {

        }

        @Override
        public LDLRendererModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
            return LDLRendererModel.INSTANCE;
        }
    }
}
