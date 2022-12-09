package com.lowdragmc.lowdraglib.gui.editor.runtime;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.gui.editor.accessors.IConfiguratorAccessor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigAccessor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.editor.data.Project;
import com.lowdragmc.lowdraglib.gui.editor.data.resource.Resource;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote AnnotationDetector
 */
@SuppressWarnings("unchecked")
public class UIDetector {

    public record Wrapper<A extends Annotation, T>(A annotation, Class<? extends T> clazz, Supplier<T> creator) { }

    public static final List<IConfiguratorAccessor<?>> CONFIGURATOR_ACCESSORS = scanClasses(ConfigAccessor.class, IConfiguratorAccessor.class, UIDetector::checkNoArgsConstructor, UIDetector::createNoArgsInstance);
    public static final List<Wrapper<RegisterUI, IGuiTexture>> REGISTER_TEXTURES = scanClasses(RegisterUI.class, IGuiTexture.class, UIDetector::checkNoArgsConstructor, UIDetector::toUINoArgsBuilder);
    public static final List<Wrapper<RegisterUI, Resource>> REGISTER_RESOURCES = scanClasses(RegisterUI.class, Resource.class, UIDetector::checkNoArgsConstructor, UIDetector::toUINoArgsBuilder);
    public static final List<Wrapper<RegisterUI, IConfigurableWidget>> REGISTER_WIDGETS = scanClasses(RegisterUI.class, IConfigurableWidget.class, UIDetector::checkNoArgsConstructor, UIDetector::toUINoArgsBuilder);
    public static final List<Project> REGISTER_PROJECTS = scanClasses(RegisterUI.class, Project.class, UIDetector::checkNoArgsConstructor, UIDetector::createNoArgsInstance);

    public static void init() {

    }

    public static <A extends Annotation, T, C> List<C> scanClasses(Class<A> annotationClass, Class<T> baseClazz, Predicate<Class<? extends T>> predicate, Function<Class<? extends T>, C> mapping) {
        List<C> result = new ArrayList<>();
        ReflectionUtils.getAnnotationClasses(annotationClass, clazz -> {
            if (baseClazz.isAssignableFrom(clazz)) {
                try {
                    Class<? extends T> realClass =  (Class<? extends T>) clazz;
                    if (predicate.test(realClass)) {
                        result.add(mapping.apply(realClass));
                    }
                } catch (Throwable e) {
                    LDLMod.LOGGER.error("failed to scan annotation {} + base class {} while handling class {} ", annotationClass, baseClazz, clazz, e);
                }
            }
        });
        return result;
    }

    private static <T> boolean checkNoArgsConstructor(Class<? extends T> clazz) {
        try {
            clazz.getConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static <T> T createNoArgsInstance(Class<? extends T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Wrapper<RegisterUI, T> toUINoArgsBuilder(Class<? extends T> clazz) {
        return new Wrapper<>(clazz.getAnnotation(RegisterUI.class), clazz, () -> createNoArgsInstance(clazz));
    }

}
