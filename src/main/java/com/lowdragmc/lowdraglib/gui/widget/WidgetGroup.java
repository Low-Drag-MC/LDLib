package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.animation.Animation;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.editor.runtime.UIDetector;
import com.lowdragmc.lowdraglib.gui.ingredient.IGhostIngredientTarget;
import com.lowdragmc.lowdraglib.gui.ingredient.IIngredientSlot;
import com.lowdragmc.lowdraglib.gui.ingredient.Target;
import com.lowdragmc.lowdraglib.gui.modular.WidgetUIAccess;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@RegisterUI(name = "group", group = "advanced")
public class WidgetGroup extends Widget implements IGhostIngredientTarget, IIngredientSlot, IConfigurableWidgetGroup {

    public final List<Widget> widgets = new ArrayList<>();
    private final WidgetGroupUIAccess groupUIAccess = new WidgetGroupUIAccess();
    private final boolean isDynamicSized;
    protected final List<Widget> waitToRemoved;
    protected final List<Widget> waitToAdded;

    public WidgetGroup() {
        this(0, 0,50, 50);
        setBackground(ResourceBorderTexture.BORDERED_BACKGROUND);
    }

    public WidgetGroup(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.isDynamicSized = false;
        waitToRemoved = new ArrayList<>();
        waitToAdded = new ArrayList<>();
    }

    public WidgetGroup(Position position) {
        super(position, Size.ZERO);
        this.isDynamicSized = true;
        waitToRemoved = new ArrayList<>();
        waitToAdded = new ArrayList<>();
    }

    public WidgetGroup(Position position, Size size) {
        super(position, size);
        this.isDynamicSized = false;
        waitToRemoved = new ArrayList<>();
        waitToAdded = new ArrayList<>();
    }

    @Override
    public void writeInitialData(FriendlyByteBuf buffer) {
        for (Widget widget : widgets) {
            if (widget.isInitialized() && !widget.isClientSideWidget) {
                widget.writeInitialData(buffer);
            }
        }
    }

    @Override
    public void readInitialData(FriendlyByteBuf buffer) {
        for (Widget widget : widgets) {
            if (widget.isInitialized() && !widget.isClientSideWidget) {
                widget.readInitialData(buffer);
            }
        }
    }

    @Override
    public WidgetGroup setClientSideWidget() {
        super.setClientSideWidget();
        for (Widget widget : widgets) {
            widget.setClientSideWidget();
        }
        return this;
    }

    public List<Widget> getContainedWidgets(boolean includeHidden) {
        ArrayList<Widget> containedWidgets = new ArrayList<>(widgets.size());

        for (Widget widget : widgets) {
            if (!widget.isVisible() && !includeHidden) continue;
            containedWidgets.add(widget);
            if (widget instanceof WidgetGroup)
                containedWidgets.addAll(((WidgetGroup) widget).getContainedWidgets(includeHidden));
        }

        return containedWidgets;
    }

    @Override
    protected void onPositionUpdate() {
        Position selfPosition = getPosition();
        for (Widget widget : widgets) {
            widget.setParentPosition(selfPosition);
        }
        recomputeSize();
    }

    @Override
    public boolean isMouseOverElement(double mouseX, double mouseY) {
        for (int i = widgets.size() - 1; i >= 0; i--) {
            Widget widget = widgets.get(i);
            if(widget.isVisible() && widget.isMouseOverElement(mouseX, mouseY)) {
                return true;
            }
        }
        return super.isMouseOverElement(mouseX, mouseY);
    }

    @Nullable
    @Override
    public Widget getHoverElement(double mouseX, double mouseY) {
        for (int i = widgets.size() - 1; i >= 0; i--) {
            Widget widget = widgets.get(i);
            if(widget.isVisible()) {
                widget = widget.getHoverElement(mouseX, mouseY);
                if (widget != null) {
                    return widget;
                }
            }
        }
        return super.getHoverElement(mouseX, mouseY);
    }

    protected void onChildSelfPositionUpdate(Widget child) {

    }

    protected void onChildSizeUpdate(Widget child) {

    }

    protected boolean recomputeSize() {
        if (isDynamicSized) {
            Size currentSize = getSize();
            Size dynamicSize = computeDynamicSize();
            if (!currentSize.equals(dynamicSize)) {
                setSize(dynamicSize);
                if (uiAccess != null)
                    uiAccess.notifySizeChange();
                return true;
            }
        }
        return false;
    }

    protected Size computeDynamicSize() {
        Position selfPosition = getPosition();
        Size currentSize = getSize();
        for (Widget widget : widgets) {
            Position size = widget.getPosition().add(widget.getSize()).subtract(selfPosition);
            if (size.x > currentSize.width) {
                currentSize = new Size(size.x, currentSize.height);
            }
            if (size.y > currentSize.height) {
                currentSize = new Size(currentSize.width, size.y);
            }
        }
        return currentSize;
    }

    public void setVisible(boolean visible) {
        if (this.isVisible() == visible) {
            return;
        }
        super.setVisible(visible);
    }

    public boolean isChild(Widget widget) {
        return widget.isParent(this);
    }

    public WidgetGroup addWidget(Widget widget) {
        return addWidget(widgets.size(), widget);
    }

    public WidgetGroup addWidget(int index, Widget widget) {
        if (widget == this) {
            throw new IllegalArgumentException("Cannot add self");
        }
        if (widgets.contains(widget)) {
            throw new IllegalArgumentException("Already added");
        }
        this.widgets.add(index, widget);
        widget.setUiAccess(groupUIAccess);
        widget.setGui(gui);
        widget.setParent(this);
        widget.setParentPosition(getPosition());
        if (isClientSideWidget) {
            widget.setClientSideWidget();
        }
        if (isInitialized() && !widget.isInitialized()) {
            widget.initWidget();
            if (!isRemote() && !widget.isClientSideWidget) {
                writeUpdateInfo(2, buffer -> {
                    buffer.writeVarInt(index);
                    widget.writeInitialData(buffer);
                });
            }
        }
        recomputeSize();
        if (uiAccess != null && !isClientSideWidget) {
            uiAccess.notifyWidgetChange();
        }
        return this;
    }

    public void addWidgetAnima(Widget widget, Animation animation) {
        addWidget(widget);
        widget.animation(animation.setIn());
    }

    public void removeWidgetAnima(Widget widget, Animation animation) {
        widget.animation(animation.setOut().setOnFinish(() -> {
            widget.setVisible(false);
            waitToRemoved(widget);
        }));
    }

    public void waitToRemoved(Widget widget) {
        synchronized (waitToRemoved) {
            waitToRemoved.add(widget);
        }
    }

    public void waitToAdded(Widget widget) {
        synchronized (waitToAdded) {
            waitToAdded.add(widget);
        }
    }

    public int getAllWidgetSize() {
        return widgets.size() - waitToRemoved.size() + waitToAdded.size();
    }

    public void removeWidget(Widget widget) {
        if (!widgets.contains(widget)) {
            return;
        }
        this.widgets.remove(widget);
        widget.setUiAccess(null);
        widget.setGui(null);
        widget.setParentPosition(Position.ORIGIN);
        recomputeSize();
        if (uiAccess != null && !isClientSideWidget) {
            this.uiAccess.notifyWidgetChange();
        }
    }

    public void clearAllWidgets() {
        this.widgets.forEach(it -> {
            it.setUiAccess(null);
            it.setGui(null);
            it.setParentPosition(Position.ORIGIN);
        });
        this.widgets.clear();
        if (!waitToRemoved.isEmpty()) {
            synchronized (waitToRemoved) {
                waitToRemoved.clear();
            }
        }
        if (!waitToAdded.isEmpty()) {
            synchronized (waitToAdded) {
                waitToAdded.clear();
            }
        }
        recomputeSize();
        if (uiAccess != null) {
            this.uiAccess.notifyWidgetChange();
        }
    }


    @Override
    public void initWidget() {
        super.initWidget();
        for (Widget widget : widgets) {
            widget.setGui(gui);
            widget.initWidget();
        }
    }

    @Override
    public List<SlotWidget> getNativeWidgets() {
        ArrayList<SlotWidget> nativeWidgets = new ArrayList<>();
        for (Widget widget : widgets) {
            nativeWidgets.addAll(widget.getNativeWidgets());
        }
        return nativeWidgets;
    }

    @Override
    public List<Target> getPhantomTargets(Object ingredient) {
        if (!isVisible()) {
            return Collections.emptyList();
        }
        ArrayList<Target> targets = new ArrayList<>();
        for (Widget widget : widgets) {
            if (widget.isVisible() && widget instanceof IGhostIngredientTarget) {
                targets.addAll(((IGhostIngredientTarget) widget).getPhantomTargets(ingredient));
            }
        }
        return targets;
    }

    @Override
    public Object getIngredientOverMouse(double mouseX, double mouseY) {
        if (!isVisible()) {
            return null;
        }
        for (Widget widget : widgets) {
            if (widget.isVisible() && widget instanceof IIngredientSlot ingredientSlot) {
                Object result = ingredientSlot.getIngredientOverMouse(mouseX, mouseY);
                if (result != null) return result;
            }
        }
        return null;
    }

    @Override
    public void detectAndSendChanges() {
        for (Widget widget : widgets) {
            if (widget.isActive()) {
                widget.detectAndSendChanges();
            }
        }
        handleSyncWidget();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void updateScreen() {
        super.updateScreen();
        for (Widget widget : widgets) {
            if (widget.isActive()) {
                widget.updateScreen();
            }
        }
        handleSyncWidget();
    }

    protected void handleSyncWidget() {
        if (!waitToRemoved.isEmpty()) {
            synchronized (waitToRemoved) {
                waitToRemoved.forEach(this::removeWidget);
                waitToRemoved.clear();
            }
        }
        if (!waitToAdded.isEmpty()) {
            synchronized (waitToAdded) {
                waitToAdded.forEach(this::addWidget);
                waitToAdded.clear();
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInForeground(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInForeground(poseStack, mouseX, mouseY, partialTicks);
        for (Widget widget : widgets) {
            if (widget.isVisible()) {
                RenderSystem.setShaderColor(1, 1, 1, 1);
                RenderSystem.enableBlend();
                if (widget.inAnimate()) {
                    widget.animation.drawInForeground(poseStack, mouseX, mouseY, partialTicks);
                } else {
                    widget.drawInForeground(poseStack, mouseX, mouseY, partialTicks);
                }

            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
        for (Widget widget : widgets) {
            if (widget.isVisible()) {
                RenderSystem.setShaderColor(1, 1, 1, 1);
                RenderSystem.enableBlend();
                if (widget.inAnimate()) {
                    widget.animation.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
                } else {
                    widget.drawInBackground(poseStack, mouseX, mouseY, partialTicks);
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
        for (int i = widgets.size() - 1; i >= 0; i--) {
            Widget widget = widgets.get(i);
            if(widget.isVisible() && widget.isActive() && widget.mouseWheelMove(mouseX, mouseY, wheelDelta)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = widgets.size() - 1; i >= 0; i--) {
            Widget widget = widgets.get(i);
            if(widget.isVisible() && widget.isActive() && widget.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (int i = widgets.size() - 1; i >= 0; i--) {
            Widget widget = widgets.get(i);
            if(widget.isVisible() && widget.isActive() && widget.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (int i = widgets.size() - 1; i >= 0; i--) {
            Widget widget = widgets.get(i);
            if(widget.isVisible() && widget.isActive() && widget.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean mouseMoved(double mouseX, double mouseY) {
        for (int i = widgets.size() - 1; i >= 0; i--) {
            Widget widget = widgets.get(i);
            if(widget.isVisible() && widget.isActive() && widget.mouseMoved(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (int i = widgets.size() - 1; i >= 0; i--) {
            Widget widget = widgets.get(i);
            if(widget.isVisible() && widget.isActive() && widget.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (int i = widgets.size() - 1; i >= 0; i--) {
            Widget widget = widgets.get(i);
            if(widget.isVisible() && widget.isActive() && widget.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean charTyped(char codePoint, int modifiers) {
        for (int i = widgets.size() - 1; i >= 0; i--) {
            Widget widget = widgets.get(i);
            if(widget.isVisible() && widget.isActive() && widget.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
        if (id == 1) {
            int widgetIndex = buffer.readVarInt();
            int widgetUpdateId = buffer.readVarInt();
            Widget widget;
            if (widgetIndex < widgets.size()) {
                widget = widgets.get(widgetIndex);
            } else {
                synchronized (waitToAdded) {
                    widget = waitToAdded.get(widgets.size() - widgetIndex);
                }
            }
            widget.readUpdateInfo(widgetUpdateId, buffer);
        } else if (id == 2) { // additional widget init
            int widgetIndex = buffer.readVarInt();
            Widget widget;
            if (widgetIndex < widgets.size()) {
                widget = widgets.get(widgetIndex);
            } else {
                synchronized (waitToAdded) {
                    widget = waitToAdded.get(widgets.size() - widgetIndex);
                }
            }
            if (!widget.isClientSideWidget && widget.isInitialized()) {
                widget.readInitialData(buffer);
            }
        }
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        if (id == 1) {
            int widgetIndex = buffer.readVarInt();
            int widgetUpdateId = buffer.readVarInt();
            Widget widget;
            if (widgetIndex < widgets.size()) {
                widget = widgets.get(widgetIndex);
            } else {
                synchronized (waitToAdded) {
                    widget = waitToAdded.get(widgets.size() - widgetIndex);
                }
            }
            widget.handleClientAction(widgetUpdateId, buffer);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onScreenSizeUpdate(int screenWidth, int screenHeight) {
        super.onScreenSizeUpdate(screenWidth, screenHeight);
        for (Widget widget : widgets) {
            widget.onScreenSizeUpdate(screenWidth, screenHeight);
        }
    }

    private class WidgetGroupUIAccess implements WidgetUIAccess {

        @Override
        public void notifySizeChange() {
            WidgetUIAccess uiAccess = WidgetGroup.this.uiAccess;
            recomputeSize();
            if (uiAccess != null) {
                uiAccess.notifySizeChange();
            }
        }

        @Override
        public boolean attemptMergeStack(ItemStack itemStack, boolean fromContainer, boolean simulate) {
            WidgetUIAccess uiAccess = WidgetGroup.this.uiAccess;
            if (uiAccess != null) {
                return uiAccess.attemptMergeStack(itemStack, fromContainer, simulate);
            }
            return false;
        }

        @Override
        public void notifyWidgetChange() {
            WidgetUIAccess uiAccess = WidgetGroup.this.uiAccess;
            if (uiAccess != null) {
                uiAccess.notifyWidgetChange();
            }
            recomputeSize();
        }

        @Override
        public void writeClientAction(Widget widget, int updateId, Consumer<FriendlyByteBuf> dataWriter) {
            WidgetGroup.this.writeClientAction(1, buffer -> {
                buffer.writeVarInt(widgets.indexOf(widget));
                buffer.writeVarInt(updateId);
                dataWriter.accept(buffer);
            });
        }

        @Override
        public void writeUpdateInfo(Widget widget, int updateId, Consumer<FriendlyByteBuf> dataWriter) {
            WidgetGroup.this.writeUpdateInfo(1, buffer -> {
                buffer.writeVarInt(widgets.indexOf(widget));
                buffer.writeVarInt(updateId);
                dataWriter.accept(buffer);
            });
        }

    }

    // *********** for jei rei ************* //

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Rect2i> getGuiExtraAreas(Rect2i guiRect, List<Rect2i> list) {
        for (Widget widget : widgets) {
            list = widget.getGuiExtraAreas(guiRect, list);
        }
        return super.getGuiExtraAreas(guiRect, list);
    }

    // *********** IConfigurableWidget ************* //

    @Override
    public boolean canWidgetDragIn(IConfigurableWidget widget) {
        if (widget == this) return false;
        var parent = this.getParent();
        while (parent != null) {
            if (parent == widget) return false;
            parent = parent.getParent();
        }
        return true;
    }

    @Override
    public void onWidgetDragIn(IConfigurableWidget widget) {
        addWidget(widget.widget());
    }

    @Override
    public void onWidgetDragOut(IConfigurableWidget widget) {
        removeWidget(widget.widget());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = IConfigurableWidgetGroup.super.serializeNBT();
        var children = new ListTag();
        for (Widget widget : widgets) {
            if (widget instanceof IConfigurableWidget child && child.isRegisterUI()) {
                CompoundTag ui = new CompoundTag();
                ui.putString("type", child.getRegisterUI().name());
                ui.put("data", child.serializeNBT());
                children.add(ui);
            }
        }
        tag.put("children", children);
        return tag;
    }

    public static final Function<String, UIDetector.Wrapper<RegisterUI, IConfigurableWidget>> CACHE = Util.memoize(type -> {
        for (var wrapper : UIDetector.REGISTER_WIDGETS) {
            if (wrapper.annotation().name().equals(type)) {
                return wrapper;
            }
        }
        return null;
    });

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        clearAllWidgets();
        IConfigurableWidgetGroup.super.deserializeNBT(nbt);
        var children = nbt.getList("children", Tag.TAG_COMPOUND);
        for (Tag tag : children) {
            if (tag instanceof CompoundTag ui) {
                String type = ui.getString("type");
                var wrapper = CACHE.apply(type);
                if (wrapper != null) {
                    var child = wrapper.creator().get();
                    addWidget(child.widget());
                    child.deserializeNBT(ui.getCompound("data"));
                }
            }
        }
    }
}
