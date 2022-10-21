package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.FileNode;
import com.lowdragmc.lowdraglib.gui.util.TreeNode;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DialogWidget extends WidgetGroup {
    private static final int HEIGHT = 128;
    private static final int WIDTH = 184;
    protected boolean isParentInVisible;
    protected Runnable onClosed;

    public DialogWidget(WidgetGroup parent, boolean isClient) {
        super(0, 0, parent.getSize().width, parent.getSize().height);
        if (isClient) setClientSideWidget();
        if (waitToAdd()) {
            parent.waitToAdded(this);
        } else {
            parent.addWidget(this);
        }
    }

    protected boolean waitToAdd() {
        return false;
    }

    public DialogWidget setOnClosed(Runnable onClosed) {
        this.onClosed = onClosed;
        return this;
    }

    public DialogWidget setParentInVisible() {
        this.isParentInVisible = true;
        for (Widget widget : parent.widgets) {
            if (widget != this) {
                widget.setVisible(false);
                widget.setActive(false);
            }
        }
        return this;
    }

    public void close() {
        parent.waitToRemoved(this);
        if (isParentInVisible) {
            for (Widget widget : parent.widgets) {
                widget.setVisible(true);
                widget.setActive(true);
            }
        }
        if (onClosed != null) {
            onClosed.run();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
//        matrixStack.translate(0, 0, 200);
        super.drawInBackground(matrixStack, mouseX, mouseY, partialTicks);
//        matrixStack.translate(0, 0, -200);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOverElement(mouseX, mouseY)) return false;
        super.mouseClicked(mouseX, mouseY, button);
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!super.keyPressed(keyCode, scanCode, modifiers) && keyCode == GLFW.GLFW_KEY_ESCAPE) {
            writeClientAction(-1, x->{});
            close();
        }
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        super.keyReleased(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean charTyped(char codePoint, int modifiers) {
        super.charTyped(codePoint, modifiers);
        return true;
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        super.handleClientAction(id, buffer);
        if (id == -1) {
            close();
        }
    }

    public static Predicate<TreeNode<File, File>> suffixFilter(String suffix) {
        return node -> !(node.isLeaf() && node.getContent().isFile() && !node.getContent().getName().toLowerCase().endsWith(suffix.toLowerCase()));
    }

    public static DialogWidget showFileDialog(WidgetGroup parent, String title, File dir, boolean isSelector, Predicate<TreeNode<File, File>> valid, Consumer<File> result) {
        Size size = parent.getSize();
        DialogWidget dialog = new DialogWidget(parent, true);
        if (!dir.isDirectory()) {
            if (!dir.mkdirs()) {
                return dialog;
            }
        }
        dialog.addWidget(new ImageWidget(0, 0, parent.getSize().width, parent.getSize().height, new ColorRectTexture(0x4f000000)));
        AtomicReference<File> selected = new AtomicReference<>();
        selected.set(dir);
        dialog.addWidget(new TreeListWidget<>(0, 0, 130, size.height, new FileNode(dir).setValid(valid), node -> selected.set(node.getKey()))
                .setNodeTexture(ResourceBorderTexture.BORDERED_BACKGROUND)
                .canSelectNode(true)
                .setLeafTexture(new ResourceTexture("ldlib:textures/gui/darkened_slot.png")));
        int x = 130 + (size.width - 133 - WIDTH) / 2;
        int y = (size.height - HEIGHT) / 2;
        dialog.addWidget(new ImageWidget(x, y, WIDTH, HEIGHT, ResourceBorderTexture.BORDERED_BACKGROUND));
        dialog.addWidget(new ButtonWidget(x + WIDTH / 2 - 30 - 20, y + HEIGHT - 32, 40, 20, cd -> {
            dialog.close();
            if (result != null) result.accept(selected.get());
        }).setButtonTexture(new ResourceTexture("ldlib:textures/gui/darkened_slot.png"), new TextTexture("ldlib.gui.tips.confirm", -1).setDropShadow(true)).setHoverBorderTexture(1, 0xff000000));
        dialog.addWidget(new ButtonWidget(x + WIDTH / 2 + 30 - 20, y + HEIGHT - 32, 40, 20, cd -> {
            dialog.close();
            if (result != null) result.accept(null);
        }).setButtonTexture(new ResourceTexture("ldlib:textures/gui/darkened_slot.png"), new TextTexture("ldlib.gui.tips.cancel", 0xffff0000).setDropShadow(true)).setHoverBorderTexture(1, 0xff000000));
        if (isSelector) {
            dialog.addWidget(new ImageWidget(x + 8, y + HEIGHT / 2 - 5, WIDTH - 16, 20, new GuiTextureGroup(new ColorBorderTexture(1, -1), new ColorRectTexture(0xff000000))));
            dialog.addWidget(new ImageWidget(x + 8, y + HEIGHT / 2 - 5, WIDTH - 16, 20,
                    new TextTexture("", -1).setWidth(WIDTH - 16).setType(TextTexture.TextType.ROLL)
                            .setSupplier(() -> {
                                if (selected.get() != null) {
                                    return selected.get().toString();
                                }
                                return "no file selected";
                            })));
        } else {
            dialog.addWidget(new TextFieldWidget(x + WIDTH / 2 - 38, y + HEIGHT / 2 - 10, 76, 20,  ()->{
                File file = selected.get();
                if (file != null && !file.isDirectory()) {
                    return selected.get().getName();
                }
                return "";
            }, res->{
                File file = selected.get();
                if (file == null) return;
                if (file.isDirectory()) {
                    selected.set(new File(file, res));
                } else {
                    selected.set(new File(file.getParent(), res));
                }
            }));
        }
        dialog.addWidget(new ButtonWidget(x + 15, y + 15, 20, 20, cd -> {
            File file = selected.get();
            if (file != null) {
                Util.getPlatform().openFile(file.isDirectory() ? file : file.getParentFile());
            }
        }).setButtonTexture(new ResourceTexture("ldlib:textures/gui/darkened_slot.png"), new TextTexture("F", -1).setDropShadow(true)).setHoverBorderTexture(1, 0xff000000).setHoverTooltips("ldlib.gui.tips.open_folder"));
        dialog.addWidget(new ImageWidget(x + 15, y + 20, WIDTH - 30,10, new TextTexture(title, -1).setWidth(WIDTH - 30).setDropShadow(true)));
        //        dialog.addWidget(new LabelWidget(x + WIDTH / 2, y + 11, ()->title).setTextColor(-1));
        return dialog;
    }
}
