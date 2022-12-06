package com.lowdragmc.lowdraglib.gui.widget.nodeWidget;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.Node;

import java.util.List;

/**
 * Node connector manager, maintain a graph to recording the relationship between all {@link Node}
 */
public class GraphManager {

	private final NodeEditPanelWidget widget;

	MutableGraph<Node> graph = GraphBuilder.directed().build();

	public GraphManager(NodeEditPanelWidget widget) {
		this.widget = widget;
	}

	public void updateGraph(List<Node> nodes) {
		nodes.forEach(graph::addNode);
		nodes.forEach(node -> {
			node.getOutputs().forEach(connector -> {
				graph.putEdge(node, connector.getConnector().getHolder());
			});
			node.getInputs().forEach(connector -> {
				graph.putEdge(connector.getConnector().getHolder(), node);
			});
		});
	}

	public void updateGraphState(List<Node> nodes) {
		nodes.stream().filter(node -> !node.getNodeGraphState().isTerminalState())
				.forEach(node -> node.setNodeGraphState(NodeGraphState.UNLINK));

		nodes.stream().filter(node -> node.getNodeGraphState() == NodeGraphState.END)
				.forEach(node -> graph.predecessors(node).forEach(
						nodeLink -> {
							if (nodeLink.getNodeGraphState() != NodeGraphState.BEGIN) {
								nodeLink.setNodeGraphState(NodeGraphState.LINK);
							}
						}
				));
	}

}