package com.lowdragmc.lowdraglib.gui.widget.nodeWidget.connector;

import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.TransType;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.Node;
import com.lowdragmc.lowdraglib.utils.Rect;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public class FloatConnector implements Connector<Float> {

	private final Float value = Float.valueOf(1f);
	private Node holder;
	private Rect rect;
	private TransType transType;

	@Override
	public Float getDefaultValue() {
		return 1f;
	}

	@Override
	public Float getValue() {
		return value;
	}

	@Override
	public Class<Float> getHolderClass() {
		return Float.class;
	}

	@Override
	public int getConnectorRenderHeight() {
		return Minecraft.getInstance().font.lineHeight + CONNECTOR_GAP_HEIGHT;
	}

	@Override
	public Rect getArea() {
		return rect;
	}

	public void setRect(Rect rect) {
		this.rect = rect;
	}

	@Override
	public Node getHolder() {
		return holder;
	}

	public void setHolder(Node holder) {
		this.holder = holder;
	}

	@Override
	public void setTransType(TransType type) {
		this.transType = type;
	}

	@Nullable
	@Override
	public TransType getTransType() {
		return transType;
	}


}
