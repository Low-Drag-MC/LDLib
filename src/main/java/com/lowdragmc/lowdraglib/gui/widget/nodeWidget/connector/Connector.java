package com.lowdragmc.lowdraglib.gui.widget.nodeWidget.connector;

import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.NodeRect;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.TransType;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.Node;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.StyleConstants;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Rect;
import com.mojang.blaze3d.vertex.PoseStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * the element in the {@link Node}
 * @param <T> the type this connector support
 */
public interface Connector<T> {
	@Nullable
	T getDefaultValue();

	@Nullable
	T getValue();

	void setTransType(TransType type);

	@Nullable
	Connector<T> getConnector();

	/**
	 * @return maybe null when haven't set
	 */
	@Nullable
	TransType getTransType();

	@SuppressWarnings("unchecked")
	default Class<T> getHolderClass() {
		return (Class<T>) Objects.requireNonNull(getValue()).getClass();
	}

	int getHeight();

	default String getConnectorName() {
		return getHolderClass().getSimpleName();
	}

	default int getConnectorColor() {
		return getHolderClass().toString().hashCode() | 0xFF000000; //0xFF >> 1 = 0x7F
	}

	default <T2> boolean canConnectTo(Connector<T2> connector) {
		return this.getHolderClass() == connector.getHolderClass();
	}

	Rect getRect();

	void setRect(Rect rect);

	NodeRect getNodeRect();

	void refreshNodeRect();

	Node getHolder();

	void setHolder(Node holder);

	default void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		refreshNodeRect();
		Rect drawRect = getNodeRect().walk(StyleConstants.CONNECTOR_GAP_HEIGHT).take(StyleConstants.STRING_HEIGHT);
		renderConnectorRound(poseStack, StyleConstants.CONNECTOR_RADIUS,
				getTransType() == TransType.IN ? drawRect.toLeftCenter() : drawRect.toRightCenter());
		renderName(poseStack, drawRect, mouseX, mouseY);
	}

	default void renderConnectorRound(PoseStack poseStack, float radius, Position centerPos) {
		DrawerHelper.drawRound(poseStack, getConnectorColor(), radius, centerPos);
	}


	default void renderName(PoseStack poseStack, Rect drawRect, int mouseX, int mouseY) {
		TextTexture textTexture = new TextTexture(getConnectorName())
				.setSupplier(this::getConnectorName)
				.setColor(0xFFFFFFFF)
				.setWidth(drawRect.getWidth())
				.setDropShadow(false)
				.setType(getTransType() == TransType.IN ? TextTexture.TextType.LEFT : TextTexture.TextType.RIGHT);

		textTexture.draw(poseStack, mouseX, mouseY,
				drawRect.left, drawRect.up, drawRect.getWidth() - StyleConstants.NAME_STRING_PADDING, drawRect.getHeight());

	}

}
