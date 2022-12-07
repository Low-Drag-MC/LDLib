package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author KilaBash
 * @date 2022/12/5
 * @implNote MainPanel
 */
public class MainPanel extends WidgetGroup {

    @Getter
    protected final Editor editor;
    @Getter
    protected final WidgetGroup root;

    @Getter
    private final Set<UIWrapper> selectedUIs = new HashSet<>();

    @Getter
    protected UIWrapper hoverUI;

    private double lastDeltaX, lastDeltaY;
    private boolean isDragPosition, isDragSize;

    public MainPanel(Editor editor) {
        super(0, 0, editor.getSize().width, editor.getSize().height);
        this.editor = editor;
        root = new WidgetGroup(30, 30, 200, 200);
        root.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND);
        addWidget(root);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseMoved(double mouseX, double mouseY) {
        // find hover widget
        var hovered = getHoverElement(mouseX, mouseY);
        if (hovered instanceof IConfigurableWidget configurableWidget && hovered != this) {
            if (hoverUI == null ||  !hoverUI.is(configurableWidget)) {
                hoverUI = new UIWrapper(this, configurableWidget);
            }
        } else {
            hoverUI = null;
        }
        return super.mouseMoved(mouseX, mouseY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hoverUI == null) {
            selectedUIs.clear();
        } else {
            if (isCtrlDown()) {
                selectedUIs.add(hoverUI);
            } else if (!selectedUIs.contains(hoverUI)){
                selectedUIs.clear();
                selectedUIs.add(hoverUI);
            }
        }

        lastDeltaX = 0;
        lastDeltaY = 0;
        isDragPosition = false;
        isDragSize = false;

        if (!selectedUIs.isEmpty()) {
            if (button == 0 && hoverUI != null) {
                editor.configPanel.openConfigurator(ConfigPanel.Tab.WIDGET, hoverUI);
            }
            if (isAltDown()) { // start dragging pos and size
                if (button == 0) {
                    isDragPosition = true;
                } else if (button == 1) {
                    isDragSize = true;
                }
                return true;
            }
            if (isShiftDown()) { // dragging itself
                var uiWrappers = selectedUIs.toArray(UIWrapper[]::new);
                getGui().getModularUIGui().setDraggingElement(uiWrappers, new GuiTextureGroup(selectedUIs.stream().map(w -> w.toDraggingTexture((int) mouseX, (int) mouseY)).toArray(IGuiTexture[]::new)));
                return true;
            }
        }

        if (button == 1) {
            editor.openMenu(mouseX, mouseY, createMenu());
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected void removeSelected() {
        for (UIWrapper selectedUI : selectedUIs) {
            selectedUI.remove();
        }
        hoverUI = null;
        selectedUIs.clear();
    }

    private CompoundTag tag;

    protected TreeBuilder.Menu createMenu() {
        return TreeBuilder.Menu.start()
                .leaf("ldlib.gui.editor.menu.remove", this::removeSelected)
                .leaf("ser", () -> {
                    tag = root.serializeNBT();
                })
                .leaf("desr", () -> {
                    if (tag != null) {
                        selectedUIs.clear();
                        hoverUI = null;
                        root.deserializeNBT(tag);
                    }
                });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(poseStack, mouseX, mouseY, partialTicks);

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (UIWrapper selectedUI : selectedUIs) {
            selectedUI.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
            var pos = selectedUI.inner().widget().getPosition();
            var size = selectedUI.inner().widget().getSize();
            minX = Math.min(minX, pos.x);
            minY = Math.min(minY, pos.y);
            maxX = Math.max(maxX, pos.x + size.width);
            maxY = Math.max(maxY, pos.y + size.height);
        }

        if (hoverUI != null) {
            hoverUI.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
        }

        if (!selectedUIs.isEmpty() && Widget.isAltDown()) {
            Position pos = new Position(minX, minY);
            Size size = new Size(maxX - minX, maxY - minY);


            float middleX = pos.x + (size.width - 16) / 2f;
            float middleY = pos.y + (size.height - 16) / 2f;
            if (isDragPosition) {
                Icons.UP.copy().setColor(-1).draw(poseStack, mouseX, mouseY, middleX, pos.y - 10 - 16, 16, 16);
                Icons.LEFT.copy().setColor(-1).draw(poseStack, mouseX, mouseY, pos.x - 10 - 16, middleY, 16, 16);
            }
            if (isDragPosition || isDragSize) {
                Icons.DOWN.copy().setColor(-1).draw(poseStack, mouseX, mouseY, middleX, pos.y + size.height + 10, 16, 16);
                Icons.RIGHT.copy().setColor(-1).draw(poseStack, mouseX, mouseY, pos.x + size.width + 10, middleY, 16, 16);
            }
        }
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
        if (!selectedUIs.isEmpty() && isAltDown()) {
            if (isDragPosition) {
                for (UIWrapper selectedUI : selectedUIs) {
                    selectedUI.onDragPosition((int) deltaX, (int) deltaY);
                }
            } else if (isDragSize) {
                for (UIWrapper selectedUI : selectedUIs) {
                    selectedUI.onDragSize((int) deltaX, (int) deltaY);

                }
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (hoverUI != null && hoverUI.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
