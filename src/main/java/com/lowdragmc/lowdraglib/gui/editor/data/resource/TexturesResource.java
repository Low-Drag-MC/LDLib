package com.lowdragmc.lowdraglib.gui.editor.data.resource;

import com.lowdragmc.lowdraglib.gui.editor.annotation.AnnotationDetector;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurable;
import com.lowdragmc.lowdraglib.gui.editor.configurator.SelectorConfigurator;
import com.lowdragmc.lowdraglib.gui.editor.ui.ResourcePanel;
import com.lowdragmc.lowdraglib.gui.editor.ui.resource.ResourceContainer;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;

import static com.lowdragmc.lowdraglib.gui.editor.data.resource.TexturesResource.RESOURCE_NAME;

/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote TextureResource
 */
@RegisterUI(name = RESOURCE_NAME)
public class TexturesResource extends Resource<IGuiTexture> {

    public final static String RESOURCE_NAME = "ldlib.gui.editor.group.textures";

    @Override
    public void buildDefault() {
        data.put("empty", IGuiTexture.EMPTY);
        data.put("border background", ResourceBorderTexture.BORDERED_BACKGROUND);
        for (var wrapper : AnnotationDetector.REGISTER_TEXTURES) {
            data.put(wrapper.annotation().name(), wrapper.creator().get());
        }
    }

    @Override
    public String name() {
        return RESOURCE_NAME;
    }

    @Override
    public ResourceContainer<IGuiTexture, ImageWidget> createContainer(ResourcePanel panel) {
        ResourceContainer<IGuiTexture, ImageWidget> container = new ResourceContainer<>(this, panel);
        container.setWidgetSupplier(t -> new ImageWidget(0, 0, 30, 30, t))
                .setDraggingRenderer(o -> o)
                .setOnEdit(key -> openTextureConfigurator(key, container, getResource(key)));
        return container;
    }

    private void openTextureConfigurator(String key, ResourceContainer<IGuiTexture, ImageWidget> container, IGuiTexture current) {
        if (key.equals("empty")) return;
        container.getPanel().getEditor().getConfigPanel().openConfigurator(new IConfigurable() {
            @Override
            public void buildConfigurator(ConfiguratorGroup father) {
                AnnotationDetector.Wrapper<RegisterUI, IGuiTexture> defaultWrapper = null;
                for (var wrapper : AnnotationDetector.REGISTER_TEXTURES) {
                    if (wrapper.clazz() == current.getClass()) {
                        defaultWrapper = wrapper;
                    }
                }

                AnnotationDetector.Wrapper<RegisterUI, IGuiTexture> finalDefaultWrapper = defaultWrapper;
                SelectorConfigurator<AnnotationDetector.Wrapper<RegisterUI, IGuiTexture>> selectorConfigurator = new SelectorConfigurator<>(
                        "ldlib.gui.editor.name.texture_type",
                        () -> finalDefaultWrapper,
                        wrapper -> {
                            if (wrapper != finalDefaultWrapper) {
                                var newTexture = wrapper.creator().get();
                                addResource(key, newTexture);
                                container.getWidgets().get(key).setImage(newTexture);
                                openTextureConfigurator(key, container, newTexture);
                            }
                        },
                        finalDefaultWrapper,
                        false,
                        AnnotationDetector.REGISTER_TEXTURES,
                        w -> w.annotation().name()
                        );
                father.addConfigurators(selectorConfigurator);
                current.buildConfigurator(father);
            }

        });
    }
}
