package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.animation.Transform;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.editor.runtime.UIDetector;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.texture.WidgetTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.lowdragmc.lowdraglib.utils.interpolate.Eases;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote ResourcePanel
 */
public class WidgetPanel extends WidgetGroup {
    public static final int WIDTH = 100;

    public static class Tab {
        public static List<Tab> TABS = new ArrayList<>();
        public static final Tab BASIC = registerTab("basic", Icons.WIDGET_BASIC);
        public static final Tab GROUP = registerTab("group", Icons.WIDGET_GROUP);
        public static final Tab CONTAINER = registerTab("container", Icons.WIDGET_CONTAINER);

        public final String groupName;
        public final ResourceTexture icon;

        private Tab(String groupName, ResourceTexture icon) {
            this.groupName = groupName;
            this.icon = icon;
            TABS.add(this);
        }

        public static Tab registerTab(String groupName, ResourceTexture icon) {
            return new Tab(groupName, icon);
        }
    }

    @Getter
    protected final Editor editor;
    protected final Map<Tab, DraggableScrollableWidgetGroup> widgetGroup = new HashMap<>(Tab.TABS.size());
    protected ButtonWidget buttonHide;
    protected TabContainer tabContainer;

    @Getter
    protected boolean isShow;

    public WidgetPanel(Editor editor) {
        super(-100, 30, WIDTH, Math.max(100, editor.getSize().getHeight() - ResourcePanel.HEIGHT - 30));
        setClientSideWidget();
        this.editor = editor;
    }


    @Override
    public void initWidget() {
        Size size = getSize();
        this.setBackground(ColorPattern.BLACK.rectTexture());

        addWidget(new LabelWidget(3, 3, "ldlib.gui.editor.group.widgets"));
        addWidget(new ImageWidget(WIDTH, 15, 20, Tab.TABS.size() * 20, ColorPattern.BLACK.rectTexture().setRightRadius(8)));

        addWidget(tabContainer = new TabContainer(0, 15, WIDTH, size.height - 15));
        tabContainer.setBackground(ColorPattern.T_GRAY.borderTexture(-1));

        int y = 4;
        for (Tab tab : Tab.TABS) {
            initWidgets(tab, y);
            y += 20;
        }

        addWidget(buttonHide = new ButtonWidget(WIDTH - 13, 3, 10, 10, new GuiTextureGroup(
                ColorPattern.BLACK.rectTexture(),
                ColorPattern.T_GRAY.borderTexture(1),
                Icons.RIGHT
        ), cd -> {
            if (isShow()) {
                hide();
            } else {
                show();
            }
        }).setHoverBorderTexture(1, -1));

        super.initWidget();
    }

    public void initWidgets(Tab tab, int y) {
        var container = widgetGroup.computeIfAbsent(tab, key -> new DraggableScrollableWidgetGroup(0, 0, WIDTH, getSize().height - 15)
                        .setYScrollBarWidth(2).setYBarStyle(null, ColorPattern.T_WHITE.rectTexture().setRadius(1).transform(-0.5f, 0)));
        tabContainer.addTab(new TabButton(WIDTH + 4, y, 12, 12) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if(isMouseOverElement(mouseX, mouseY)) {
                    show();
                }
                return super.mouseClicked(mouseX, mouseY, button);
            }
        }.setTexture(tab.icon, tab.icon.copy().setColor(ColorPattern.T_GREEN.color)), container);

        int yOffset = 3;
        for (UIDetector.Wrapper<RegisterUI, IConfigurableWidget> wrapper : UIDetector.REGISTER_WIDGETS) {
            String group = wrapper.annotation().group().isEmpty() ? "basic" : wrapper.annotation().group();
            if (group.equals(tab.groupName)) {
                var widget = wrapper.creator().get();
                var size = widget.widget().getSize();
                widget.widget().setSelfPosition(new Position((WIDTH - 2 - size.width) / 2, (65 - size.height) / 2 + 14));
                widget.widget().setActive(false);
                SelectableWidgetGroup selectableWidgetGroup = new SelectableWidgetGroup(0, yOffset, WIDTH - 2, 65 + 14);
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
                container.addWidget(selectableWidgetGroup);

                yOffset += 65 + 14 + 3;
            }
        }
    }

    public void hide() {
        if (isShow() && !inAnimate()) {
            isShow = !isShow;
            animation(new Transform()
                    .setOffset(-WIDTH, 0)
                    .setEase(Eases.EaseQuadOut)
                    .setDuration(500)
                    .setOnFinish(() -> {
                        addSelfPosition(-WIDTH, 0);
                        buttonHide.setButtonTexture(ColorPattern.BLACK.rectTexture(), ColorPattern.T_GRAY.borderTexture(1), Icons.RIGHT);
                    }));
        }
    }

    public void show() {
        if (!isShow() && !inAnimate()) {
            isShow = !isShow;
            animation(new Transform()
                    .setOffset(WIDTH, 0)
                    .setEase(Eases.EaseQuadOut)
                    .setDuration(500)
                    .setOnFinish(() -> {
                        addSelfPosition(WIDTH, 0);
                        buttonHide.setButtonTexture(ColorPattern.BLACK.rectTexture(), ColorPattern.T_GRAY.borderTexture(1), Icons.LEFT);
                    }));
        }
    }

    public interface IWidgetPanelDragging extends Supplier<IConfigurableWidget> {
    }
}
