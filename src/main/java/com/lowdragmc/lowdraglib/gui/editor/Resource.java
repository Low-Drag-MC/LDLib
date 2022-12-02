package com.lowdragmc.lowdraglib.gui.editor;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote Resource
 */
public class Resource {
    Map<String, IGuiTexture> textures = new LinkedHashMap<>();
    Map<String, Integer> colors = new LinkedHashMap<>();
    Map<String, String> entries = new LinkedHashMap<>();

    public static Resource defaultResource() { // default
        Resource resource = new Resource();
        resource.textures.put("border background", ResourceBorderTexture.BORDERED_BACKGROUND);
        for (ColorPattern color : ColorPattern.values()) {
            resource.colors.put(color.name(), color.color);
        }
        resource.entries.put("ldlib.author", "Hello KilaBash!");
        return resource;
    }

}
