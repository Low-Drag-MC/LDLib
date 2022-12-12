package com.lowdragmc.lowdraglib.gui.editor.annotation;

import lombok.NoArgsConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * register it as a usable content in ui editor
 * make sure your class always have a constructor {@link NoArgsConstructor}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RegisterUI {
    String name();
    String group() default "";
    String modID() default "";
}
