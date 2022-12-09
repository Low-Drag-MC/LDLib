package com.lowdragmc.lowdraglib.gui.editor.ui.tool;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.editor.runtime.UIDetector;
import com.lowdragmc.lowdraglib.gui.editor.ui.ToolPanel;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.texture.WidgetTexture;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SelectableWidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/12/10
 * @implNote WidgetToolBox
 */
public class WidgetToolBox extends DraggableScrollableWidgetGroup {
    public static class Default {
        public static List<Default> TABS = new ArrayList<>();
        public static final Default BASIC = registerTab("basic", Icons.WIDGET_BASIC);
        public static final Default GROUP = registerTab("group", Icons.WIDGET_GROUP);
        public static final Default CONTAINER = registerTab("container", Icons.WIDGET_CONTAINER);

        public final String groupName;
        public final ResourceTexture icon;

        private Default(String groupName, ResourceTexture icon) {
            this.groupName = groupName;
            this.icon = icon;
            TABS.add(this);
        }

        public WidgetToolBox createToolBox() {
            return new WidgetToolBox(groupName);
        }

        public static Default registerTab(String groupName, ResourceTexture icon) {
            return new Default(groupName, icon);
        }
    }

    public WidgetToolBox(String groupName) {
        super(0, 0, ToolPanel.WIDTH, 100);
        int yOffset = 3;
        for (UIDetector.Wrapper<RegisterUI, IConfigurableWidget> wrapper : UIDetector.REGISTER_WIDGETS) {
            String group = wrapper.annotation().group().isEmpty() ? "basic" : wrapper.annotation().group();
            if (group.equals(groupName)) {
                var widget = wrapper.creator().get();
                var size = widget.widget().getSize();
                widget.widget().setSelfPosition(new Position((ToolPanel.WIDTH - 2 - size.width) / 2, (65 - size.height) / 2 + 14));
                widget.widget().setActive(false);
                SelectableWidgetGroup selectableWidgetGroup = new SelectableWidgetGroup(0, yOffset, ToolPanel.WIDTH - 2, 65 + 14);
                selectableWidgetGroup.addWidget(widget.widget());
                selectableWidgetGroup.addWidget(new LabelWidget(3, 3,
                        "ldlib.gui.editor.register.widget." + wrapper.annotation().name()));
                selectableWidgetGroup.setSelectedTexture(ColorPattern.T_GRAY.rectTexture());
                selectableWidgetGroup.setDraggingProvider(() -> new IWidgetPanelDragging() {
                    final IConfigurableWidget configurableWidget = wrapper.creator().get();
                    @Override
                    public IConfigurableWidget get() {
                        return configurableWidget;
                    }
                }, (w, p) -> new WidgetTexture(w.get().widget()).setDragging(true));
                addWidget(selectableWidgetGroup);
                yOffset += 65 + 14 + 3;
            }
        }
    }

    public interface IWidgetPanelDragging extends Supplier<IConfigurableWidget> {
    }
}
