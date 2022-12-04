package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.animation.Transform;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.data.Resources;
import com.lowdragmc.lowdraglib.gui.editor.data.resource.Resource;
import com.lowdragmc.lowdraglib.gui.texture.*;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.utils.Size;
import com.lowdragmc.lowdraglib.utils.interpolate.Eases;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote ResourcePanel
 */
public class ResourcePanel extends WidgetGroup {
    @Getter
    protected Editor editor;
    protected ButtonWidget buttonHide;
    protected TabContainer tabContainer;
    protected Resources resources = Resources.defaultResource();
    @Getter
    protected boolean isShow = true;

    public ResourcePanel(Editor editor) {
        super(0, editor.getSize().height - 100, editor.getSize().getWidth() - 200, 100);
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
                Icons.DOWN
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

        for (Resource<?> resource : resources.resources.values()) {
            tabContainer.addTab(
                    new TabButton(offset, -15, 50, 15).setTexture(
                            new TextTexture(resource.name()),
                            new GuiTextureGroup(new TextTexture(resource.name(), ColorPattern.T_GREEN.color), ColorPattern.T_GRAY.rectTexture())
                    ),
                    resource.createContainer(this)
            );
            offset += 52;
        }

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

    public void loadResource(Resources resources) {
        // todo
        this.resources = resources;
    }

}
