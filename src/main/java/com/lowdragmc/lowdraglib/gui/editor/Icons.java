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
    public static ResourceTexture ADD = icon("add");
    public static ResourceTexture SAVE = icon("save");
    public static ResourceTexture HELP = icon("help");
    public static ResourceTexture REMOVE = icon("remove");
    public static ResourceTexture DELETE = icon("delete");
    public static ResourceTexture EXPORT = icon("export");
    public static ResourceTexture IMPORT = icon("import");
    public static ResourceTexture OPEN_FILE = icon("open_file");
    public static ResourceTexture ADD_FILE = icon("add_file");
    public static ResourceTexture EDIT_FILE = icon("edit_file");
    public static ResourceTexture REMOVE_FILE = icon("remove_file");

    private static ResourceTexture icon(String name) {
        return new ResourceTexture("ldlib:textures/gui/icon/%s.png".formatted(name));
    }

    public static IGuiTexture borderText(int border, String text, int color) {
        return new GuiTextureGroup(new ColorBorderTexture(border, color), new TextTexture(text, color).transform(1, 1));
    }

    public static IGuiTexture borderText(String text) {
        return borderText(1, text, -1);
    }

}
