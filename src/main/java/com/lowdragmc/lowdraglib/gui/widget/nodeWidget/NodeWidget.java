package com.lowdragmc.lowdraglib.gui.widget.nodeWidget;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.TextBoxWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.connector.Connector;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.Node;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.NodeHolder;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.StyleConstants;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector4f;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * the base node widget class, can contain multi {@link Connector}
 */
public class NodeWidget extends WidgetGroup implements DraggingSensitive, NodeHolder {

	private final DraggingState<?> draggingState = new DraggingState<>(this);
	private Node node;
	private final TextBoxWidget textWidget = new TextBoxWidget(0, (int) (StyleConstants.CONNECTOR_GAP_HEIGHT * 1.5), getSize().width,
			List.of("unknown")).setCenter(true);

	public NodeWidget(int x, int y) {
		super(x, y, 100, 100);
		addWidget(textWidget);
	}

	public void setNode(Node node) {
		if (node != null) {
			this.node = node;
			this.node.setHolder(this);
			onNodeUpdate();
		}
	}

	@Override
	public void onNodeUpdate() {
		textWidget.setContent(List.of(node.getNodeName()));

		var nodeConnectorHeight = node.getHeight();

		this.setSize(new Size(Math.max(getSize().width, textWidget.getMaxContentWidth()),
				nodeConnectorHeight + StyleConstants.STRING_HEIGHT
						+ StyleConstants.CONNECTOR_GAP_HEIGHT * 2
						+ StyleConstants.NODE_DOWN_PADDING));

		int up = getPosition().y + StyleConstants.STRING_HEIGHT;

		node.setRect(NodeRect.warp(
				getRect().verticalExpand(-(StyleConstants.STRING_HEIGHT + 2 * StyleConstants.CONNECTOR_GAP_HEIGHT),
						0)
		));
	}

	@Override
	protected void onPositionUpdate() {
		super.onPositionUpdate();
		onNodeUpdate();
	}

	@Override
	protected void onSizeUpdate() {
		super.onSizeUpdate();
		textWidget.setSize(new Size(this.getSize().width, textWidget.getSize().height));
	}

	@Override
	public void drawInBackground(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		if (node != null) {
			DrawerHelper.drawRoundBox(getRect(), Node.getOuterRadius(), node.getNodeColor());
			node.render(poseStack, mouseX, mouseY, partialTicks);
		} else {
			DrawerHelper.drawRoundBox(getRect(),
					new Vector4f(8, 8, 8, 8), 0x55FFFF00);
		}
		super.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (draggingState.onMouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		draggingState.onMouseClicked(mouseX, mouseY, button);
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
