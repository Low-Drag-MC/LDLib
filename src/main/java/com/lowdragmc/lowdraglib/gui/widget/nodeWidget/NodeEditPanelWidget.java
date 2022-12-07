package com.lowdragmc.lowdraglib.gui.widget.nodeWidget;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.ConstNode;
import com.lowdragmc.lowdraglib.utils.LdUtils;
import com.lowdragmc.lowdraglib.utils.Position;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * the panel widget for node editor, can contain multi {@link NodeWidget}
 */
public class NodeEditPanelWidget extends WidgetGroup implements DraggingSensitive {

	private Position locatePosition;
	private final NodeEditContextMenuWidget contextMenu = LdUtils.make(
			new NodeEditContextMenuWidget(70, this),
			(menu -> menu.setVisible(false))
	);


	public NodeEditPanelWidget(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.locatePosition = this.getPosition();
	}

	private final DraggingState<DraggingData> draggingState = new DraggingState<>(this,
			new DraggingData(), draggingData -> {
		draggingData.initLocateX = locatePosition.x;
		draggingData.initLocateY = locatePosition.y;
	});

	@Override
	public void initWidget() {
		super.initWidget();
		NodeWidget widget = new NodeWidget(100, 0);
		widget.setNode(new ConstNode());
		this.addWidget(widget);
	}

	@Override
	public void drawInBackground(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		var scale = Minecraft.getInstance().getWindow().getGuiScale();
		{
			var x = (int) (getPosition().x * scale);
			var y = (int) (getPosition().y * scale);
			var width = (int) (getSize().width * scale);
			var height = (int) (getSize().height * scale);
			RenderSystem.enableScissor(x, y, width, height);
		}
		DrawerHelper.drawPanelBg();
		super.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
		RenderSystem.disableScissor();
	}


	@Override
	public void drawInForeground(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		super.drawInForeground(poseStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!super.mouseClicked(mouseX, mouseY, button)) {
			draggingState.onMouseClicked(mouseX, mouseY, button);
			return contextMenu.mouseClicked(mouseX, mouseY, button);
		}
		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (!super.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
			draggingState.onMouseDragged(mouseX, mouseY, button, dragX, dragY);
		}
		return true;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (!super.mouseReleased(mouseX, mouseY, button)) {
			draggingState.onMouseReleased(mouseX, mouseY, button);
		}
		return true;
	}

	@Override
	public boolean onDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (draggingState.getDraggingButton() == GLFW.GLFW_MOUSE_BUTTON_3) {
			DraggingData value = draggingState.value;
			var x = (int) (draggingState.deltaX(mouseX) + value.initLocateX);
			var y = (int) (draggingState.deltaY(mouseY) + value.initLocateY);
			this.locatePosition = new Position(x, y);
			widgets.forEach(widget -> widget.setParentPosition(locatePosition));
		}
		return false;
	}

	public static class DraggingData {
		int initLocateX;
		int initLocateY;
	}
}
