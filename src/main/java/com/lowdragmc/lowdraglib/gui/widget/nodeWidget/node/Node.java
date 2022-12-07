package com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.NodeGraphState;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.NodeRect;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.connector.Connector;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector4f;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public interface Node {

	/**
	 * @return all the input connector
	 */
	default List<Connector<?>> getInputs() {
		return List.of();
	}

	/**
	 * @return all thr output connector
	 */
	default List<Connector<?>> getOutputs() {
		return List.of();
	}

	/**
	 * @return the area this Node take
	 */
	NodeRect getRect();

	void setRect(NodeRect rect);

	default int getNodeColor() {
		return this.hashCode() | 0xFF;
	}

	/**
	 * @return the name of this node
	 */
	default String getNodeName() {
		return this.getClass().getSimpleName();
	}

	default int getHeight() {
		List<Connector<?>> inputs = Objects.requireNonNullElse(getInputs(), List.of());
		List<Connector<?>> outputs = Objects.requireNonNullElse(getOutputs(), List.of());
		int height = 0;
		for (Connector<?> connector : inputs) {
			height += connector.getHeight();
		}
		for (Connector<?> connector : outputs) {
			height += connector.getHeight();
		}
		height += (inputs.size() + outputs.size()) * StyleConstants.CONNECTOR_GAP_HEIGHT;
		return height;
	}

	void setNodeGraphState(NodeGraphState state);

	NodeGraphState getNodeGraphState();

	default boolean addInputConnector(Connector<?> connector) {
		throw new UnsupportedOperationException("not suppoerted by this node:" + getNodeName());
	}

	default boolean addOutputConnector(Connector<?> connector) {
		throw new UnsupportedOperationException("not supported by this node:" + getNodeName());
	}

	/**
	 * @return the holder of this class
	 */
	NodeHolder getHolder();

	void setHolder(NodeHolder holder);


	/**
	 * render the node self and all the {@link Connector}
	 */
	default void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		DrawerHelper.drawRoundBox(getRect().unwarp(), getInnerRadius(), 0xFF303030);
		getInputs().forEach(connector ->
				connector.render(poseStack, mouseX, mouseY, partialTicks));
		getOutputs().forEach(connector ->
				connector.render(poseStack, mouseX, mouseY, partialTicks));
		var processStr = "";

	}

	static Vector4f getOuterRadius() {
		return new Vector4f(StyleConstants.NODE_RADIUS, StyleConstants.NODE_RADIUS, StyleConstants.NODE_RADIUS, StyleConstants.NODE_RADIUS);
	}

	static Vector4f getInnerRadius() {
		return new Vector4f(0, StyleConstants.NODE_RADIUS, 0, StyleConstants.NODE_RADIUS);
	}


	default <T extends Connector<?>> T makeConnector(T node) {
		node.setHolder(this);
		return node;
	}

	default <T extends Connector<?>> T makeConnector(T node, Consumer<T> consumer) {
		node.setHolder(this);
		consumer.accept(node);
		return node;
	}

}
