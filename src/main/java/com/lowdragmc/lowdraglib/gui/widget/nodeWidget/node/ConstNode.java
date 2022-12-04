package com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.TransType;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.connector.Connector;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.connector.FloatConnector;
import com.lowdragmc.lowdraglib.utils.Rect;

import java.util.List;

/**
 * builtin node for producing a const value manually
 */
public class ConstNode implements Node {

	private final FloatConnector connector = makeConnector(new FloatConnector(), node -> node.setTransType(TransType.OUT));
	private TransType transType;
	private Widget holder;

	@Override
	public List<Connector<?>> getInputs() {
		return List.of();
	}

	@Override
	public List<Connector<?>> getOutputs() {
		return List.of(connector);
	}

	@Override
	public Rect getRect() {
		return null;
	}

	@Override
	public Widget getHolder() {
		return holder;
	}

	public void setHolder(Widget holder) {
		this.holder = holder;
	}
}
