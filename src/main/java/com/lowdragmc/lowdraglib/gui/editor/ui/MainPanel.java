package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author KilaBash
 * @date 2022/12/5
 * @implNote MainPanel
 */
public class MainPanel extends WidgetGroup {

    @Getter
    protected final Editor editor;

    public MainPanel(Editor editor) {
        super(0, 0, editor.getSize().width, editor.getSize().height);
        this.editor = editor;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 1) {
            editor.openMenu(mouseX, mouseY, new TreeBuilder<String , Runnable>("")
                            .leaf("123", () -> {})
                            .leaf("ldlib.author", () -> {})
                            .branch("branch1", branch1 ->
                                    branch1.leaf("ldlib.gui.editor.group.position", () -> {})
                                            .leaf("ldlib.gui.editor.name.text", () -> {})
                                            .branch("branch1-1", branch1_1 ->
                                                    branch1_1.leaf("ldlib.gui.editor.name.text", () -> {})
                                            )
                                            .leaf("ldlib.gui.editor.tips.id", () -> {})
                            )
                            .leaf("123dae", () -> {})
                            .branch("branch2", branch2 ->
                                    branch2.leaf("ldlib.gui.editor.name.text", () -> {})
                            )
                            .build())
                    .setOnNodeClicked(node -> System.out.println(node.getKey()));
            return true;
        }
        return false;
    }
}
