package com.lowdragmc.lowdraglib.gui.widget.nodeWidget.connector;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.TransType;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.Node;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Rect;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * the element in the {@link Node}
 * @param <T> the type this connector support
 */
public interface Connector<T> {

	int CONNECTOR_GAP_HEIGHT = 5;
	int CONNECTOR_RADIUS = 3;
	int CONNECTOR_DEFAULT_COLOR = 0xFF_00_00_00;
	int NAME_STRING_PADDING = 5;

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

	Node getHolder();

	void setHolder(Node holder);

	default void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		renderConnectorRound(CONNECTOR_RADIUS, getConnectorPos());
		renderName(poseStack);
	}

	default Position getConnectorPos() {
		return switch (Objects.requireNonNull(getTransType())) {
			case IN -> getRect().toLeftUp().add(
					new Position(0, CONNECTOR_GAP_HEIGHT + Minecraft.getInstance().font.lineHeight / 2)
			);
			case OUT -> getRect().toRightUp().add(
					new Position(0, CONNECTOR_GAP_HEIGHT + Minecraft.getInstance().font.lineHeight / 2)
			);
		};
	}

	default void renderConnectorRound(float radius, Position centerPos) {
		DrawerHelper.drawRound(getConnectorColor(), radius, centerPos);
	}

	default void renderName(PoseStack poseStack) {
		var name = getConnectorName();
		var strPos = switch (Objects.requireNonNullElse(getTransType(), TransType.IN)) {
			case IN -> getRect().toLeftUp().add(new Position(NAME_STRING_PADDING, CONNECTOR_GAP_HEIGHT));
			case OUT -> getRect().toRightUp()
					.add(new Position(-Minecraft.getInstance().font.width(name) - NAME_STRING_PADDING, CONNECTOR_GAP_HEIGHT));
		};
		DrawerHelper.drawText(poseStack, getConnectorName(),
				strPos.x, strPos.y, 1, 0xFFFFFFFF);

	}

}
