package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.animation.Transform;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.utils.Size;
import com.lowdragmc.lowdraglib.utils.interpolate.Eases;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote ResourcePanel
 */
public class ToolPanel extends WidgetGroup {
    public static final int WIDTH = 100;

    @Getter
    protected final Editor editor;
    protected final List<Widget> toolBoxes = new ArrayList<>();
    protected ButtonWidget buttonHide;
    protected TabContainer tabContainer;
    protected ImageWidget tabsBackground;

    @Getter
    protected boolean isShow;

    public ToolPanel(Editor editor) {
        super(-100, 30, WIDTH, Math.max(100, editor.getSize().getHeight() - ResourcePanel.HEIGHT - 30));
        setClientSideWidget();
        this.editor = editor;
    }

    @Override
    public void initWidget() {
        Size size = getSize();
        this.setBackground(ColorPattern.BLACK.rectTexture());

        addWidget(new LabelWidget(3, 3, "ldlib.gui.editor.group.tool_box"));
        addWidget(tabsBackground = new ImageWidget(WIDTH, 15, 20, 0, ColorPattern.BLACK.rectTexture().setRightRadius(8)));

        addWidget(tabContainer = new TabContainer(0, 15, WIDTH, size.height - 15));
        tabContainer.setBackground(ColorPattern.T_GRAY.borderTexture(-1));

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

    @Override
    public void clearAllWidgets() {
        toolBoxes.clear();
        tabContainer.clearAllWidgets();
    }

    public void addNewToolBox(ResourceTexture texture, WidgetGroup toolBox) {
        toolBox.setSize(new Size(WIDTH, getSize().height - 15));
        tabContainer.addTab(new TabButton(WIDTH + 4, 4 + toolBoxes.size() * 20, 12, 12) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if(isMouseOverElement(mouseX, mouseY)) {
                    show();
                }
                return super.mouseClicked(mouseX, mouseY, button);
            }
        }.setTexture(texture, texture.copy().setColor(ColorPattern.T_GREEN.color)), toolBox);
        toolBoxes.add(toolBox);
        tabsBackground.setSize(new Size(20, toolBoxes.size() * 20));
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
}
