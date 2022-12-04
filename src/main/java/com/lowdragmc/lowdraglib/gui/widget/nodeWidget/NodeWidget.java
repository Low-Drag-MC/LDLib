package com.lowdragmc.lowdraglib.gui.widget.nodeWidget;

import com.lowdragmc.lowdraglib.gui.widget.TextBoxWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.connector.Connector;
import com.lowdragmc.lowdraglib.utils.Position;
import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * the base node widget class, can contain multi {@link Connector}
 */
public class NodeWidget extends WidgetGroup implements DraggingSensitive {

	private final String nodeName;
	private final NodeEditContextMenuWidget contextMenu = new NodeEditContextMenuWidget(0, 0, 200, 200);
	private final DraggingState draggingState = new DraggingState(this);

	public NodeWidget(String nodeName, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.nodeName = nodeName;
		TextBoxWidget textBoxWidget = new TextBoxWidget(0, 0, width, List.of(nodeName)).setCenter(true);
		addWidget(textBoxWidget);
	}

	@Override
	public void drawInBackground(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		super.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
		this.drawRectSolid(poseStack, 0x55FFFF00);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (draggingState.onMouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		draggingState.onMouseClicked(mouseX, mouseY, button);
		contextMenu.mouseClicked(mouseX, mouseY, button);
		return super.mouseClicked(mouseX, mouseY, button);
	}

	public Position getScreenSpacePostion() {
		return this.getParent().getPosition().add(this.getSelfPosition());
	}

	@Override
	public boolean onDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_3 && draggingState.isDragging()) {
			this.setSelfPosition(new Position(
					(int) (draggingState.deltaX(mouseX) + draggingState.getInitSelfPosX()),
					(int) (draggingState.deltaY(mouseY) + draggingState.getInitSelfPosY())
			));
		}
		return true;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		draggingState.onMouseReleased(mouseX, mouseY, button);
		return super.mouseReleased(mouseX, mouseY, button);
	}


}
