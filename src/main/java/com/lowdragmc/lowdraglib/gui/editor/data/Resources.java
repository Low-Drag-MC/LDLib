package com.lowdragmc.lowdraglib.gui.editor.data;

import com.lowdragmc.lowdraglib.gui.editor.annotation.AnnotationDetector;
import com.lowdragmc.lowdraglib.gui.editor.data.resource.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote Resource
 */
public class Resources {
    public Map<String, Resource<?>> resources = new HashMap<>();

    public static Resources defaultResource() { // default
        Resources resources = new Resources();
        for (var wrapper : AnnotationDetector.REGISTER_RESOURCES) {
            var resource =  wrapper.creator().get();
            resource.buildDefault();
            resources.resources.put(resource.name(), resource);
        }
        return resources;
    }

}
