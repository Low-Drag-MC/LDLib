package com.lowdragmc.lowdraglib.gui.editor;

import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.NumberConfigurator;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
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
 * @implNote FrameWidget
 */
public class FrameWidget extends WidgetGroup {
    private static FrameWidget focus;
    private final Editor editor;
    @Getter
    private final Widget inner;
    private double lastDeltaX, lastDeltaY;
    private boolean dragPosition, dragSize;

    public FrameWidget(Editor editor, Widget inner) {
        super(30, 30, inner.getSize().width, inner.getSize().height);
        setClientSideWidget();
        this.editor = editor;
        this.inner = inner;
        this.addWidget(inner);
    }

    @Override
    public void setSelfPosition(Position selfPosition) {
        super.setSelfPosition(selfPosition);
    }

    @Override
    public void setSize(Size size) {
        super.setSize(size);
        getInner().setSize(size);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
        Position pos = getPosition();
        Size size = getSize();
        // render border
        if (focus == this) {
            new ColorBorderTexture(1, 0xffff0000).draw(poseStack, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
            int color = 0;
            if (isCtrlDown() && dragPosition) {
                color = -1;
            } else if (isShiftDown() && dragSize) {
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
        } else {
            if (isMouseOverElement(mouseX, mouseY)) {
                new ColorBorderTexture(1, 0xff0000ff).draw(poseStack, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        lastDeltaX = 0;
        lastDeltaY = 0;
        dragPosition = false;
        dragSize = false;
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (isMouseOverElement(mouseX, mouseY)) {
            if (focus != this && button == 0) {
                focus = this;
                ConfiguratorGroup common = new ConfiguratorGroup("ldlib.gui.editor.group.focused_widget", false);
                common.setCanCollapse(false);
                this.buildConfigurator(common);
                editor.configPanel.initConfigurator(common);
            }
            if (isCtrlDown() && focus == this && button == 0) { // start dragging
                dragPosition = true;
            } else if (isShiftDown() && focus == this && button == 0) {
                dragSize = true;
            }
        }
        return false;
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
        if (focus == this && isCtrlDown() && dragPosition) {
            addSelfPosition((int) deltaX, (int) deltaY);
            return true;
        } else if (focus == this && isShiftDown() && dragSize) {
            setSize(new Size(getSize().width + (int) deltaX, getSize().getHeight() + (int) deltaY));
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragPosition = false;
        dragSize = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void buildConfigurator(ConfiguratorGroup father) {
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

        father.addConfigurators(position, size);
        getInner().buildConfigurator(father);
    }
}
