package com.lowdragmc.lowdraglib.client.model;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;

/**
 * Author: KilaBash
 * Date: 2022/04/24
 * Description:
 */
@OnlyIn(Dist.CLIENT)
public class ModelFactory {

    public static IUnbakedModel getUnBakedModel(ResourceLocation modelLocation) {
        return ModelLoader.instance().getModelOrMissing(modelLocation);
    }

    public static ModelRotation getRotation(Direction facing) {
        switch (facing) {
            case DOWN:  return ModelRotation.X90_Y0;
            case UP:    return ModelRotation.X270_Y0;
            case NORTH: return ModelRotation.X0_Y0;
            case SOUTH: return ModelRotation.X0_Y180;
            case WEST:  return ModelRotation.X0_Y270;
            case EAST:  return ModelRotation.X0_Y90;
        }
        throw new IllegalArgumentException(String.valueOf(facing));
    }

    public static Either<RenderMaterial, String> parseTextureLocationOrReference(ResourceLocation pLocation, String pName) {
        if (isTextureReference(pName)) {
            return Either.right(pName.substring(1));
        } else {
            ResourceLocation resourcelocation = ResourceLocation.tryParse(pName);
            if (resourcelocation == null) {
                throw new JsonParseException(pName + " is not valid resource location");
            } else {
                return Either.left(new RenderMaterial(pLocation, resourcelocation));
            }
        }
    }

    public static Either<RenderMaterial, String> parseBlockTextureLocationOrReference(String pName) {
        if (isTextureReference(pName)) {
            return Either.right(pName.substring(1));
        } else {
            ResourceLocation resourcelocation = ResourceLocation.tryParse(pName);
            if (resourcelocation == null) {
                throw new JsonParseException(pName + " is not valid resource location");
            } else {
                return Either.left(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, resourcelocation));
            }
        }
    }

    private static boolean isTextureReference(String pStr) {
        return pStr.charAt(0) == '#';
    }
}
