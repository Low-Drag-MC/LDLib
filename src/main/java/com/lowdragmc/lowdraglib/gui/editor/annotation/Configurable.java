package com.lowdragmc.lowdraglib.gui.editor.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Configurable {
    String name() default "";
    String[] tips() default {};
    boolean collapse() default true;
    boolean canCollapse() default true;
    boolean forceUpdate() default true;
}
