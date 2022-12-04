package com.lowdragmc.lowdraglib.gui.widget.nodeWidget;

import com.lowdragmc.lowdraglib.gui.widget.TextBoxWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * open while right click in the {@link NodeEditPanelWidget} for fast operation
 */
public class NodeEditContextMenuWidget extends WidgetGroup {
	public NodeEditContextMenuWidget(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public void initWidget() {
		super.initWidget();
		addWidget(new TextBoxWidget(0, 0, 20, List.of("context")));
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isMouseOverElement(mouseX, mouseY)) {
			if (isVisible()) {
				return super.mouseClicked(mouseX, mouseY, button);
			} else if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
				this.setVisible(true);
				this.setSelfPosition(new Position((int) mouseX, (int) mouseY));
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
