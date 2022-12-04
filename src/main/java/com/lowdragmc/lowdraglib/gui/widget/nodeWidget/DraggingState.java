package com.lowdragmc.lowdraglib.gui.widget.nodeWidget;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import org.apache.http.util.Asserts;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * a isDragging process component class must add process to you widget's subsequence methods
 * {@link #onMouseClicked(double, double, int)}<br>
 * {@link #onMouseDragged(double, double, int, double, double)}<br>
 * {@link #onMouseReleased(double, double, int)}<br>
 * and implementation {@link DraggingSensitive}
 *
 * @param <T> the isDragging extra data type, can be null
 */
public class DraggingState<T> {
	/**
	 * the mouse button that trig the isDragging<br>
	 * {@link org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1} left click<br>
	 * {@link org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_3} middle click<br>
	 * {@link org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_2} right click<br>
	 */
	private int draggingButton = -1;
	/**
	 * whether it is isDragging ot not
	 */
	private boolean isDragging = false;
	/**
	 * maybe transformed into isDragging, trig by {@link #onMouseClicked(double, double, int)}
	 */
	private boolean draggingCandidate = false;
	/**
	 * the initial self position x of the widget, set by {@link #beginDragInternal(double, double, int, double, double)}
	 */
	private double initSelfPosX;
	/**
	 * the initial self position y of the widget, set by {@link #beginDragInternal(double, double, int, double, double)}
	 */
	private double initSelfPosY;
	/**
	 * the initial absolute position x of the widget, set by {@link #beginDragInternal(double, double, int, double, double)}
	 */
	private double initPositionX;
	/**
	 * the initial absolute position y of the widget, set by {@link #beginDragInternal(double, double, int, double, double)}
	 */
	private double initPositionY;

	/**
	 * the initial mouse x of the widget, set by {@link #beginDragInternal(double, double, int, double, double)}
	 */
	double initMouseX;
	/**
	 * the initial mouse y of the widget, set by {@link #beginDragInternal(double, double, int, double, double)}
	 */
	double initMouseY;

	/**
	 * the value holder for isDragging extra data
	 */
	@Nullable
	final T value;

	/**
	 * the updater method for isDragging extra data, called by {@link #beginDragInternal(double, double, int, double, double)}
	 */
	@Nullable
	final Consumer<T> updater;

	/**
	 * the attached widget, must implement {@link DraggingSensitive}
	 */
	private final Widget widget;


	/**
	 * constructor
	 *
	 * @param widget  the attached widget
	 * @param value   the extra isDragging data, can be null if it is redundant
	 * @param updater the extra isDragging data's update class, can be null too
	 */
	public DraggingState(Widget widget, @Nullable T value, @Nullable Consumer<T> updater) {
		Asserts.check(widget instanceof DraggingSensitive, "DraggingState must be instanced by DraggingSensitive");
		this.widget = widget;
		this.value = value;
		this.updater = updater;
	}

	/**
	 * constructor , no need for isDragging extra data
	 *
	 * @param widget the attached widget
	 */
	public DraggingState(Widget widget) {
		this(widget, null, null);
	}

	/**
	 * the internal method called by {@link #onMouseClicked(double, double, int)}
	 */
	private DraggingState mayDrag() {
		this.draggingCandidate = true;
		return this;
	}

	/**
	 * the internal method when drgging is end
	 */
	private void endDragInternal() {
		this.isDragging = false;
		this.draggingCandidate = false;
		this.initMouseX = -1;
		this.initMouseY = -1;
		this.draggingButton = -1;
	}

	/**
	 * calculate the relative mouse delta x
	 */
	public double deltaX(double mouseX) {
		return mouseX - initMouseX;
	}

	/**
	 * calculate the relative mouse delta y
	 */
	public double deltaY(double mouseY) {
		return mouseY - initMouseY;
	}

	/**
	 * must be called by attached widget
	 */
	public void onMouseClicked(double mouseX, double mouseY, int button) {
		if (widget.isMouseOverElement(mouseX, mouseY)) mayDrag();
	}

	/**
	 * must be called by attached widget
	 */
	public void onMouseReleased(double mouseX, double mouseY, int button) {
		if (this.isDragging && this.draggingButton == button) {
			((DraggingSensitive) widget).endDrag(mouseX, mouseY, button);
			this.endDragInternal();
		}
	}

	/**
	 * must be called by attached widget
	 */
	public boolean onMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (this.isDragging || (widget.isMouseOverElement(mouseX, mouseY) && this.draggingCandidate)) {
			if (!this.isDragging) {
				beginDragInternal(mouseX, mouseY, button, dragX, dragY);
			} else {
				((DraggingSensitive) widget).onDragged(mouseX, mouseY, button, dragX, dragY);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * called when begins isDragging, called internal for recording general widget data
	 */
	private void beginDragInternal(double mouseX, double mouseY, int button, double dragX, double dragY) {
		isDragging = true;
		this.draggingButton = button;
		this.initMouseX = mouseX;
		this.initMouseY = mouseY;
		this.initSelfPosX = widget.getSelfPosition().x;
		this.initSelfPosY = widget.getSelfPosition().y;
		this.initPositionX = widget.getPosition().x;
		this.initPositionY = widget.getPosition().y;
		if (this.updater != null) {
			this.updater.accept(value);
		}
		((DraggingSensitive) widget).beginDragged(mouseX, mouseY, button, dragX, dragY);
	}

	/**
	 * the getter method for the extra isDragging data
	 */
	public @Nullable T getValue() {
		return value;
	}

	public boolean isDragging() {
		return isDragging;
	}

	public int getDraggingButton() {
		assert isDragging;
		return draggingButton;
	}

	public double getInitSelfPosX() {
		assert isDragging;
		return initSelfPosX;
	}

	public double getInitSelfPosY() {
		assert isDragging;
		return initSelfPosY;
	}

	public double getInitPositionX() {
		assert isDragging;
		return initPositionX;
	}

	public double getInitPositionY() {
		assert isDragging;
		return initPositionY;
	}

	public double getInitMouseX() {
		assert isDragging;
		return initMouseX;
	}

	public double getInitMouseY() {
		assert isDragging;
		return initMouseY;
	}

}