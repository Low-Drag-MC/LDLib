package com.lowdragmc.lowdraglib.gui.widget.nodeWidget.connector;

import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.NodeRect;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.TransType;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.Node;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.StyleConstants;
import com.lowdragmc.lowdraglib.utils.Rect;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public abstract class BaseConnector implements Connector<Float> {

	private Node holder;
	private Rect rect;
	private NodeRect nodeRect;
	private TransType transType;

	@Override
	public Class<Float> getHolderClass() {
		return Float.class;
	}

	@Override
	public int getHeight() {
		return Minecraft.getInstance().font.lineHeight + StyleConstants.CONNECTOR_GAP_HEIGHT;
	}

	@Override
	public Rect getRect() {
		return rect;
	}

	@Override
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
	public Connector<Float> getConnector() {
		return null;
	}

	@Nullable
	@Override
	public TransType getTransType() {
		return transType;
	}

	@Override
	public NodeRect getNodeRect() {
		return nodeRect;
	}

	@Override
	public void refreshNodeRect() {
		this.nodeRect = NodeRect.warp(rect);
	}
}
