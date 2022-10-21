package com.lowdragmc.lowdraglib.json;

import com.google.gson.JsonObject;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.texture.ShaderTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author KilaBash
 * @date 2022/10/20
 * @implNote IGuiTextureJsonUtils
 */
public class SimpleIGuiTextureJsonUtils {

    public static JsonObject toJson(IGuiTexture texture) {
        JsonObject jsonObject = new JsonObject();
        if (texture instanceof ResourceTexture resourceTexture) {
            jsonObject.addProperty("type", "resource");
            jsonObject.addProperty("res", resourceTexture.imageLocation.toString());
        } else if (texture instanceof ItemStackTexture itemStackTexture && itemStackTexture.itemStack.length > 0) {
            jsonObject.addProperty("type", "item");
            jsonObject.addProperty("res", itemStackTexture.itemStack[0].getItem().getRegistryName().toString());
        } else if (texture instanceof ShaderTexture shaderTexture && shaderTexture.location != null) {
            jsonObject.addProperty("type", "shader");
            jsonObject.addProperty("res", shaderTexture.location.toString());
        }
        return jsonObject;
    }

    public static IGuiTexture fromJson(JsonObject jsonObject) {
        return switch (jsonObject.get("type").getAsString()) {
            case "resource" -> new ResourceTexture(jsonObject.get("res").getAsString());
            case "item" -> new ItemStackTexture(ForgeRegistries.ITEMS.getValue(new ResourceLocation(jsonObject.get("res").getAsString())));
            case "shader" -> ShaderTexture.createShader(new ResourceLocation(jsonObject.get("res").getAsString()));
            default -> IGuiTexture.EMPTY;
        };
    }
}
