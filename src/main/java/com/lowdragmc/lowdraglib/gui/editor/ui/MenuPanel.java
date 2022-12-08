package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.data.Project;
import com.lowdragmc.lowdraglib.gui.editor.data.Resources;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.DialogWidget;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;

/**
 * @author KilaBash
 * @date 2022/12/6
 * @implNote MenuPanel
 */
public class MenuPanel extends WidgetGroup {
    public static final int HEIGHT = 16;

    @Getter
    protected final Editor editor;

    public MenuPanel(Editor editor) {
        super(0, 0, editor.getSize().getWidth() - ConfigPanel.WIDTH, HEIGHT);
        setClientSideWidget();
        this.editor = editor;
    }

    @Override
    public void initWidget() {
        this.setBackground(ColorPattern.T_RED.rectTexture());
        this.addWidget(new ImageWidget(2, 2, 12, 12, new ResourceTexture()));
        if (isRemote()) {
            int x = 20;
            var font = Minecraft.getInstance().font;
            int width = font.width(LocalizationUtils.format("ldlib.gui.editor.menu.file"));
            this.addWidget(new ButtonWidget(x, 0, width + 6, 16, new TextTexture("ldlib.gui.editor.menu.file"), cd -> {
                var pos = getPosition();
                editor.openMenu(pos.x + x, pos.y + 14, getFileMenu());
            }).setHoverTexture(ColorPattern.T_WHITE.rectTexture(), new TextTexture("ldlib.gui.editor.menu.file")));
        }
        super.initWidget();
    }

    protected TreeBuilder.Menu getFileMenu() {
        return TreeBuilder.Menu.start()
                .branch("ldlib.gui.editor.menu.new", menu -> {
                    menu.leaf("ldlib.gui.editor.menu.project", this::newProject);
                })
                .leaf("ldlib.gui.editor.menu.open", this::openProject)
                .leaf("ldlib.gui.editor.menu.save", this::saveProject)
                .branch("ldlib.gui.editor.menu.import", menu -> {
                    menu.leaf("ldlib.gui.editor.menu.resource", this::importResource);
                })
                .branch("ldlib.gui.editor.menu.export", menu -> {
                    menu.leaf("ldlib.gui.editor.menu.resource", this::exportResource);
                });
    }

    private void exportResource() {
        var resources = editor.resourcePanel.getResources();
        if (resources != null) {
            File path = new File(LDLMod.location, "ui_editor");
            DialogWidget.showFileDialog(editor, "ldlib.gui.editor.tips.save_resource", path, false,
                    DialogWidget.suffixFilter(".resource"), r -> {
                        if (r != null && !r.isDirectory()) {
                            if (!r.getName().endsWith(".resource")) {
                                r = new File(r.getParentFile(), r.getName() + ".resource");
                            }
                            try {
                                NbtIo.write(resources.serializeNBT(), r);
                            } catch (IOException ignored) {
                                // TODO
                            }
                        }
                    });
        }
    }

    private void importResource() {
        File path = new File(LDLMod.location, "ui_editor");
        DialogWidget.showFileDialog(editor, "ldlib.gui.editor.tips.load_resource", path, true,
                DialogWidget.suffixFilter(".resource"), r -> {
                    if (r != null && r.isFile()) {
                        try {
                            var tag = NbtIo.read(r);
                            if (tag != null) {
                                editor.resourcePanel.loadResource(Resources.fromNBT(tag), true);
                            }
                        } catch (IOException ignored) {
                            // TODO
                        }
                    }
                });
    }

    private void newProject() {
        editor.loadProject(Project.newEmptyProject());
    }

    private void saveProject() {
        var project = editor.getCurrentProject();
        if (project != null) {
            File path = new File(LDLMod.location, "ui_editor");
            DialogWidget.showFileDialog(editor, "ldlib.gui.editor.tips.save_resource", path, false,
                    DialogWidget.suffixFilter(".ui"), r -> {
                        if (r != null && !r.isDirectory()) {
                            if (!r.getName().endsWith(".ui")) {
                                r = new File(r.getParentFile(), r.getName() + ".ui");
                            }
                            try {
                                NbtIo.write(project.serializeNBT(), r);
                            } catch (IOException ignored) {
                                // TODO
                            }
                        }
                    });
        }
    }

    private void openProject() {
        File path = new File(LDLMod.location, "ui_editor");
        DialogWidget.showFileDialog(editor, "ldlib.gui.editor.tips.load_resource", path, true,
                DialogWidget.suffixFilter(".ui"), r -> {
                    if (r != null && r.isFile()) {
                        try {
                            var tag = NbtIo.read(r);
                            if (tag != null) {
                                editor.loadProject(Project.fromNBT(tag));
                            }
                        } catch (IOException ignored) {
                            // TODO
                        }
                    }
                });
    }

}
