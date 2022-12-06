package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurable;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.editor.configurator.NumberConfigurator;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.WidgetDraggingTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

/**
 * @author KilaBash
 * @date 2022/12/6
 * @implNote UIWrapper
 */
public record UIWrapper(@Getter MainPanel panel, @Getter IConfigurableWidget inner) implements IConfigurable {

    public boolean isSelected() {
        return panel.getSelectedUIs().contains(this);
    }

    public boolean isHover() {
        return panel.getHoverUI() == this;
    }

    public boolean checkAcceptable(UIWrapper uiWrapper) {
        return inner.canWidgetDragIn(uiWrapper.inner);
    }

    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        Position pos = inner.widget().getPosition();
        Size size = inner.widget().getSize();
        // render border
        int borderColor = 0;
        if (isSelected()) {
            borderColor = 0xffff0000;
        }
        if (isHover()) {
            if (!isSelected()) {
                borderColor = 0x4f0000ff;
            }
            var dragging = panel.getGui().getModularUIGui().getDraggingElement();
            boolean drawDragging = false;
            if (dragging instanceof UIWrapper[] uiWrappers && Arrays.stream(uiWrappers).allMatch(this::checkAcceptable)) { // can accept
                drawDragging = true;
            } else if (dragging instanceof IGuiTexture) {
                drawDragging = true;
            } else if (dragging instanceof String) {
                drawDragging = true;
            }
            if (drawDragging) {
                borderColor = 0xff55aa55;
            }
        }
        if (borderColor != 0) {
            new ColorBorderTexture(1, borderColor).draw(poseStack, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isHover()) {
            var dragging = panel.getGui().getModularUIGui().getDraggingElement();

            if (dragging instanceof UIWrapper[] uiWrappers && Arrays.stream(uiWrappers).allMatch(this::checkAcceptable)) {
                for (UIWrapper uiWrapper : uiWrappers) {

                    var parent = uiWrapper.inner.widget().getParent(); // remove from original parent

                    if (parent != null) {
                        parent.onWidgetDragOut(uiWrapper.inner);
                    }

                    // accept it with correct position
                    Position position = new Position((int) mouseX, (int) mouseY).subtract(inner.widget().getPosition());
                    uiWrapper.inner.widget().setSelfPosition(new Position(
                            position.x - uiWrapper.inner.widget().getSize().width / 2,
                            position.y - uiWrapper.inner.widget().getSize().height / 2));
                    inner.onWidgetDragIn(uiWrapper.inner);
                }

                return true;
            } else if (dragging instanceof IGuiTexture guiTexture) {
                inner.widget().setBackground(guiTexture);
                return true;
            } else if (dragging instanceof String string) {
                inner.widget().setHoverTooltips(string);
                return true;
            }
        }
        return false;
    }

    @Override
    public void buildConfigurator(ConfiguratorGroup father) {
        ConfiguratorGroup common = new ConfiguratorGroup("ldlib.gui.editor.group.focused_widget", false);
        common.setCanCollapse(false);
        father.addConfigurators(common);

        // position
        ConfiguratorGroup position = new ConfiguratorGroup("ldlib.gui.editor.group.position");
        position.addConfigurators(new NumberConfigurator("x",
                () -> inner.widget().getSelfPosition().x,
                number -> inner.widget().setSelfPosition(new Position(number.intValue(), inner.widget().getSelfPosition().y)),
                0,
                true).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE));
        position.addConfigurators(new NumberConfigurator("y",
                () -> inner.widget().getSelfPosition().y,
                number -> inner.widget().setSelfPosition(new Position(inner.widget().getSelfPosition().x, number.intValue())),
                0,
                true).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE));

        // size
        ConfiguratorGroup size = new ConfiguratorGroup("ldlib.gui.editor.group.size");
        size.addConfigurators(new NumberConfigurator("width",
                () -> inner.widget().getSize().width,
                number -> inner.widget().setSize(new Size(number.intValue(), inner.widget().getSize().height)),
                10,
                true).setRange(0, Integer.MAX_VALUE));
        size.addConfigurators(new NumberConfigurator("height",
                () -> inner.widget().getSize().height,
                number -> inner.widget().setSize(new Size(inner.widget().getSize().width, number.intValue())),
                10,
                true).setRange(0, Integer.MAX_VALUE));

        common.addConfigurators(position, size);
        inner.buildConfigurator(common);
    }

    public void remove() {
        var parent = inner.widget().getParent();
        parent.waitToRemoved(inner.widget());
    }

    public void onDragPosition(int deltaX, int deltaY) {
        inner().widget().addSelfPosition(deltaX, deltaY);
    }

    public void onDragSize(int deltaX, int deltaY) {
        Widget selected = inner().widget();
        selected.setSize(new Size(selected.getSize().width + deltaX, selected.getSize().getHeight() + deltaY));
    }

    public boolean is(IConfigurableWidget configurableWidget) {
        return inner == configurableWidget;
    }

    public IGuiTexture toDraggingTexture(int mouseX, int mouseY) {
        return new WidgetDraggingTexture(mouseX, mouseY, inner.widget());
    }
}
