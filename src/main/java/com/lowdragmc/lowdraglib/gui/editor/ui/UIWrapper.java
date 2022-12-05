package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.NumberConfigurator;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.WidgetDraggingTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * @author KilaBash
 * @date 2022/12/1
 * @implNote UIWrapper
 */
public class UIWrapper extends WidgetGroup {
    @Getter
    private final Editor editor;
    @Getter
    private final Widget inner;
    private double lastDeltaX, lastDeltaY;
    private boolean dragPosition, dragSize;

    public UIWrapper(Editor editor, Widget inner) {
        super(30, 30, inner.getSize().width, inner.getSize().height);
        setClientSideWidget();
        this.editor = editor;
        this.inner = inner;
        this.addWidget(inner);
    }

    public boolean isWidgetGroup() {
        return inner instanceof WidgetGroup;
    }

    @Override
    public void setSize(Size size) {
        super.setSize(size);
        getInner().setSize(size);
    }

    public boolean isFocusUI() {
        return editor.getFocusUI() == this;
    }

    public boolean isHover() {
        return editor.getLastHover() == this;
    }

    public boolean checkAcceptable(UIWrapper uiWrapper) {
        if (getInner() instanceof WidgetGroup && uiWrapper != this) {
            // make sure accepted ui not my parent.
            var parent = this.getParent();
            while (parent != null) {
                if (parent == uiWrapper) return false;
                parent = parent.getParent();
            }
            return true;
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void updateScreen() {
        super.updateScreen();
        if (!inner.getSize().equals(getSize())) {
            setSize(inner.getSize());
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        Position pos = getPosition();
        Size size = getSize();
        // render border
        if (isFocusUI()) {
            new ColorBorderTexture(1, 0xffff0000).draw(poseStack, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
            int color = 0;
            if (isAltDown() && dragPosition) {
                color = -1;
            } else if (isAltDown() && dragSize) {
                color = 0xff55aa55;
            }
            if (color != 0) {
                float middleX = pos.x + (size.width - 16) / 2f;
                float middleY = pos.y + (size.height - 16) / 2f;
                if (color == -1) {
                    Icons.UP.copy().setColor(color).draw(poseStack, mouseX, mouseY, middleX, pos.y - 10 - 16, 16, 16);
                    Icons.LEFT.copy().setColor(color).draw(poseStack, mouseX, mouseY, pos.x - 10 - 16, middleY, 16, 16);
                }
                Icons.DOWN.copy().setColor(color).draw(poseStack, mouseX, mouseY, middleX, pos.y + size.height + 10, 16, 16);
                Icons.RIGHT.copy().setColor(color).draw(poseStack, mouseX, mouseY, pos.x + size.width + 10, middleY, 16, 16);
            }
        }

        if (isMouseOverElement(mouseX, mouseY)) {
            editor.setHover(this);
            if (editor.getLastHover() == this) {
                if (!isFocusUI()) {
                    new ColorBorderTexture(1, 0x4f0000ff).draw(poseStack, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
                }
                var dragging = getGui().getModularUIGui().getDraggingElement();
                boolean drawDragging = false;
                if (dragging instanceof UIWrapper uiWrapper && checkAcceptable(uiWrapper)) { // can accept
                    drawDragging = true;
                }else if (dragging instanceof IGuiTexture) {
                    drawDragging = true;
                } else if (dragging instanceof String) {
                    drawDragging = true;
                }
                if (drawDragging) {
                    if (editor.setLastDraggingHover(this)) {
                        new ColorBorderTexture(1, 0xff55aa55).draw(poseStack, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
                    }
                }
            }
        }

        super.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        lastDeltaX = 0;
        lastDeltaY = 0;
        dragPosition = false;
        dragSize = false;
        if (isMouseOverElement(mouseX, mouseY)) {
            editor.setFocusUI(this);
            if (isFocusUI()) {
                if (button == 0) {
                    editor.configPanel.openConfigurator(ConfigPanel.Tab.WIDGET, this);
                }
                if (isAltDown()) { // start dragging pos and size
                    if (button == 0) {
                        dragPosition = true;
                    } else if (button == 1) {
                        dragSize = true;
                    }
                    return true;
                }
                if (isShiftDown()) { // dragging itself
                    getGui().getModularUIGui().setDraggingElement(this, new WidgetDraggingTexture(this));
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        double dx = deltaX + lastDeltaX;
        double dy = deltaY + lastDeltaY;
        deltaX = (int) dx;
        deltaY = (int) dy;
        lastDeltaX = dx - deltaX;
        lastDeltaY = dy - deltaY;
        if (isFocusUI() && isAltDown()) {
            if (dragPosition) {
                addSelfPosition((int) deltaX, (int) deltaY);
            } else if (dragSize) {
                setSize(new Size(getSize().width + (int) deltaX, getSize().getHeight() + (int) deltaY));
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragPosition = false;
        dragSize = false;
        if (super.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        if (isMouseOverElement(mouseX, mouseY)) {
            if (editor.getLastHover() == this) {
                var dragging = getGui().getModularUIGui().getDraggingElement();

                if (dragging instanceof UIWrapper uiWrapper && checkAcceptable(uiWrapper)) {
                    var parent = uiWrapper.getParent(); // remove from original parent
                    if (parent != null) {
                        parent.removeWidget(uiWrapper);
                    }

                    // accept it with correct position
                    Position position = new Position((int) mouseX, (int) mouseY).subtract(getPosition());
                    uiWrapper.setSelfPosition(new Position(position.x - uiWrapper.getSize().width / 2, position.y - uiWrapper.getSize().height / 2));
                    ((WidgetGroup) inner).addWidget(uiWrapper);

                    return true;
                } else if (dragging instanceof IGuiTexture guiTexture) {
                    inner.setBackground(guiTexture);
                    return true;
                } else if (dragging instanceof String string) {
                    inner.setHoverTooltips(string);
                    return true;
                }
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
                () -> getSelfPosition().x,
                number -> setSelfPosition(new Position(number.intValue(), getSelfPosition().y)),
                0,
                true).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE));
        position.addConfigurators(new NumberConfigurator("y",
                () -> getSelfPosition().y,
                number -> setSelfPosition(new Position(getSelfPosition().x, number.intValue())),
                0,
                true).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE));

        // size
        ConfiguratorGroup size = new ConfiguratorGroup("ldlib.gui.editor.group.size");
        size.addConfigurators(new NumberConfigurator("width",
                () -> getSize().width,
                number -> setSize(new Size(number.intValue(), getSize().height)),
                10,
                true).setRange(0, Integer.MAX_VALUE));
        size.addConfigurators(new NumberConfigurator("height",
                () -> getSize().height,
                number -> setSize(new Size(getSize().width, number.intValue())),
                10,
                true).setRange(0, Integer.MAX_VALUE));

        common.addConfigurators(position, size);
        getInner().buildConfigurator(common);
    }
}
