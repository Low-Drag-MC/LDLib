package com.lowdragmc.lowdraglib.gui.widget.nodeWidget;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.Node;

/**
 * Node connector manager, maintain a graph to recording the relationship between all {@link Node}
 */
public class GraphManager {

	MutableGraph graph = GraphBuilder.directed().build();

}