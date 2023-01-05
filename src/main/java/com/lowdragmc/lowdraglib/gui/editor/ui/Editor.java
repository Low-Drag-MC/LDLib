package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.data.Project;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import com.lowdragmc.lowdraglib.gui.util.TreeNode;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.io.File;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author KilaBash
 * @date 2022/11/30
 * @implNote MainPage
 */
public class Editor extends WidgetGroup {
    @OnlyIn(Dist.CLIENT)
    public static Editor INSTANCE;
    @Getter
    protected final File workSpace;
    @Getter
    protected Project currentProject;
    @Getter
    protected MenuPanel menuPanel;
    @Getter
    protected TabContainer tabPages;
    @Getter
    protected ConfigPanel configPanel;
    @Getter
    protected ResourcePanel resourcePanel;
    @Getter
    protected WidgetGroup floatView;
    @Getter
    protected ToolPanel toolPanel;
    @Getter
    protected String copyType;
    @Getter
    protected Object copied;

    public Editor(File workSpace) {
        super(0, 0, 10, 10);
        setClientSideWidget();
        this.workSpace = workSpace;
    }

    @Override
    public void setGui(ModularUI gui) {
        super.setGui(gui);
        if (isRemote()) {
            if (gui == null) {
                INSTANCE = null;
            } else {
                INSTANCE = this;
                getGui().registerCloseListener(() -> INSTANCE = null);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onScreenSizeUpdate(int screenWidth, int screenHeight) {
        setSize(new Size(screenWidth, screenHeight));
        super.onScreenSizeUpdate(screenWidth, screenHeight);
        this.clearAllWidgets();

        addWidget(tabPages = new TabContainer(0, 0, screenWidth, screenHeight));
        addWidget(toolPanel = new ToolPanel(this));
        addWidget(configPanel = new ConfigPanel(this));
        addWidget(resourcePanel = new ResourcePanel(this));
        addWidget(menuPanel = new MenuPanel(this));
        addWidget(floatView = new WidgetGroup(0, 0, screenWidth, screenHeight));

        loadProject(currentProject);
    }

    public DialogWidget openDialog(DialogWidget dialog) {
        this.addWidget(dialog);
        Position pos = dialog.getPosition();
        Size size = dialog.getSize();
        if (pos.x + size.width > getGui().getScreenWidth()) {
            dialog.addSelfPosition(pos.x + size.width - getGui().getScreenWidth(), 0);
        } else if (pos.x < 0) {
            dialog.addSelfPosition(-pos.x, 0);
        }
        if (pos.y + size.height > getGui().getScreenHeight()) {
            dialog.addSelfPosition(0, pos.y + size.height - getGui().getScreenHeight());
        } else if (pos.y < 0) {
            dialog.addSelfPosition(0, -pos.y);
        }
        return dialog;
    }

    public <T, C> MenuWidget<T, C> openMenu(double posX, double posY, TreeNode<T, C> menuNode) {
        IGuiTexture nodeTexture = new IGuiTexture() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void draw(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
                ColorPattern.BLACK.rectTexture().draw(stack, mouseX, mouseY, x, y, width, height);
                Icons.RIGHT.draw(stack, mouseX, mouseY, x + width - height + 3, y + 3, height - 6, height - 6);
            }
        };

        var menu = new MenuWidget<>((int) posX, (int) posY, 14, menuNode)
                .setNodeTexture(nodeTexture)
                .setLeafTexture(ColorPattern.BLACK.rectTexture())
                .setNodeHoverTexture(ColorPattern.T_GRAY.rectTexture());
        waitToAdded(menu.setBackground(new ColorRectTexture(0xff3C4146), ColorPattern.GRAY.borderTexture(1)));

        return menu;
    }

    public void openMenu(double posX, double posY, TreeBuilder.Menu menuBuilder) {
        if (menuBuilder == null) return;
        openMenu(posX, posY, menuBuilder.build())
                .setCrossLinePredicate(TreeBuilder.Menu::isCrossLine)
                .setKeyIconSupplier(TreeBuilder.Menu::getIcon)
                .setKeyNameSupplier(TreeBuilder.Menu::getName)
                .setOnNodeClicked(TreeBuilder.Menu::handle);
    }

    public void loadProject(Project project) {
        if (currentProject != null) {
            currentProject.onClosed(this);
        }

        currentProject = project;
        tabPages.clearAllWidgets();
        toolPanel.clearAllWidgets();

        if (currentProject != null) {
            currentProject.onLoad(this);
        }
    }

    public void setCopy(String copyType, Object copied) {
        this.copied = copied;
        this.copyType = copyType;
    }

    public void ifCopiedPresent(String copyType, Consumer<Object> consumer) {
        if (Objects.equals(copyType, this.copyType)) {
            consumer.accept(copied);
        }
    }

}
