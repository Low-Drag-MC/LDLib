package com.lowdragmc.lowdraglib.gui.editor.configurator;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

/**
 * @author KilaBash
 * @date 2022/12/6
 * @implNote IConfigurableWidgetGroup
 */
public interface IConfigurableWidgetGroup extends IConfigurableWidget {

    @Override
    default WidgetGroup widget() {
        return (WidgetGroup) this;
    }

    /**
     * Whether widget given be accepted via dragging
     */
    default boolean canWidgetDragIn(IConfigurableWidget widget) {
        return false;
    }

    /**
     * Accept given widget
     */
    default void onWidgetDragIn(IConfigurableWidget widget) {

    }

    /**
     * Child dragged out
     */
    default void onWidgetDragOut(IConfigurableWidget widget) {

    }

}
