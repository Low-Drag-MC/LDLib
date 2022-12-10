package com.lowdragmc.lowdraglib.gui.editor.data;

import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.ui.Editor;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
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

    public String getSuffix() {
        return getRegisterUI().name();
    }

    public void onClosed(Editor editor) {
    }

    public void onLoad(Editor editor) {
        editor.getResourcePanel().loadResource(getResources(), false);
    }

    public void attachMenu(String name, TreeBuilder.Menu menu) {

    }

}
