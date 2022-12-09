package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.data.Project;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import com.lowdragmc.lowdraglib.gui.util.TreeNode;
import com.lowdragmc.lowdraglib.gui.widget.MenuWidget;
import com.lowdragmc.lowdraglib.gui.widget.TabButton;
import com.lowdragmc.lowdraglib.gui.widget.TabContainer;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author KilaBash
 * @date 2022/11/30
 * @implNote MainPage
 */
public class Editor extends WidgetGroup {
    @OnlyIn(Dist.CLIENT)
    public static Editor INSTANCE;
    @Getter
    protected Project currentProject;
    @Getter
    protected MenuPanel menuPanel;
    @Getter
    protected TabContainer tabPages;
    @Getter
    protected MainPanel currentMainPanel;
    @Getter
    protected ConfigPanel configPanel;
    @Getter
    protected ResourcePanel resourcePanel;
    @Getter
    protected WidgetPanel widgetPanel;


    public Editor() {
        super(0, 0, 10, 10);
        setClientSideWidget();
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
        addWidget(widgetPanel = new WidgetPanel(this));
        addWidget(configPanel = new ConfigPanel(this));
        addWidget(resourcePanel = new ResourcePanel(this));
        addWidget(menuPanel = new MenuPanel(this));

        loadProject(currentProject);
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
        openMenu(posX, posY, menuBuilder.build())
                .setCrossLinePredicate(TreeBuilder.Menu::isCrossLine)
                .setKeyIconSupplier(TreeBuilder.Menu::getIcon)
                .setKeyNameSupplier(TreeBuilder.Menu::getName)
                .setOnNodeClicked(TreeBuilder.Menu::handle);
    }

    public void loadProject(Project project) {
        currentProject = project;
        if (currentProject != null) {
            resourcePanel.loadResource(project.resources(), false);

            tabPages.clearAllWidgets();
            tabPages.addTab(new TabButton(50, 16, 60, 14).setTexture(
                    new GuiTextureGroup(ColorPattern.T_GREEN.rectTexture().setBottomRadius(10).transform(0, 0.4f), new TextTexture("Main")),
                    new GuiTextureGroup(ColorPattern.T_RED.rectTexture().setBottomRadius(10).transform(0, 0.4f), new TextTexture("Main"))
            ), currentMainPanel = new MainPanel(this, project.root()));
        }
    }

}
