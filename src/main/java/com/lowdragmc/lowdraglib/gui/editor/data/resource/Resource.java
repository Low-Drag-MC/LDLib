package com.lowdragmc.lowdraglib.gui.editor.data.resource;

import com.lowdragmc.lowdraglib.gui.editor.annotation.AnnotationDetector;
import com.lowdragmc.lowdraglib.gui.editor.ui.ResourcePanel;
import com.lowdragmc.lowdraglib.gui.editor.ui.resource.ResourceContainer;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import org.checkerframework.checker.units.qual.C;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote Resource
 */
public abstract class Resource<T> {
    protected final Map<String, T> data = new LinkedHashMap<>();

    public void buildDefault() {

    }

    public void removeResource(String key) {
        data.remove(key);
    }

    public boolean hasResource(String key) {
        return data.containsKey(key);
    }

    public void addResource(String key, T resource) {
        data.put(key, resource);
    }

    public Set<Map.Entry<String, T>> allResources() {
        return data.entrySet();
    }

    public T getResource(String key) {
        return data.get(key);
    }

    public abstract String name();

    public abstract ResourceContainer<T, ? extends Widget> createContainer(ResourcePanel panel);

}
