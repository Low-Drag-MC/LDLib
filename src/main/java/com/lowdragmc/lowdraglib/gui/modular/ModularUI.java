package com.lowdragmc.lowdraglib.gui.modular;

import com.google.common.base.Preconditions;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: KilaBash
 * Date: 2022/04/23
 * Description: ModularUI.
 */
public final class ModularUI {

    public final WidgetGroup mainGroup;

    private int screenWidth, screenHeight;
    private int width, height;
    @OnlyIn(Dist.CLIENT)
    private ModularUIGuiContainer guiContainer;
    private ModularUIContainer container;
    private final List<Runnable> uiCloseCallback;

    /**
     * UIHolder of this modular UI
     */
    public final IUIHolder holder;
    public final Player entityPlayer;

    public ModularUI(int width, int height, IUIHolder holder, Player entityPlayer) {
        this.mainGroup = new WidgetGroup(Position.ORIGIN, new Size(width, height));
        this.width = width;
        this.height = height;
        this.holder = holder;
        this.entityPlayer = entityPlayer;
        this.uiCloseCallback = new ArrayList<>();
    }

    public ModularUIContainer getModularUIContainer() {
        return container;
    }

    public void setModularUIContainer(ModularUIContainer container) {
        this.container = container;
    }


    public void registerCloseListener(Runnable runnable) {
        uiCloseCallback.add(runnable);
    }

    public void triggerCloseListeners() {
        uiCloseCallback.forEach(Runnable::run);
    }

    @OnlyIn(Dist.CLIENT)
    public ModularUIGuiContainer getModularUIGui() {
        return guiContainer;
    }

    @OnlyIn(Dist.CLIENT)
    public void setModularUIGui(ModularUIGuiContainer modularUIGuiContainer) {
        this.guiContainer = modularUIGuiContainer;
        setModularUIContainer(modularUIGuiContainer.getMenu());
    }

    public List<Widget> getFlatVisibleWidgetCollection() {
        List<Widget> widgetList = new ArrayList<>();

        for (Widget widget : mainGroup.widgets) {
            if (!widget.isVisible()) continue;
            widgetList.add(widget);

            if (widget instanceof WidgetGroup)
                widgetList.addAll(((WidgetGroup) widget).getContainedWidgets(false));
        }

        return widgetList;
    }

    public List<Widget> getFlatWidgetCollection() {
        List<Widget> widgetList = new ArrayList<>();
        for (Widget widget : mainGroup.widgets) {
            widgetList.add(widget);
            if (widget instanceof WidgetGroup) {
                widgetList.addAll(((WidgetGroup) widget).getContainedWidgets(true));
            }
        }
        return widgetList;
    }

    @OnlyIn(Dist.CLIENT)
    public void setSize(int width, int height) {
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            getModularUIGui().init();
        }
    }

    public void updateScreenSize(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        Position displayOffset = new Position(getGuiLeft(), getGuiTop());
        mainGroup.setParentPosition(displayOffset);
        mainGroup.onScreenSizeUpdate(screenWidth, screenHeight);
    }

    public void initWidgets() {
        mainGroup.setGui(this);
        mainGroup.initWidget();
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getGuiLeft() {
        return (getScreenWidth() - getWidth()) / 2;
    }

    public int getGuiTop() {
        return (getScreenHeight() - getHeight()) / 2;
    }

    public Rectangle toScreenCoords(Rectangle widgetRect) {
        return new Rectangle(getGuiLeft() + widgetRect.x, getGuiTop() + widgetRect.y, widgetRect.width, widgetRect.height);
    }

    public ModularUI widget(Widget widget) {
        Preconditions.checkNotNull(widget);
        mainGroup.addWidget(widget);
        return this;
    }

}
