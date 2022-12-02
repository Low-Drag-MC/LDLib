package com.lowdragmc.lowdraglib.gui.editor.configurator;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.utils.Size;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote GuiTextureConfigurator
 */
public class GuiTextureConfigurator extends ValueConfigurator<IGuiTexture>{
    protected ImageWidget preview;

    public GuiTextureConfigurator(String name, Supplier<IGuiTexture> supplier, Consumer<IGuiTexture> onUpdate, boolean forceUpdate) {
        super(name, supplier, onUpdate, IGuiTexture.EMPTY, forceUpdate);
    }

    @Override
    protected void onValueUpdate(IGuiTexture newValue) {
        if (Objects.equals(newValue, value)) return;
        super.onValueUpdate(newValue);
        preview.setImage(newValue);
    }

    @Override
    public void computeHeight() {
        super.computeHeight();
        setSize(new Size(getSize().width, 15 + preview.getSize().height + 4));
    }

    @Override
    public void init(int width) {
        super.init(width);
        int w = Math.min(width - 6, 100);
        int x = (width - w) / 2;
        addWidget(preview = new ImageWidget(x, 17, w, w, value).setBorder(2, ColorPattern.T_WHITE.color));
        addWidget(new ButtonWidget(x, 17, w, w, IGuiTexture.EMPTY, this::openDialog));
    }

    private void openDialog(ClickData clickData) {
    }

}
