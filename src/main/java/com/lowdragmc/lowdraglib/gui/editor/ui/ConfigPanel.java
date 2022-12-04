package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.configurator.Configurator;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurable;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author KilaBash
 * @date 2022/12/1
 * @implNote ConfigPanel
 */
public class ConfigPanel extends WidgetGroup {
    @Getter
    private IConfigurable focus;
    private final Editor editor;
    private DraggableScrollableWidgetGroup configuratorGroup;
    private final List<Configurator> configurators = new ArrayList<>();

    public ConfigPanel(Editor editor) {
        super(editor.getSize().getWidth() - 200, 0, 200, editor.getSize().height);
        setClientSideWidget();
        this.editor = editor;
    }

    @Override
    public void initWidget() {
        this.setBackground(ColorPattern.T_BLACK.rectTexture());
        addWidget(new ImageWidget(0, 10, 200, 10, new TextTexture("ldlib.gui.editor.configurator").setWidth(200)));
        addWidget(configuratorGroup = new DraggableScrollableWidgetGroup(0, 25, 200, editor.getSize().height - 25));
        super.initWidget();
    }

    public void clearAllConfigurators() {
        this.focus = null;
        configuratorGroup.clearAllWidgets();
        configurators.clear();
    }

    public void openConfigurator(IConfigurable configurable) {
        if (Objects.equals(configurable, this.focus)) return;
        clearAllConfigurators();
        this.focus = configurable;
        ConfiguratorGroup group = new ConfiguratorGroup("", false);
        configurable.buildConfigurator(group);
        for (Configurator configurator : group.getConfigurators()) {
            configurator.setConfigPanel(this);
            configurator.init(200);
            this.configurators.add(configurator);
            configuratorGroup.addWidget(configurator);
        }
        computeLayout();
    }
    
    public void computeLayout() {
        int height = 0;
        int yOffset = configuratorGroup.getScrollYOffset();
        for (Configurator configurator : configurators) {
            configurator.computeHeight();
            configurator.setSelfPosition(new Position(0, height - yOffset));
            height += configurator.getSize().height + 5;
        }
        configuratorGroup.computeMax();
    }
}
