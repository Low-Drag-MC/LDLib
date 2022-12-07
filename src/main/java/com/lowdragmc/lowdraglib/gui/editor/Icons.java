package com.lowdragmc.lowdraglib.gui.editor;

import com.lowdragmc.lowdraglib.gui.texture.*;

/**
 * @author KilaBash
 * @date 2022/12/1
 * @implNote Icons
 */
public class Icons {
    public static ResourceTexture LEFT = new ResourceTexture("ldlib:textures/gui/left.png");
    public static ResourceTexture UP = new ResourceTexture("ldlib:textures/gui/up.png");
    public static ResourceTexture DOWN = new ResourceTexture("ldlib:textures/gui/down.png");
    public static ResourceTexture RIGHT = new ResourceTexture("ldlib:textures/gui/right.png");
    public static ResourceTexture RESOURCE_TAB = new ResourceTexture("ldlib:textures/gui/resource_tab.png");
    public static ResourceTexture WIDGET_TAB = new ResourceTexture("ldlib:textures/gui/widget_tab.png");
    public static ResourceTexture BASIC_WIDGET_TAB = new ResourceTexture("ldlib:textures/gui/basic_widget_tab.png");
    public static ResourceTexture ADVANCED_WIDGET_TAB = new ResourceTexture("ldlib:textures/gui/advanced_widget_tab.png");

    public static IGuiTexture borderText(int border, String text, int color) {
        return new GuiTextureGroup(new ColorBorderTexture(border, color), new TextTexture(text, color));
    }

    public static IGuiTexture borderText(String text) {
        return borderText(1, text, -1);
    }

}
