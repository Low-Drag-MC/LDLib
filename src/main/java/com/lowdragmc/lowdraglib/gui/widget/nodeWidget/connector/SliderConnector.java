package com.lowdragmc.lowdraglib.gui.widget.nodeWidget.connector;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.Node;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.StyleConstants;
import com.lowdragmc.lowdraglib.utils.Rect;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector4f;
import org.jetbrains.annotations.NotNull;

public class SliderConnector extends BaseConnector {

	private final Float value = Float.valueOf(1f);

	@Override
	public Float getDefaultValue() {
		return 1f;
	}

	@Override
	public Float getValue() {
		return value;
	}

	@Override
	public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		super.render(poseStack, mouseX, mouseY, partialTicks);
		renderSlide();
	}

	@Override
	public int getHeight() {
		return super.getHeight() + StyleConstants.SLIDER_HEIGHT;
	}

	public void renderSlide() {
		Rect slide = getNodeRect()
				.walk(StyleConstants.CONNECTOR_GAP_HEIGHT)
				.take(StyleConstants.SLIDER_HEIGHT)
				.horizontalExpand(-StyleConstants.SLIDER_PADDING);
		var percent = System.currentTimeMillis() % 2000 / 2000f;
		DrawerHelper.drawProgressRoundBox(slide, getProgressRadius(),
				0xFF4772b3, 0xFF_54_54_54, percent);
	}

	protected Vector4f getProgressRadius() {
		var radius = Math.min(StyleConstants.CONNECTOR_RADIUS,StyleConstants.SLIDER_HEIGHT / 2);
		return new Vector4f(radius,radius,radius,radius);
	}

}
