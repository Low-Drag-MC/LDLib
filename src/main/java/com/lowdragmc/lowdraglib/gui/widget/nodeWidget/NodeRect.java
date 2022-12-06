package com.lowdragmc.lowdraglib.gui.widget.nodeWidget;

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

	public void reset() {
		pointer = 0;
	}


}
