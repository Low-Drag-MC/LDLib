package com.lowdragmc.lowdraglib.gui.editor.data.resource;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.ui.ResourcePanel;
import com.lowdragmc.lowdraglib.gui.editor.ui.resource.ResourceContainer;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;

import static com.lowdragmc.lowdraglib.gui.editor.data.resource.ColorsResource.RESOURCE_NAME;

/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote TextureResource
 */
@RegisterUI(name = RESOURCE_NAME)
public class ColorsResource extends Resource<Integer> {

    public final static String RESOURCE_NAME = "ldlib.gui.editor.group.colors";


    @Override
    public void buildDefault() {
        for (ColorPattern color : ColorPattern.values()) {
            data.put(color.name(), color.color);
        }
    }

    @Override
    public String name() {
        return RESOURCE_NAME;
    }

    @Override
    public ResourceContainer<Integer, ImageWidget> createContainer(ResourcePanel panel) {
        ResourceContainer<Integer, ImageWidget> container = new ResourceContainer<>(this, panel);
        container.setWidgetSupplier(t -> new ImageWidget(0, 0, 30, 30, new ColorRectTexture(t)))
                .setDraggingRenderer(ColorRectTexture::new)
                .setOnAdd(key -> -1);
        return container;
    }
}
