package com.lowdragmc.lowdraglib.gui.editor.configurator;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/12/1
 * @implNote NumberConfigurator
 */
public class NumberConfigurator extends ValueConfigurator<Number> {
    protected boolean isDecimal;
    protected TextFieldWidget textFieldWidget;
    protected Number min, max;

    public NumberConfigurator(String name, Supplier<Number> supplier, Consumer<Number> onUpdate, @Nonnull Number defaultValue, boolean forceUpdate) {
        super(name, supplier, onUpdate, defaultValue, forceUpdate);
        if (value == null) {
            value = defaultValue;
        }
        this.isDecimal = value instanceof Double || value instanceof Float;
        setRange(value, value);
    }

    public NumberConfigurator setRange(Number min, Number max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    protected void onValueUpdate(Number newValue) {
        if (newValue == null) newValue = defaultValue;
        if (newValue.equals(value)) return;
        super.onValueUpdate(newValue);
        if (isDecimal) {
            textFieldWidget.setCurrentString(value.floatValue());
        } else {
            textFieldWidget.setCurrentString(value.longValue());
        }
    }

    @Override
    public void init(int width) {
        super.init(width);
        addWidget(new ImageWidget(leftWidth, 2, width - leftWidth - 3 - rightWidth, 10, ColorPattern.T_GRAY.rectTexture()));
        addWidget(textFieldWidget = new TextFieldWidget(leftWidth + 3, 2, width - leftWidth - 6 - rightWidth, 10, null, this::onNumberUpdate));
        textFieldWidget.setClientSideWidget();
        if (isDecimal) {
            textFieldWidget.setCurrentString(value.floatValue());
        } else {
            textFieldWidget.setCurrentString(value.longValue());
        }
        textFieldWidget.setBordered(false);
        if (isDecimal) {
            textFieldWidget.setNumbersOnly(min.floatValue(), max.floatValue());
        } else {
            textFieldWidget.setNumbersOnly(min.longValue(), max.longValue());
        }
    }

    private void onNumberUpdate(String s) {
        Number newValue = isDecimal ? Float.parseFloat(s) : Long.parseLong(s);
        if (value instanceof Integer && !value.equals(newValue.intValue())) {
            value = newValue.intValue();
            updateValue();
        } else if (value instanceof Long && !value.equals(newValue.longValue())) {
            value = newValue.longValue();
            updateValue();
        } else if (value instanceof Float && !value.equals(newValue.floatValue())) {
            value = newValue.floatValue();
            updateValue();
        } else if (value instanceof Double && !value.equals(newValue.doubleValue())) {
            value = newValue.doubleValue();
            updateValue();
        } else if (value instanceof Byte && !value.equals(newValue.byteValue())) {
            value = newValue.byteValue();
            updateValue();
        } else if (value == null) {
            if (isDecimal) {
                value = newValue.floatValue();
            } else {
                value = newValue.intValue();
            }
            updateValue();
        }
    }

}
