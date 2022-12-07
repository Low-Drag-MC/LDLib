package com.lowdragmc.lowdraglib.gui.editor.accessors;

import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigAccessor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.DefaultValue;
import com.lowdragmc.lowdraglib.gui.editor.configurator.Configurator;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.NumberConfigurator;
import com.lowdragmc.lowdraglib.utils.Size;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote PositionAccessor
 */
@ConfigAccessor
public class SizeAccessor extends TypesAccessor<Size> {

    public SizeAccessor() {
        super(Size.class);
    }

    @Override
    public Size defaultValue(Field field, Class<?> type) {
        if (field.isAnnotationPresent(DefaultValue.class)) {
            return new Size((int) field.getAnnotation(DefaultValue.class).numberValue()[0], (int) field.getAnnotation(DefaultValue.class).numberValue()[1]);
        }
        return Size.ZERO;
    }

    @Override
    public Configurator create(String name, Supplier<Size> supplier, Consumer<Size> consumer, boolean forceUpdate, Field field) {
        ConfiguratorGroup group = new ConfiguratorGroup(name);
        group.addConfigurators(new NumberConfigurator("width", () -> supplier.get().width, number -> consumer.accept(new Size(number.intValue(), supplier.get().height)), 0, forceUpdate).setRange(0, Integer.MAX_VALUE));
        group.addConfigurators(new NumberConfigurator("height", () -> supplier.get().height, number -> consumer.accept(new Size(supplier.get().width, number.intValue())), 0, forceUpdate).setRange(0, Integer.MAX_VALUE));
        return group;
    }

}
