package com.lowdragmc.lowdraglib.gui.editor;

import com.lowdragmc.lowdraglib.gui.editor.configurator.Configurator;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KilaBash
 * @date 2022/12/1
 * @implNote ConfigPanel
 */
public class ConfigPanel extends WidgetGroup {
    private Editor editor;
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
        configuratorGroup.clearAllWidgets();
        configurators.clear();
    }

    public void initConfigurator(Configurator... configurators) {
        clearAllConfigurators();
        for (Configurator configurator : configurators) {
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
