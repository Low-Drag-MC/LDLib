package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.configurator.Configurator;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurable;
import com.lowdragmc.lowdraglib.gui.texture.*;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.utils.Position;
import lombok.Getter;

import java.util.*;

/**
 * @author KilaBash
 * @date 2022/12/1
 * @implNote ConfigPanel
 */
public class ConfigPanel extends WidgetGroup {
    public static final int WIDTH = 202;
    public enum Tab {
        WIDGET(Icons.WIDGET_TAB),
        RESOURCE(Icons.RESOURCE_TAB);

        final ResourceTexture icon;

        Tab(ResourceTexture icon) {
            this.icon = icon;
        }
    }

    @Getter
    protected final Editor editor;
    @Getter
    protected final EnumMap<Tab, IConfigurable> focus = new EnumMap<>(Tab.class);
    protected final EnumMap<Tab, DraggableScrollableWidgetGroup> configuratorGroup = new EnumMap<>(Tab.class);
    protected final EnumMap<Tab, List<Configurator>> configurators = new EnumMap<>(Tab.class);

    protected TabContainer tabContainer;


    public ConfigPanel(Editor editor) {
        super(editor.getSize().getWidth() - WIDTH, 0, WIDTH, editor.getSize().height);
        setClientSideWidget();
        this.editor = editor;
    }

    @Override
    public void initWidget() {
        this.setBackground(ColorPattern.T_BLACK.rectTexture());
        addWidget(new ImageWidget(0, 10, WIDTH, 10, new TextTexture("ldlib.gui.editor.configurator").setWidth(202)));
        addWidget(new ImageWidget(-20, 30, 20, Tab.values().length * 20, ColorPattern.T_BLACK.rectTexture()));

        addWidget(tabContainer = new TabContainer(0, 0, WIDTH, editor.getSize().height));
        int y = 34;

        for (Tab tab : Tab.values()) {
            tabContainer.addTab(new TabButton(-16, y, 12, 12).setTexture(
                            tab.icon,
                            tab.icon.copy().setColor(ColorPattern.T_GREEN.color)
                    ),
                    configuratorGroup.computeIfAbsent(tab, key -> new DraggableScrollableWidgetGroup(0, 25, WIDTH, editor.getSize().height - 25)
                            .setYScrollBarWidth(2).setYBarStyle(null, ColorPattern.T_WHITE.rectTexture()))
            );
            configurators.put(tab, new ArrayList<>());
            y += 20;
        }

        super.initWidget();
    }

    public void clearAllConfigurators(Tab tab) {
        this.focus.remove(tab);
        configuratorGroup.get(tab).clearAllWidgets();
        configurators.get(tab).clear();
    }

    public void openConfigurator(Tab tab, IConfigurable configurable) {
        switchTag(tab);
        if (Objects.equals(configurable, this.focus.get(tab))) return;
        clearAllConfigurators(tab);
        this.focus.put(tab, configurable);
        ConfiguratorGroup group = new ConfiguratorGroup("", false);
        configurable.buildConfigurator(group);
        for (Configurator configurator : group.getConfigurators()) {
            configurator.setConfigPanel(this, tab);
            configurator.init(200);
            this.configurators.get(tab).add(configurator);
            configuratorGroup.get(tab).addWidget(configurator);
        }
        computeLayout(tab);
    }

    public void switchTag(Tab tab) {
        tabContainer.switchTag(configuratorGroup.get(tab));
    }
    
    public void computeLayout(Tab tab) {
        int height = 0;
        int yOffset = configuratorGroup.get(tab).getScrollYOffset();
        for (Configurator configurator : configurators.get(tab)) {
            configurator.computeHeight();
            configurator.setSelfPosition(new Position(0, height - yOffset));
            height += configurator.getSize().height + 5;
        }
        configuratorGroup.get(tab).computeMax();
    }
}
