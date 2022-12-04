package com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.TransType;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.connector.Connector;
import com.lowdragmc.lowdraglib.utils.Rect;
import com.mojang.blaze3d.vertex.PoseStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

public interface Node {

	int NODE_DEFAULT_COLOR = 0xFF_FF_FF_FF;

	/**
	 * @return all the input connector, {@link TransType#IO} will exist in both
	 */
	List<Connector<?>> getInputs();

	/**
	 * @return all thr output connect, {@link TransType#IO} will exist in both
	 */
	List<Connector<?>> getOutputs();

	/**
	 * @return the area this Node take
	 */
	Rect getRect();

	/**
	 * @return the name of this node
	 */
	default String getNodeName() {
		return this.getClass().getSimpleName();
	}

	default boolean addInputConnector(Connector<?> connector) {
		throw new UnsupportedOperationException("not suppoerted by this node:" + getNodeName());
	}

	default boolean addOutputConnector(Connector<?> connector) {
		throw new UnsupportedOperationException("not supported by this node:" + getNodeName());
	}

	/**
	 * @return the holder of this class
	 */
	Widget getHolder();

	/**
	 * render the node self and all the {@link Connector}
	 */
	default void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		DrawerHelper.drawSolidRect(poseStack, getRect(), NODE_DEFAULT_COLOR);
		getInputs().forEach(connector ->
				connector.render(poseStack, mouseX, mouseY, partialTicks));
		getOutputs().forEach(connector ->
				connector.render(poseStack, mouseX, mouseY, partialTicks));
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
