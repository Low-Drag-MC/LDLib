package com.lowdragmc.lowdraglib.gui.widget.nodeWidget;

import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node.Node;
import com.lowdragmc.lowdraglib.utils.Rect;

import javax.annotation.Nonnull;
import java.util.Objects;

public class NodeRect {
	@Nonnull
	private final Rect rect;
	private int pointer = 0;

	private NodeRect(Rect rect) {
		this.rect = Objects.requireNonNull(rect);
	}

	public static NodeRect warp(Rect rect) {
		return new NodeRect(rect);
	}

	public Rect unwarp() {
		return rect;
	}

	public Rect take(int height) {
		var area = Rect.ofRelative(rect.left, rect.getWidth(), rect.up + pointer, height);
		pointer += height;
		return area;
	}

	public Rect take(int height, int padding) {
		pointer += padding;
		return take(height);
	}

	public NodeRect walk(int height) {
		pointer += height;
		return this;
	}

	public boolean remainSpace() {
		return pointer < rect.getHeight();
	}

	public void reset() {
		pointer = 0;
	}


}
