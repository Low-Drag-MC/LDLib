package com.lowdragmc.lowdraglib.gui.editor.ui.resource;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.data.resource.Resource;
import com.lowdragmc.lowdraglib.gui.editor.ui.ResourcePanel;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote ResourceContainer
 */
@Accessors(chain = true)
public class ResourceContainer<T, C extends Widget> extends WidgetGroup {
    @Getter
    private final ResourcePanel panel;
    private final Resource<T> resource;
    @Getter
    private final Map<String, C> widgets;
    private DraggableScrollableWidgetGroup container;
    @Setter @Getter
    private Function<T, C> widgetSupplier;
    @Setter
    private Function<String, T> onAdd;
    @Setter
    private Consumer<String> onRemove;
    @Setter
    private Consumer<String> onEdit;
    @Setter
    private Function<T, IGuiTexture> draggingRenderer;

    public ResourceContainer(Resource<T> resource, ResourcePanel panel) {
        super(3, 0, panel.getSize().width - 6, panel.getSize().height - 14);
        setClientSideWidget();
        this.widgets = new HashMap<>();
        this.panel = panel;
        this.resource = resource;
    }

    @Override
    public void initWidget() {
        Size size = getSize();
        addWidget(new ButtonWidget(size.width - 17, 4, 9, 9, Icons.borderText("+"), this::addNewResource).setHoverTooltips("ldlib.gui.editor.tips.add_item"));
        addWidget(new ButtonWidget(size.width - 17, 4 + 15, 9, 9, Icons.borderText("-"), this::removeSelectedResource).setHoverTooltips("ldlib.gui.editor.tips.remove_item"));
        container = new DraggableScrollableWidgetGroup(1, 2, size.width - 2, size.height - 2);
        container.setYScrollBarWidth(4).setYBarStyle(null, ColorPattern.T_WHITE.rectTexture());
        addWidget(container);
        reBuild();
        super.initWidget();
    }

    public void reBuild() {
        container.clearAllWidgets();
        int width = getSize().getWidth();
        int x = 1;
        int y = 3;
        for (Map.Entry<String, T> entry : resource.allResources()) {
            var widget = widgetSupplier.apply(entry.getValue());
            widgets.put(entry.getKey(), widget);
            Size size = widget.getSize();
            ButtonWidget config;
            SelectableWidgetGroup selectableWidgetGroup = new SelectableWidgetGroup(0, 0, size.width, size.height + 14);
            selectableWidgetGroup.setDraggingProvider(entry::getValue, draggingRenderer);
            selectableWidgetGroup.addWidget(widget);
            selectableWidgetGroup.addWidget(new ImageWidget(0, size.height + 3, size.width, 10, new TextTexture(entry.getKey()).setWidth(size.width).setType(TextTexture.TextType.ROLL)));
            selectableWidgetGroup.addWidget(config = new ButtonWidget(size.width - 10, 1, 9, 9, Icons.borderText("S"), cd -> onEdit.accept(entry.getKey())));
            config.setVisible(false);
            config.setActive(false);
            selectableWidgetGroup.setOnSelected(s -> {
                config.setVisible(true);
                config.setActive(true);
            });
            selectableWidgetGroup.setOnUnSelected(s -> {
                config.setVisible(false);
                config.setActive(false);
            });
            selectableWidgetGroup.setSelectedTexture(ColorPattern.T_GRAY.rectTexture());
            size = selectableWidgetGroup.getSize();

            if (size.width >= width - 5) {
                selectableWidgetGroup.setSelfPosition(new Position(0, y));
                y += size.height + 3;
            } else if (size.width < width - 5 - x) {
                selectableWidgetGroup.setSelfPosition(new Position(x, y));
                x += size.width + 3;
            } else {
                y += size.height + 3;
                x = 1;
                selectableWidgetGroup.setSelfPosition(new Position(x, y));
                x += size.width + 3;
            }
            container.addWidget(selectableWidgetGroup);
        }
    }

    private void addNewResource(ClickData clickData) {

    }

    private void removeSelectedResource(ClickData clickData) {

    }

}
