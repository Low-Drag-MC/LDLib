package com.lowdragmc.lowdraglib.utils;

public class Rect {

	public final int left;
	public final int right;
	public final int up;
	public final int down;

	private Rect(int left, int right, int up, int down) {
		this.left = left;
		this.right = right;
		this.up = up;
		this.down = down;
	}

	public Rect ofAbsolute(int left, int right, int up, int down) {
		return new Rect(left, right, up, down);
	}

	public Rect ofRelative(int left, int width, int up, int height) {
		return new Rect(left, left + width, up, up + height);
	}

	public Rect of(Position position, Size size) {
		return new Rect(position.x, position.x + size.width, position.y, position.y + size.height);
	}

}
