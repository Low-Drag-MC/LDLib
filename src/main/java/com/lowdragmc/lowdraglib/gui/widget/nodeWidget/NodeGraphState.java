package com.lowdragmc.lowdraglib.gui.widget.nodeWidget;

public enum NodeGraphState {
	BEGIN, END, LINK, UNLINK, UNKNOWN;

	public boolean isTerminalState() {
		return this == BEGIN || this == END;
	}
}
