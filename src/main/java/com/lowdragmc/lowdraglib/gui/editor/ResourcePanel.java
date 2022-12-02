package com.lowdragmc.lowdraglib.gui.editor;

import com.lowdragmc.lowdraglib.gui.animation.Transform;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.utils.Size;
import com.lowdragmc.lowdraglib.utils.interpolate.Eases;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote ResourcePanel
 */
public class ResourcePanel extends WidgetGroup {
    protected Editor editor;
    protected ButtonWidget buttonHide;
    protected TabContainer tabContainer;
    protected Resource resource = Resource.defaultResource();
    @Getter
    protected boolean isShow;

    public ResourcePanel(Editor editor) {
        super(5, editor.getSize().height, editor.getSize().getWidth() - 210, 100);
        setClientSideWidget();
        this.editor = editor;
    }

    @Override
    public void initWidget() {
        Size size = getSize();
        this.setBackground(ColorPattern.BLACK.rectTexture());
        addWidget(buttonHide = new ButtonWidget((getSize().width - 30) / 2, -10, 30, 10, new GuiTextureGroup(
                ColorPattern.BLACK.rectTexture(),
                ColorPattern.T_GRAY.borderTexture(1),
                Icons.UP
        ), cd -> {
            if (isShow()) {
                hide();
            } else {
                show();
            }
        }).setHoverBorderTexture(1, -1));
        addWidget(new LabelWidget(3, 3, "ldlib.gui.editor.group.resources"));
        int offset = Minecraft.getInstance().font.width(I18n.get("ldlib.gui.editor.group.resources")) + 8;
        addWidget(tabContainer = new TabContainer(0, 15, size.width, size.height - 14));
        tabContainer.setBackground(ColorPattern.T_GRAY.borderTexture(-1));

        initTextures(offset);
        offset += 52;
        initColors(offset);
        offset += 52;
        initEntries(offset);
    }

    public void initTextures(int offset) {
        Size size = getSize();
        tabContainer.addTab(
                new TabButton(offset, -30, 50, 15).setTexture(
                        new TextTexture("ldlib.gui.editor.group.textures"),
                        new GuiTextureGroup(new TextTexture("ldlib.gui.editor.group.textures", 0x8833ff00), ColorPattern.T_GRAY.rectTexture())
                ),
                new ResourceContainer<>(resource.textures));
    }

    public void initColors(int offset) {
        Size size = getSize();
        tabContainer.addTab(
                new TabButton(offset, -30, 50, 15).setTexture(
                        new TextTexture("ldlib.gui.editor.group.colors"),
                        new GuiTextureGroup(new TextTexture("ldlib.gui.editor.group.colors", 0x8833ff00), ColorPattern.T_GRAY.rectTexture())
                ),
                new ResourceContainer<>(resource.colors));
        super.initWidget();
    }

    public void initEntries(int offset) {
        Size size = getSize();
        tabContainer.addTab(
                new TabButton(offset, -30, 50, 15).setTexture(
                        new TextTexture("ldlib.gui.editor.group.entries"),
                        new GuiTextureGroup(new TextTexture("ldlib.gui.editor.group.entries", 0x8833ff00), ColorPattern.T_GRAY.rectTexture())
                ),
                new ResourceContainer<>(resource.entries));
        super.initWidget();
    }

    public void hide() {
        if (isShow() && !inAnimate()) {
            isShow = !isShow;
            animation(new Transform()
                    .setOffset(0, getSize().height)
                    .setEase(Eases.EaseQuadOut)
                    .setDuration(500)
                    .setOnFinish(() -> {
                        addSelfPosition(0, getSize().height);
                        buttonHide.setButtonTexture(ColorPattern.BLACK.rectTexture(), ColorPattern.T_GRAY.borderTexture(1), Icons.UP);
                    }));
        }
    }

    public void show() {
        if (!isShow() && !inAnimate()) {
            isShow = !isShow;
            animation(new Transform()
                    .setOffset(0, -getSize().height)
                    .setEase(Eases.EaseQuadOut)
                    .setDuration(500)
                    .setOnFinish(() -> {
                        addSelfPosition(0, -getSize().height);
                        buttonHide.setButtonTexture(ColorPattern.BLACK.rectTexture(), ColorPattern.T_GRAY.borderTexture(1), Icons.DOWN);
                    }));
        }
    }

    public void loadResource(Resource resource) {
        this.resource = resource;
    }

    @Accessors(fluent = true)
    private class ResourceContainer<T> extends WidgetGroup {
        final Map<String, T> resources;
        final DraggableScrollableWidgetGroup container;
        @Setter
        Supplier<T> onWidget;
        @Setter
        Supplier<T> onAdd;
        @Setter
        Consumer<String> onRemove;
        @Setter
        Consumer<String> onEdit;

        public ResourceContainer(Map<String, T> resources) {
            super(0, 0, ResourcePanel.super.getSize().width, ResourcePanel.super.getSize().height - 14);
            setClientSideWidget();
            Size size = getSize();
            addWidget(new ButtonWidget(size.width - 15, 3, 9, 9, Icons.borderText("+"), this::addNewResource).setHoverTooltips("ldlib.gui.editor.tips.add_item"));
            container = new DraggableScrollableWidgetGroup(0, 0, size.width, size.height - 14);
            this.resources = resources;
            reBuild();
        }

        public void reBuild() {

        }

        private void addNewResource(ClickData clickData) {

        }

    }

}
