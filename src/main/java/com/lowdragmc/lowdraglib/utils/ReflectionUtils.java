package com.lowdragmc.lowdraglib.utils;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
}
