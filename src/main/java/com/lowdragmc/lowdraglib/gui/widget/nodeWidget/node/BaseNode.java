package com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node;

import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.NodeGraphState;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.NodeRect;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.TransType;

/**
 * builtin node for producing a const value manually
 */
public abstract class BaseNode implements Node {

	private TransType transType;
	private NodeHolder holder;
	private NodeGraphState graphState = NodeGraphState.UNKNOWN;
	private NodeRect rect;

	@Override
	public NodeRect getRect() {
		return rect;
	}

	@Override
	public void setRect(NodeRect rect) {
		this.rect = rect;
		rect.reset();
		this.getInputs().stream().takeWhile(c -> rect.remainSpace())
				.forEach(connector -> connector.setRect(rect.take(connector.getHeight())));
		this.getOutputs().stream().takeWhile(c -> rect.remainSpace())
				.forEach(connector -> connector.setRect(rect.take(connector.getHeight())));
		rect.reset();
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
