package com.lowdragmc.lowdraglib.gui.widget.nodeWidget.connector;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.TransType;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.Node;
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

	int CONNECTOR_GAP_HEIGHT = 5;
	int CONNECTOR_DEFAULT_COLOR = 0xFF_00_00_00;

	@Nullable
	T getDefaultValue();

	@Nullable
	T getValue();

	void setTransType(TransType type);

	/**
	 * @return maybe null when haven't set
	 */
	@Nullable
	TransType getTransType();

	@SuppressWarnings("unchecked")
	default Class<T> getHolderClass() {
		return (Class<T>) Objects.requireNonNull(getValue()).getClass();
	}

	int getConnectorRenderHeight();

	default String connectorTypeName() {
		return getHolderClass().getSimpleName();
	}

	default int getConnectorColor() {
		return getHolderClass().toString().hashCode() | 0x7F000000; //0xFF >> 1 = 0x7F
	}

	default <T2> boolean canConnectTo(Connector<T2> connector) {
		return this.getHolderClass() == connector.getHolderClass();
	}

	Rect getArea();

	Node getHolder();

	void setHolder(Node holder);

	default void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		DrawerHelper.drawSolidRect(poseStack, getArea(), CONNECTOR_DEFAULT_COLOR);
	}

}
