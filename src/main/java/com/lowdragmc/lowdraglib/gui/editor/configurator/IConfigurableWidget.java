package com.lowdragmc.lowdraglib.gui.editor.configurator;

import com.lowdragmc.lowdraglib.gui.widget.Widget;

/**
 * @author KilaBash
 * @date 2022/12/6
 * @implNote IConfigurableWidget
 */
public interface IConfigurableWidget extends IConfigurable{
    default Widget widget() {
        return (Widget) this;
    }

    default boolean canWidgetDragIn(IConfigurableWidget widget) {
        return false;
    }

    default void onWidgetDragIn(IConfigurableWidget widget) {

    }

    default void onWidgetDragOut(IConfigurableWidget widget) {

    }
}
