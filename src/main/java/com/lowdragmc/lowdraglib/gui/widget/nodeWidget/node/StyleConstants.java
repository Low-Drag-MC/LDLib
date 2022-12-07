package com.lowdragmc.lowdraglib.gui.widget.nodeWidget.node;

import net.minecraft.client.Minecraft;

public final class StyleConstants {

	public static final int CONNECTOR_GAP_HEIGHT = 3;
	public static final int CONNECTOR_RADIUS = 3;
	public static final int CONNECTOR_DEFAULT_COLOR = 0xFF_00_00_00;

	public static final int NAME_STRING_PADDING = 5;
	public static final int NODE_DEFAULT_COLOR = 0xFF_FF_FF_FF;
	public static final int SLIDER_PADDING = 5;
	public static final float NODE_RADIUS = 8;
	public static final int SLIDER_HEIGHT = 10;
	public static final int NODE_DOWN_PADDING = 10;

	public static final int STRING_HEIGHT = Minecraft.getInstance().font.lineHeight;

	private StyleConstants() {
		throw new UnsupportedOperationException();
	}

}
