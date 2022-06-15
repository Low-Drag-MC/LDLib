package com.lowdragmc.lowdraglib.client.model.custommodel;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lowdragmc.lowdraglib.LDLMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class LDLMetadataSection {
    public static final String SECTION_NAME = LDLMod.MODID;
    private static final Map<ResourceLocation, LDLMetadataSection> METADATA_CACHE = new HashMap<>();

    public final boolean emissive;
    private final RenderType layer;

    public LDLMetadataSection(boolean emissive, RenderType layer) {
        this.emissive = emissive;
        this.layer = layer;
    }


    @Nullable
    public static LDLMetadataSection getMetadata(ResourceLocation res) {
        if (METADATA_CACHE.containsKey(res)) {
            return METADATA_CACHE.get(res);
        }
        LDLMetadataSection ret;
        try (Resource resource = Minecraft.getInstance().getResourceManager().getResource(res)) {
            ret = resource.getMetadata(Serializer.INSTANCE);
        } catch (Exception e) {
            ret = null;
        }
        METADATA_CACHE.put(res, ret);
        return ret;
    }

    public static boolean isEmissive(TextureAtlasSprite sprite) {
        LDLMetadataSection ret = getMetadata(spriteToAbsolute(sprite.getName()));
        return ret != null && ret.emissive;
    }

    public static RenderType getLayer(TextureAtlasSprite sprite) {
        LDLMetadataSection ret = getMetadata(spriteToAbsolute(sprite.getName()));
        return ret == null ? null : ret.layer;
    }

    public static ResourceLocation spriteToAbsolute(ResourceLocation sprite) {
        if (!sprite.getPath().startsWith("textures/")) {
            sprite = new ResourceLocation(sprite.getNamespace(), "textures/" + sprite.getPath());
        }
        if (!sprite.getPath().endsWith(".png")) {
            sprite = new ResourceLocation(sprite.getNamespace(), sprite.getPath() + ".png");
        }
        return sprite;
    }

    public static class Serializer implements MetadataSectionSerializer<LDLMetadataSection> {
        static Serializer INSTANCE = new Serializer();

        @Override
        @Nonnull
        public String getMetadataSectionName() {
            return SECTION_NAME;
        }

        @Override
        @Nonnull
        public LDLMetadataSection fromJson(@Nonnull JsonObject json) {
            boolean emissive = false;
            RenderType layer = null;
            if (json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();
                if (obj.has("emissive")) {
                    JsonElement element = obj.get("emissive");
                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
                        emissive = element.getAsBoolean();
                    }
                }
                if (obj.has("layer")) {
                    JsonElement element = obj.get("emissive");
                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                        layer = RenderType.chunkBufferLayers().stream().filter(type->type.toString().equals(element.getAsString())).findAny().orElseGet(null);
                    }
                }
            }
            return new LDLMetadataSection(emissive, layer);
        }
    }
}
