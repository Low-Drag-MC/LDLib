package com.lowdragmc.lowdraglib.gui.widget.nodeWidget;

/**
 * interface for widget to implementation to interact with {@link DraggingState}
 */
@FunctionalInterface
public interface DraggingSensitive {

	/**
	 * called when being dragged
	 */
	boolean onDragged(double mouseX, double mouseY, int button, double dragX, double dragY);

	/**
	 * called when begun dragged
	 */
	default void beginDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
	}

	/**
	 * called when drag has already end
	 */
	default void endDrag(double mouseX, double mouseY, int button) {
	}

}
