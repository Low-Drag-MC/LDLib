package com.lowdragmc.lowdraglib.gui.editor.accessors;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ArrayConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.Configurator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.AllArgsConstructor;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote ArrayConfiguratorAccessor
 */
@AllArgsConstructor
public class CollectionConfiguratorAccessor implements IConfiguratorAccessor<Collection> {
    private final Class<?> baseType;
    private final Class<?> childType;
    private final IConfiguratorAccessor childAccessor;

    @Override
    public boolean test(Class<?> type) {
        return type.isArray();
    }

    @Override
    public Collection defaultValue(Field field, Class<?> type) {
        if (type == List.class) {
            return new ArrayList<>();
        } else if (type == Set.class) {
            return new HashSet<>();
        }
        return new ArrayList<>();
    }

    @Override
    public Configurator create(String name, Supplier<Collection> supplier, Consumer<Collection> consumer, boolean forceUpdate, Field field) {
        boolean isCollapse = true;
        boolean canCollapse = true;
        if (field.isAnnotationPresent(Configurable.class)) {
            isCollapse = field.getAnnotation(Configurable.class).collapse();
            canCollapse = field.getAnnotation(Configurable.class).canCollapse();
        }

        var collection = supplier.get();
        if (collection == null) {
            collection = defaultValue(field, baseType);
        }

        final Collection base = collection;

        ArrayConfiguratorGroup arrayGroup = new ArrayConfiguratorGroup(name, isCollapse);
        arrayGroup.setCanCollapse(canCollapse);

        List<Object> objectList = new ArrayList<>(collection);
        Object2IntMap<Configurator> indexMap = new Object2IntOpenHashMap<>();

        for (int i = 0; i < objectList.size(); i++) {
            arrayGroup.addConfigurators(createConfigurator(name, consumer, forceUpdate, field, objectList, indexMap, i, base));
        }

        arrayGroup.setAddConfigurator(index -> {
            objectList.add(childAccessor.defaultValue(field, childType));
            consumer.accept(updateCollection(base, objectList));
            return createConfigurator(name, consumer, forceUpdate, field, objectList, indexMap, index, base);
        });

        arrayGroup.setRemoveConfigurator(index -> {
            objectList.remove(index);
            consumer.accept(updateCollection(base, objectList));
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

    private Configurator createConfigurator(String name, Consumer consumer, boolean forceUpdate, Field field, List<Object> objectList, Object2IntMap<Configurator> indexMap, int index, Collection base) {
        AtomicReference<Configurator> reference = new AtomicReference<>();
        Configurator configurator = childAccessor.create("", () -> objectList.get(indexMap.getInt(reference.get())), value -> {
            objectList.set(indexMap.getInt(reference.get()), value);
            consumer.accept(updateCollection(base, objectList));
        }, forceUpdate, field);
        reference.set(configurator);
        indexMap.put(configurator, index);
        return configurator;
    }

    public Collection updateCollection(Collection base, List<Object> objectList) {
        base.clear();
        base.addAll(objectList);
        return base;
    }
}
