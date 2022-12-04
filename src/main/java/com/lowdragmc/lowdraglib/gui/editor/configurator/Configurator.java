package com.lowdragmc.lowdraglib.gui.editor.configurator;

import com.lowdragmc.lowdraglib.gui.editor.ui.ConfigPanel;
import com.lowdragmc.lowdraglib.gui.texture.*;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Size;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

/**
 * @author KilaBash
 * @date 2022/12/1
 * @implNote Configurator
 */
public class Configurator extends WidgetGroup {
    @Setter
    protected ConfigPanel configPanel;
    protected String[] tips = new String[0];
    protected String name;
    protected int leftWidth, rightWidth, width = -1;

    public Configurator(String name) {
        super(0, 0, 200, 15);
        this.name = name;
        setClientSideWidget();
        if (!name.isEmpty()) {
            this.addWidget(new LabelWidget(3, 3, name));
            leftWidth = Minecraft.getInstance().font.width(I18n.get(name)) + 6;
        } else {
            leftWidth = 3;
        }
    }

    public Configurator() {
        this("");
    }

    protected void computeLayout() {
        configPanel.computeLayout();
    }

    public void setTips(String... tips) {
        this.tips = tips;
        rightWidth = tips.length > 0 ? 13 : 0;
    }

    public boolean isInit() {
        return width > -1;
    }

    public void computeHeight() {

    }

    public void init(int width) {
        this.width = width;
        setSize(new Size(width, getSize().height));
        if (tips.length > 0) {
            this.addWidget(new ImageWidget(width - 12, 3, 9, 9, new GuiTextureGroup(new ColorBorderTexture(1, -1), new TextTexture("?", -1))).setHoverTooltips(tips));
        }
    }

}
