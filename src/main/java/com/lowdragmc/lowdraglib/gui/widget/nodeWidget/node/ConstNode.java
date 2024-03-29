package com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node;

import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.NodeRect;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.TransType;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.connector.Connector;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.connector.SliderConnector;
import com.lowdragmc.lowdraglib.utils.Rect;

import java.util.List;

public class ConstNode extends BaseNode {

	private final SliderConnector connector = makeConnector(new SliderConnector(),
			node -> node.setTransType(TransType.OUT));

	@Override
	public List<Connector<?>> getOutputs() {
		return List.of(connector);
	}

}
