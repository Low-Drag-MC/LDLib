package com.lowdragmc.lowdraglib.client.model.custommodel;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.lowdragmc.lowdraglib.client.bakedpipeline.VertexBuilder;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Used to baked the model with emissive effect. or multi-layer
 *
 * Making the top layer emissive.
 */
@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CustomBakedModel implements IBakedModel {
    private final IBakedModel parent;
    private final Table<RenderType, Direction, List<BakedQuad>> sideCache;
    private final Map<RenderType, List<BakedQuad>> noSideCache;

    public CustomBakedModel(IBakedModel parent) {
        this.parent = parent;
        this.noSideCache = new ConcurrentHashMap<>(RenderType.chunkBufferLayers().size());
        this.sideCache = Tables.newCustomTable(new ConcurrentHashMap<>(RenderType.chunkBufferLayers().size()), ConcurrentHashMap::new);
    }

    public boolean shouldRenderInLayer(@Nullable BlockState state, Random rand) {
        if (!getQuads(state, null, rand).isEmpty()) return true;
        for (Direction side : Direction.values()) {
            if (!getQuads(state, side, rand).isEmpty()) return true;
        }
        return false;
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        RenderType currentLayer = MinecraftForgeClient.getRenderLayer();
        currentLayer = currentLayer == null ? RenderType.cutoutMipped() : currentLayer;
        if (side == null) {
            return noSideCache.computeIfAbsent(currentLayer, layer->reBake(layer, state, null, rand));
        } else {
            if (!sideCache.contains(currentLayer, side)) {
                sideCache.put(currentLayer, side, reBake(currentLayer, state, side, rand));
            }
            List<BakedQuad> quads = sideCache.get(currentLayer, side);
            return quads == null ? Collections.emptyList() : quads;
        }
    }


    @Nonnull
    public List<BakedQuad> reBake(RenderType currentLayer, @Nullable BlockState state, @Nullable Direction side, Random rand) {
        List<BakedQuad> parentQuads = parent.getQuads(state, side, rand);
        List<BakedQuad> resultQuads = new LinkedList<>();
        for (BakedQuad quad : parentQuads) {
            TextureAtlasSprite sprite = quad.getSprite();
            boolean isEmissive = LDLMetadataSection.isEmissive(sprite);
            RenderType layer = LDLMetadataSection.getLayer(sprite);
            layer = layer == null ? parent.getClass().getName().contains("MultiLayer") ? currentLayer : RenderType.cutoutMipped() : layer;
            if (currentLayer != layer) continue;
            if (isEmissive) {
                quad = reBakeEmissive(quad);
            }
            resultQuads.add(quad);
        }
        return resultQuads;
    }

    public static BakedQuad reBakeEmissive(BakedQuad quad) {
        VertexBuilder builder = new VertexBuilder(DefaultVertexFormats.BLOCK, quad.getSprite());
        quad.pipe(builder);
        VertexFormat format = builder.vertexFormat;

        BakedQuadBuilder unpackedBuilder = new BakedQuadBuilder();
        unpackedBuilder.setQuadOrientation(builder.quadOrientation);
        unpackedBuilder.setQuadTint(builder.quadTint);
        unpackedBuilder.setApplyDiffuseLighting(builder.applyDiffuseLighting);
        unpackedBuilder.setTexture(builder.sprite);

        for (int v = 0; v < 4; v++) {
            for (int i = 0; i < format.getElements().size(); i++) {
                VertexFormatElement ele = format.getElements().get(i);
                if (ele == DefaultVertexFormats.ELEMENT_UV2) {
                    unpackedBuilder.put(i, (15<<4)/32768.0f, (15<<4)/32768.0f, 0, 1);
                } else {
                    unpackedBuilder.put(i, builder.data.get(ele).get(v));
                }
            }
        }
        return unpackedBuilder.build();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return parent.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return parent.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return parent.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return parent.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return parent.getParticleIcon();
    }

    @Override
    public ItemCameraTransforms getTransforms() {
        return parent.getTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return parent.getOverrides();
    }
}
