package com.lowdragmc.lowdraglib.gui.editor.ui.resource;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.data.resource.Resource;
import com.lowdragmc.lowdraglib.gui.editor.ui.ResourcePanel;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.SelectableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.utils.Size;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author KilaBash
 * @date 2022/12/5
 * @implNote EntriesResourceContainer
 */
public class EntriesResourceContainer extends ResourceContainer<String, TextFieldWidget>{

    public EntriesResourceContainer(Resource<String> resource, ResourcePanel panel) {
        super(resource, panel);
        setDraggingMapping(key -> key);
        setDraggingRenderer(TextTexture::new);
        setOnAdd(key -> "Hello KilaBash!");
        setNameSupplier(() -> {
            String randomName = "new.";
            int i = 0;
            while (getResource().hasResource(randomName + i)) {
                i++;
            }
            randomName += i;
            return randomName;
        });
    }

    @Override
    public void reBuild() {
        selected = null;
        container.clearAllWidgets();
        int width = (getSize().getWidth() - 6) / 2;
        int i = 0;
        for (var entry : resource.allResources()) {
            TextFieldWidget widget = new TextFieldWidget(width, 0, width, 15, null, s -> resource.addResource(entry.getKey(), s));
            widget.setCurrentString(entry.getValue());
            widget.setBordered(false);
            widget.setBackground(ColorPattern.T_WHITE.rectTexture());

            widgets.put(entry.getKey(), widget);
            Size size = widget.getSize();
            SelectableWidgetGroup selectableWidgetGroup = new SelectableWidgetGroup(3, 3 + i * 17, width * 2, 15) {
                @Override
                @OnlyIn(Dist.CLIENT)
                public boolean mouseClicked(double mouseX, double mouseY, int button) {
                    draggingElement = null;
                    tryToDrag = draggingProvider != null && isMouseOverElement(mouseX, mouseY);
                    return super.mouseClicked(mouseX, mouseY, button);
                }
            };
            selectableWidgetGroup.setDraggingProvider(draggingMapping == null ? entry::getValue : () -> draggingMapping.apply(entry.getKey()), draggingRenderer);
            selectableWidgetGroup.addWidget(new ImageWidget(0, 0, width, 15, new GuiTextureGroup(
                    ColorPattern.GRAY.rectTexture(),
                    new TextTexture("" + entry.getKey() + " ").setWidth(size.width).setType(TextTexture.TextType.ROLL))));
            selectableWidgetGroup.addWidget(widget);
            selectableWidgetGroup.setOnSelected(s -> selected = entry.getKey());
            selectableWidgetGroup.setOnUnSelected(s -> selected = null);
            selectableWidgetGroup.setSelectedTexture(ColorPattern.T_GRAY.rectTexture());
            container.addWidget(selectableWidgetGroup);
            i++;
        }
    }

    @Override
    protected TreeBuilder.Menu getMenu() {
        return TreeBuilder.Menu.start()
                .leaf("ldlib.gui.editor.menu.add_resource", this::addNewResource)
                .leaf("ldlib.gui.editor.menu.remove", this::removeSelectedResource)
                .leaf("ldlib.gui.editor.menu.rename", this::renameResource);
    }
}
