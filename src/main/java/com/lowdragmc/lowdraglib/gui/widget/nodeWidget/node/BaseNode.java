package com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node;

import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.NodeGraphState;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.TransType;
import com.lowdragmc.lowdraglib.utils.Rect;

/**
 * builtin node for producing a const value manually
 */
public abstract class BaseNode implements Node {

	private TransType transType;
	private NodeHolder holder;
	private NodeGraphState graphState = NodeGraphState.UNKNOWN;
	private Rect rect;

	@Override
	public Rect getRect() {
		return rect;
	}

	@Override
	public void setRect(Rect rect) {
		this.rect = rect;
	}

	@Override
	public int getNodeColor() {
		return 0xff375a82;
	}

	@Override
	public void setNodeGraphState(NodeGraphState state) {
		this.graphState = state;
	}

	@Override
	public NodeGraphState getNodeGraphState() {
		return graphState;
	}

	@Override
	public NodeHolder getHolder() {
		return holder;
	}

	@Override
	public void setHolder(NodeHolder holder) {
		this.holder = holder;
	}

}
