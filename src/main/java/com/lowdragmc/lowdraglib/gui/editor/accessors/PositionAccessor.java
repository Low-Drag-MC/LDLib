package com.lowdragmc.lowdraglib.gui.editor.accessors;

import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigAccessor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.DefaultValue;
import com.lowdragmc.lowdraglib.gui.editor.configurator.Configurator;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.NumberConfigurator;
import com.lowdragmc.lowdraglib.utils.Position;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote PositionAccessor
 */
@ConfigAccessor
public class PositionAccessor extends TypesAccessor<Position> {

    public PositionAccessor() {
        super(Position.class);
    }

    @Override
    public Position defaultValue(Field field, Class<?> type) {
        if (field.isAnnotationPresent(DefaultValue.class)) {
            return new Position((int) field.getAnnotation(DefaultValue.class).numberValue()[0], (int) field.getAnnotation(DefaultValue.class).numberValue()[1]);
        }
        return Position.ORIGIN;
    }

    @Override
    public Configurator create(String name, Supplier<Position> supplier, Consumer<Position> consumer, boolean forceUpdate, Field field) {
        ConfiguratorGroup group = new ConfiguratorGroup(name);
        group.addConfigurators(new NumberConfigurator("x", () -> supplier.get().x, number -> consumer.accept(new Position(number.intValue(), supplier.get().y)), 0, forceUpdate).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE));
        group.addConfigurators(new NumberConfigurator("y", () -> supplier.get().y, number -> consumer.accept(new Position(supplier.get().x, number.intValue())), 0, forceUpdate).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE));
        return group;
    }

}
