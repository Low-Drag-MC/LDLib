package com.lowdragmc.lowdraglib.gui.editor.data;

import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.ui.Editor;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author KilaBash
 * @date 2022/12/9
 * @implNote Project
 */
public abstract class Project {

    public abstract Resources getResources();

    /**
     * Save project
     */
    public abstract void saveProject(File file);

    /**
     * Load project from file. return null if loading failed
     */
    @Nullable
    public abstract Project loadProject(File file);

    public abstract Project newEmptyProject();

    public RegisterUI getRegisterUI() {
        return getClass().getAnnotation(RegisterUI.class);
    }

    /**
     * Suffix name of this project
     */
    public String getSuffix() {
        return getRegisterUI().name();
    }

    /**
     * Fired when project is closed
     */
    public void onClosed(Editor editor) {
    }

    /**
     * Fired when project is opened
     */
    public void onLoad(Editor editor) {
        editor.getResourcePanel().loadResource(getResources(), false);
    }

    /**
     * Attach menu
     * @param name menu name
     * @param menu current menu
     */
    public void attachMenu(Editor editor, String name, TreeBuilder.Menu menu) {

    }

    /**
     * Load resource from nbt data
     */
    public Resources loadResources(CompoundTag tag) {
        return Resources.fromNBT(tag);
    }

}
