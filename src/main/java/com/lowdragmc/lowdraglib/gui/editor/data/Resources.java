package com.lowdragmc.lowdraglib.gui.editor.data;

import com.lowdragmc.lowdraglib.gui.editor.runtime.UIDetector;
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
        for (var wrapper : UIDetector.REGISTER_RESOURCES) {
            var resource =  wrapper.creator().get();
            resource.buildDefault();
            resources.resources.put(resource.name(), resource);
        }
        return resources;
    }

    public void load() {
        resources.values().forEach(Resource::onLoad);
    }

    public void dispose() {
        resources.values().forEach(Resource::unLoad);
    }

}
