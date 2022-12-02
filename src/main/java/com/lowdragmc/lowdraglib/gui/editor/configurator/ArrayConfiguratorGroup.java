package com.lowdragmc.lowdraglib.gui.editor.configurator;

import com.lowdragmc.lowdraglib.gui.editor.ConfigPanel;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import lombok.Setter;

import java.util.Arrays;
import java.util.function.IntConsumer;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote ArrayConfigurator
 */
public class ArrayConfiguratorGroup extends ConfiguratorGroup{

    protected boolean addMask;
    protected ItemConfigurator removeMask;

    @Setter
    protected Int2ObjectFunction<Configurator> addConfigurator = index -> null;
    @Setter
    protected IntConsumer removeConfigurator = index -> {};

    public ArrayConfiguratorGroup(String name) {
        super(name);
    }

    public ArrayConfiguratorGroup(String name, boolean isCollapse) {
        super(name, isCollapse);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (addMask) {
            addMask = false;
            addConfigurators(addConfigurator.get(configurators.size()));
        } if (removeMask != null) {
            removeConfigurator.accept(configurators.indexOf(removeMask));
            configurators.remove(removeMask);
            removeWidget(removeMask);
            removeMask = null;
            computeLayout();
        }
    }

    @Override
    public void addConfigurators(Configurator... configurators) {
        super.addConfigurators(Arrays.stream(configurators).map(ItemConfigurator::new).toArray(Configurator[]::new));
    }

    @Override
    public void init(int width) {
        super.init(width);
        this.addWidget(new ButtonWidget(width - (tips.length > 0 ? 24 : 12), 3, 9, 9,
                new GuiTextureGroup(new ColorBorderTexture(1, -1), new TextTexture("+", -1)),
                cd -> addMask = true).setHoverTooltips("ldlib.gui.editor.tips.add_item"));
    }

    private class ItemConfigurator extends Configurator {
        Configurator inner;
        public ItemConfigurator(Configurator inner) {
            this.inner = inner;
            this.addWidget(inner);
            this.addWidget(new ButtonWidget(2, 2, 9, 9,
                    new GuiTextureGroup(new ColorBorderTexture(1, -1), new TextTexture("-", -1)),
                    cd -> removeMask = this)
                    .setHoverTooltips("ldlib.gui.editor.tips.remove_item"));
        }

        @Override
        public void setConfigPanel(ConfigPanel configPanel) {
            super.setConfigPanel(configPanel);
            inner.setConfigPanel(configPanel);
        }

        @Override
        public void setSelfPosition(Position selfPosition) {
            super.setSelfPosition(selfPosition);
        }

        @Override
        public void computeHeight() {
            inner.computeHeight();
            inner.setSelfPosition(new Position(10, 0));
            int height = inner.getSize().height;
            setSize(new Size(getSize().width, height));
        }

        @Override
        public void init(int width) {
            super.init(width);
            inner.init(width - 10);
        }
    }


}
