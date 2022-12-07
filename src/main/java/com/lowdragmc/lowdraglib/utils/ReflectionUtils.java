package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.LDLMod;
import me.shedaniel.rei.impl.init.PrimitivePlatformAdapter;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ReflectionUtils {
    public static Class<?> getRawType(Type type, Class<?> fallback) {
        var rawType = getRawType(type);
        return rawType != null ? rawType : fallback;
    }
    public static Class<?> getRawType(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof GenericArrayType) {
            return getRawType(((GenericArrayType) type).getGenericComponentType());
        } else if (type instanceof ParameterizedType) {
            return getRawType(((ParameterizedType) type).getRawType());
        } else {
            return null;
        }
    }
    public static <A extends Annotation> void getAnnotationClasses(Class<A> annotationClass, Consumer<Class<?>> consumer) {
        org.objectweb.asm.Type annotationType = org.objectweb.asm.Type.getType(annotationClass);
        for (ModFileScanData data : ModList.get().getAllScanData()) {
            for (ModFileScanData.AnnotationData annotation : data.getAnnotations()) {
                if (annotationType.equals(annotation.annotationType())) {
                    try {
                        consumer.accept(Class.forName(annotation.memberName()));
                    } catch (Throwable throwable) {
                        LDLMod.LOGGER.error("Failed to load class for notation: " + annotation.memberName(), throwable);
                    }
                }
            }
        }
    }
}
