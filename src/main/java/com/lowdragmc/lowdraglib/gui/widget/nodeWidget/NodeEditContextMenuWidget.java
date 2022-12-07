package com.lowdragmc.lowdraglib.gui.widget.nodeWidget;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.TextBoxWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector4f;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * open while right click in the {@link NodeEditPanelWidget} for fast operation
 */
public class NodeEditContextMenuWidget extends WidgetGroup {

	private static final int CONTEXT_MENU_WIDTH = 50;

	private final NodeEditPanelWidget holder;

	public NodeEditContextMenuWidget(int height, NodeEditPanelWidget holder) {
		super(0, 0, CONTEXT_MENU_WIDTH, height);
		this.holder = holder;
	}

	@Override
	public void initWidget() {
		super.initWidget();
		addWidget(new TextBoxWidget(0, 0, CONTEXT_MENU_WIDTH, List.of("context")));
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (holder.isMouseOverElement(mouseX, mouseY)) {
			if (this.isMouseOverElement(mouseX, mouseY) && isVisible()) {
				return super.mouseClicked(mouseX, mouseY, button);
				//do self mouse clicked here
			} else {
				if (button == GLFW.GLFW_MOUSE_BUTTON_2 && !this.isVisible()) {
					this.setVisible(true);
					this.setSelfPosition(new Position((int) mouseX, (int) mouseY));
				} else if (button != GLFW.GLFW_MOUSE_BUTTON_3) {
					this.setVisible(false);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void drawInBackground(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		if (this.isVisible()) {
			DrawerHelper.drawRoundBox(poseStack, getRect(),
					new Vector4f(5, 5, 5, 5), 0xffff00ff);
			super.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
		}
	}
}
