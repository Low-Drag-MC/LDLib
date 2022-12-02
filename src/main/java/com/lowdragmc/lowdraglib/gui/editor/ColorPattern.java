package com.lowdragmc.lowdraglib.gui.editor;

import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;

/**
 * @author KilaBash
 * @date 2022/12/1
 * @implNote ColorPattern
 */
public enum ColorPattern {
    WHITE(0xffffffff),
    BLACK(0xff222222),
    GRAY(0xff666666),
    T_WHITE(0x88ffffff),
    T_BLACK(0x44222222),
    T_GRAY(0x66666666),
    ;
    public final int color;

    ColorPattern(int color) {
        this.color = color;
    }

    public ColorRectTexture rectTexture() {
        return new ColorRectTexture(color);
    }

    public ColorBorderTexture borderTexture(int border) {
        return new ColorBorderTexture(border, color);
    }
}
