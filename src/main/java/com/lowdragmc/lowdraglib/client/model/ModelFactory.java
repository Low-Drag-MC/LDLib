package com.lowdragmc.lowdraglib.client.model;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ForgeModelBakery;

/**
 * Author: KilaBash
 * Date: 2022/04/24
 * Description:
 */
@OnlyIn(Dist.CLIENT)
public class ModelFactory {

    public static UnbakedModel getUnBakedModel(ResourceLocation modelLocation) {
        return ForgeModelBakery.instance().getModelOrMissing(modelLocation);
    }

    public static BlockModelRotation getRotation(Direction facing) {
        switch (facing) {
            case DOWN:  return BlockModelRotation.X90_Y0;
            case UP:    return BlockModelRotation.X270_Y0;
            case NORTH: return BlockModelRotation.X0_Y0;
            case SOUTH: return BlockModelRotation.X0_Y180;
            case WEST:  return BlockModelRotation.X0_Y270;
            case EAST:  return BlockModelRotation.X0_Y90;
        }
        throw new IllegalArgumentException(String.valueOf(facing));
    }

    public static Either<Material, String> parseTextureLocationOrReference(ResourceLocation pLocation, String pName) {
        if (isTextureReference(pName)) {
            return Either.right(pName.substring(1));
        } else {
            ResourceLocation resourcelocation = ResourceLocation.tryParse(pName);
            if (resourcelocation == null) {
                throw new JsonParseException(pName + " is not valid resource location");
            } else {
                return Either.left(new Material(pLocation, resourcelocation));
            }
        }
    }

    public static Either<Material, String> parseBlockTextureLocationOrReference(String pName) {
        if (isTextureReference(pName)) {
            return Either.right(pName.substring(1));
        } else {
            ResourceLocation resourcelocation = ResourceLocation.tryParse(pName);
            if (resourcelocation == null) {
                throw new JsonParseException(pName + " is not valid resource location");
            } else {
                return Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, resourcelocation));
            }
        }
    }

    private static boolean isTextureReference(String pStr) {
        return pStr.charAt(0) == '#';
    }
}
