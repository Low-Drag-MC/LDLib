package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.data.Project;
import com.lowdragmc.lowdraglib.gui.editor.data.Resources;
import com.lowdragmc.lowdraglib.gui.editor.runtime.UIDetector;
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
import java.util.stream.Collectors;

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
        var fileMenu = TreeBuilder.Menu.start()
                .branch("ldlib.gui.editor.menu.new", this::newProject)
                .crossLine()
                .leaf(Icons.OPEN_FILE, "ldlib.gui.editor.menu.open", this::openProject)
                .leaf(Icons.SAVE, "ldlib.gui.editor.menu.save", this::saveProject)
                .crossLine()
                .branch(Icons.IMPORT, "ldlib.gui.editor.menu.import", menu -> {
                    menu.leaf("ldlib.gui.editor.menu.resource", this::importResource);
                })
                .branch(Icons.EXPORT, "ldlib.gui.editor.menu.export", menu -> {
                    menu.leaf("ldlib.gui.editor.menu.resource", this::exportResource);
                });
        if (editor.currentProject != null) {
            editor.currentProject.attachMenu(editor, "file", fileMenu);
        }
        return fileMenu;
    }

    private void exportResource() {
        var resources = editor.resourcePanel.getResources();
        if (resources != null) {
            DialogWidget.showFileDialog(editor, "ldlib.gui.editor.tips.save_resource", editor.getWorkSpace(), false,
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
        if (editor.currentProject != null) {
            DialogWidget.showFileDialog(editor, "ldlib.gui.editor.tips.load_resource", editor.getWorkSpace(), true,
                    DialogWidget.suffixFilter(".resource"), r -> {
                        if (r != null && r.isFile()) {
                            try {
                                var tag = NbtIo.read(r);
                                if (tag != null) {
                                    editor.resourcePanel.loadResource(editor.currentProject.loadResources(tag), true);
                                }
                            } catch (IOException ignored) {
                                // TODO
                            }
                        }
                    });
        }
    }

    private void newProject(TreeBuilder.Menu menu) {
        for (var project : UIDetector.REGISTER_PROJECTS) {
            String name = "ldlib.gui.editor.menu.project." + project.getSuffix();
            menu = menu.leaf(name, () -> editor.loadProject(project.newEmptyProject()));
        }
    }

    private void saveProject() {
        var project = editor.getCurrentProject();
        if (project != null) {
            String suffix = "." + project.getSuffix();
            DialogWidget.showFileDialog(editor, "ldlib.gui.editor.tips.save_project", editor.getWorkSpace(), false,
                    DialogWidget.suffixFilter(suffix), file -> {
                        if (file != null && !file.isDirectory()) {
                            if (!file.getName().endsWith(suffix)) {
                                file = new File(file.getParentFile(), file.getName() + suffix);
                            }
                            project.saveProject(file);
                        }
                    });
        }
    }

    private void openProject() {
        var suffixes = UIDetector.REGISTER_PROJECTS.stream().map(Project::getSuffix).collect(Collectors.toSet());
        DialogWidget.showFileDialog(editor, "ldlib.gui.editor.tips.load_project", editor.getWorkSpace(), true,
                node -> {
                    if (node.isLeaf() && node.getContent().isFile()) {
                        String file = node.getContent().getName().toLowerCase();
                        for (String suffix : suffixes) {
                            if (file.endsWith(suffix.toLowerCase())) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return true;
                }, r -> {
                    if (r != null && r.isFile()) {
                        String file = r.getName().toLowerCase();
                        for (var project : UIDetector.REGISTER_PROJECTS) {
                            if (file.endsWith(project.getSuffix())) {
                                var p = project.loadProject(r);
                                if (p != null) {
                                    editor.loadProject(p);
                                }
                            }
                        }
                    }
                });
    }

}
