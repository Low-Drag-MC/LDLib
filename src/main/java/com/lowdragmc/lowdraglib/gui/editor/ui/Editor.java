package com.lowdragmc.lowdraglib.gui.editor.ui;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.data.Project;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import com.lowdragmc.lowdraglib.gui.util.TreeNode;
import com.lowdragmc.lowdraglib.gui.widget.MenuWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

/**
 * @author KilaBash
 * @date 2022/11/30
 * @implNote MainPage
 */
public class Editor extends WidgetGroup {
    @OnlyIn(Dist.CLIENT)
    public static Editor INSTANCE;

    @Getter
    private UIWrapper focusUI;

    @Getter
    protected UIWrapper hover;

    @Getter
    protected UIWrapper lastHover;

    @Getter
    protected UIWrapper lastDraggingHover;

    @Getter
    protected Project currentProject;

    @Getter
    protected MainPanel mainPanel;

    @Getter
    protected ConfigPanel configPanel;

    @Getter
    protected ResourcePanel resourcePanel;

    @Getter
    protected WidgetPanel widgetPanel;

    @Getter
    protected UIWrapper rootWidget;

    public Editor() {
        super(0, 0, 10, 10);
        setClientSideWidget();
        currentProject = new Project();
    }

    @Override
    public void initWidget() {
        super.initWidget();
        if (isRemote()) {
            INSTANCE = this;
            getGui().registerCloseListener(() -> INSTANCE = null);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onScreenSizeUpdate(int screenWidth, int screenHeight) {
        setSize(new Size(screenWidth, screenHeight));
        super.onScreenSizeUpdate(screenWidth, screenHeight);
        this.clearAllWidgets();

        addWidget(mainPanel = new MainPanel(this));
        addWidget(configPanel = new ConfigPanel(this));
        addWidget(resourcePanel = new ResourcePanel(this));
        addWidget(widgetPanel = new WidgetPanel(this));

        mainPanel.addWidget(rootWidget = new UIWrapper(this, new WidgetGroup(0, 0, 200, 200).setBackground(ResourceBorderTexture.BORDERED_BACKGROUND)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.hover = null;
        this.lastDraggingHover = null;
        super.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
        this.lastHover = hover;
    }

    public void setFocusUI(UIWrapper focusUI) {
        if (lastHover == focusUI) {
            this.focusUI = focusUI;
        }
    }

    public boolean setHover(UIWrapper hover) {
//        if (this.hover == null || (this.hover.getParent() == hover.getParent())) {
//            this.hover = hover;
//            return true;
//        }
        this.hover = hover;
        return false;
    }

    public boolean setLastDraggingHover(UIWrapper dragging) {
        if (this.lastDraggingHover == null) {
            this.lastDraggingHover = dragging;
            return true;
        }
        return false;
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
                .setKeyIconSupplier(TreeBuilder.Menu::getIcon)
                .setKeyNameSupplier(TreeBuilder.Menu::getName)
                .setOnNodeClicked(TreeBuilder.Menu::handle);
    }

}
