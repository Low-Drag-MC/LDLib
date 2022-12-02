package com.lowdragmc.lowdraglib.gui.editor.configurator;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote StringConfigurator
 */
public class StringConfigurator extends ValueConfigurator<String>{
    protected TextFieldWidget textFieldWidget;

    public StringConfigurator(String name, Supplier<String> supplier, Consumer<String> onUpdate, @Nonnull String defaultValue, boolean forceUpdate) {
        super(name, supplier, onUpdate, defaultValue, forceUpdate);
    }

    @Override
    protected void onValueUpdate(String newValue) {
        if (newValue == null) newValue = defaultValue;
        if (newValue.equals(value)) return;
        super.onValueUpdate(newValue);
        textFieldWidget.setCurrentString(value == null ? defaultValue : value);
    }

    @Override
    public void init(int width) {
        super.init(width);
        addWidget(new ImageWidget(leftWidth, 2, width - leftWidth - 3 - rightWidth, 10, ColorPattern.T_GRAY.rectTexture()));
        addWidget(textFieldWidget = new TextFieldWidget(leftWidth + 3, 2, width - leftWidth - 6 - rightWidth, 10, null, this::onStringUpdate));
        textFieldWidget.setClientSideWidget();
        textFieldWidget.setCurrentString(value == null ? defaultValue : value);
        textFieldWidget.setBordered(false);
    }

    private void onStringUpdate(String s) {
        value = s;
        updateValue();
    }
}
