package com.lowdragmc.lowdraglib.gui.editor.accessors;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ArrayConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.Configurator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.AllArgsConstructor;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote ArrayConfiguratorAccessor
 */
@AllArgsConstructor
public class ArrayConfiguratorAccessor implements IConfiguratorAccessor<Object> {
    private final Class<?> childType;
    private final IConfiguratorAccessor childAccessor;

    @Override
    public boolean test(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    @Override
    public Object defaultValue(Field field, Class<?> type) {
        return Array.newInstance(childType, 0);
    }

    @Override
    public Configurator create(String name, Supplier supplier, Consumer consumer, boolean forceUpdate, Field field) {
        boolean isCollapse = true;
        boolean canCollapse = true;
        if (field.isAnnotationPresent(Configurable.class)) {
            isCollapse = field.getAnnotation(Configurable.class).collapse();
            canCollapse = field.getAnnotation(Configurable.class).canCollapse();
        }
        Object array = supplier.get();
        if (array == null) {
            array = defaultValue(field, Object.class);
        }
        ArrayConfiguratorGroup arrayGroup = new ArrayConfiguratorGroup(name, isCollapse);
        arrayGroup.setCanCollapse(canCollapse);

        int length = Array.getLength(array);

        List<Object> objectList = new ArrayList<>();
        Object2IntMap<Configurator> indexMap = new Object2IntOpenHashMap<>();

        for (int i = 0; i < length; i++) {
            objectList.add(Array.get(array, i));
            arrayGroup.addConfigurators(createConfigurator(name, consumer, forceUpdate, field, objectList, indexMap, i));
        }

        arrayGroup.setAddConfigurator(index -> {
            objectList.add(childAccessor.defaultValue(field, childType));
            consumer.accept(toArray(objectList));
            return createConfigurator(name, consumer, forceUpdate, field, objectList, indexMap, index);
        });
        arrayGroup.setRemoveConfigurator(index -> {
            objectList.remove(index);
            consumer.accept(toArray(objectList));
            var iter = indexMap.object2IntEntrySet().iterator();
            while (iter.hasNext()) {
                var entry = iter.next();
                if (entry.getIntValue() == index) {
                    iter.remove();
                } else if (entry.getIntValue() > index) {
                    entry.setValue(entry.getIntValue() - 1);
                }
            }
        });
        return arrayGroup;
    }

    private Configurator createConfigurator(String name, Consumer consumer, boolean forceUpdate, Field field, List<Object> objectList, Object2IntMap<Configurator> indexMap, int index) {
        AtomicReference<Configurator> reference = new AtomicReference<>();
        Configurator configurator = childAccessor.create(name, () -> objectList.get(indexMap.getInt(reference.get())), value -> {
            objectList.set(indexMap.getInt(reference.get()), value);
            consumer.accept(toArray(objectList));
        }, forceUpdate, field);
        reference.set(configurator);
        indexMap.put(configurator, index);
        return configurator;
    }

    public Object toArray(List<Object> objectList) {
        Object array = Array.newInstance(childType, objectList.size());
        for (int i = 0; i < objectList.size(); i++) {
            Array.set(array, i, objectList.get(i));
        }
        return array;
    }
}
