package com.lowdragmc.lowdraglib.gui.editor.data.resource;

import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.ui.ResourcePanel;
import com.lowdragmc.lowdraglib.gui.editor.ui.resource.ResourceContainer;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;

import static com.lowdragmc.lowdraglib.gui.editor.data.resource.EntriesResource.RESOURCE_NAME;


/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote TextureResource
 */
@RegisterUI(name = RESOURCE_NAME)
public class EntriesResource extends Resource<String> {

    public final static String RESOURCE_NAME = "ldlib.gui.editor.group.entries";

    @Override
    public String name() {
        return RESOURCE_NAME;
    }

    @Override
    public void buildDefault() {
        data.put("ldlib.author", "Hello KilaBash!");
    }

    @Override
    public ResourceContainer<String, ImageWidget> createContainer(ResourcePanel panel) {
        ResourceContainer<String, ImageWidget> container = new ResourceContainer<>(this, panel);
        container.setWidgetSupplier(t -> new ImageWidget(0, 0, 65, 65, new TextTexture(t).setType(TextTexture.TextType.ROLL).setWidth(50)))
                .setDraggingRenderer(TextTexture::new);
        return container;
    }
}
