package com.lowdragmc.lowdraglib.gui.widget;

import com.google.common.base.Preconditions;
import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.gui.modular.WidgetUIAccess;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.util.InputMappings;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Widget is functional element of ModularUI
 * It can draw, perform actions, react to key press and mouse
 * It's information is also synced to client
 */
public class Widget {

    protected ModularUI gui;
    protected WidgetUIAccess uiAccess;
    private Position parentPosition = Position.ORIGIN;
    private Position selfPosition;
    private Position position;
    private Size size;
    private boolean isVisible;
    private boolean isActive;
    private boolean isFocus;
    protected boolean isClientSideWidget;
    protected List<ITextComponent> tooltipTexts;
    protected IGuiTexture backgroundTexture;
    protected IGuiTexture hoverTexture;
    protected WidgetGroup parent;
    private boolean initialized;

    public Widget(Position selfPosition, Size size) {
        Preconditions.checkNotNull(selfPosition, "selfPosition");
        Preconditions.checkNotNull(size, "size");
        this.selfPosition = selfPosition;
        this.size = size;
        this.position = this.parentPosition.add(selfPosition);
        this.isVisible = true;
        this.isActive = true;
    }

    public Widget(int x, int y, int width, int height) {
        this(new Position(x, y), new Size(width, height));
    }

    public Widget setClientSideWidget() {
        isClientSideWidget = true;
        return this;
    }

    public Widget setHoverTooltips(String... tooltipText) {
        tooltipTexts = Arrays.stream(tooltipText).filter(Objects::nonNull).filter(s->!s.isEmpty()).map(TranslationTextComponent::new).collect(Collectors.toList());
        return this;
    }

    public Widget setHoverTooltips(ITextComponent... tooltipText) {
        tooltipTexts = Arrays.stream(tooltipText).filter(Objects::nonNull).collect(Collectors.toList());
        return this;
    }

    public Widget setHoverTooltips(List<ITextComponent> tooltipText) {
        tooltipTexts = tooltipText;
        return this;
    }

    public Widget setHoverTooltip(ITextComponent tooltipText) {
        tooltipTexts = Collections.singletonList(tooltipText);
        return this;
    }

    public Widget setBackground(IGuiTexture... backgroundTexture) {
        this.backgroundTexture = backgroundTexture.length > 1 ? new GuiTextureGroup(backgroundTexture) : backgroundTexture[0];
        return this;
    }

    public Widget setHoverTexture(IGuiTexture... hoverTexture) {
        this.hoverTexture = hoverTexture.length > 1 ? new GuiTextureGroup(hoverTexture) : hoverTexture[0];
        return this;
    }

    public void setGui(ModularUI gui) {
        this.gui = gui;
    }
    
    public ModularUI getGui() {
        return gui;
    }

    public void setUiAccess(WidgetUIAccess uiAccess) {
        this.uiAccess = uiAccess;
    }

    public void setParentPosition(Position parentPosition) {
        Preconditions.checkNotNull(parentPosition, "parentPosition");
        this.parentPosition = parentPosition;
        recomputePosition();
    }

    public void setSelfPosition(Position selfPosition) {
        Preconditions.checkNotNull(selfPosition, "selfPosition");
        this.selfPosition = selfPosition;
        recomputePosition();
    }

    public Position addSelfPosition(int addX, int addY) {
        this.selfPosition = new Position(selfPosition.x + addX, selfPosition.y + addY);
        recomputePosition();
        return this.selfPosition;
    }

    public Position getSelfPosition() {
        return selfPosition;
    }

    public void setSize(Size size) {
        Preconditions.checkNotNull(size, "size");
        this.size = size;
        onSizeUpdate();
    }

    public final Position getPosition() {
        return position;
    }

    public final Size getSize() {
        return size;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @OnlyIn(Dist.CLIENT)
    public Rectangle2d toRectangleBox() {
        Position pos = getPosition();
        Size size = getSize();
        return new Rectangle2d(pos.x, pos.y, size.width, size.height);
    }

    protected void recomputePosition() {
        this.position = this.parentPosition.add(selfPosition);
        onPositionUpdate();
    }

    protected void onPositionUpdate() {
    }

    protected void onSizeUpdate() {
    }

    public boolean isMouseOverElement(double mouseX, double mouseY) {
        Position position = getPosition();
        Size size = getSize();
        return isMouseOver(position.x, position.y, size.width, size.height, mouseX, mouseY);
    }

    public static boolean isMouseOver(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= x && mouseY >= y && x + width > mouseX && y + height > mouseY;
    }

    public Position getParentPosition() {
        return parentPosition;
    }

    public WidgetGroup getParent() {
        return parent;
    }

    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Called on both sides to initialize widget data
     */
    public void initWidget() {
        initialized = true;
    }

    public void writeInitialData(PacketBuffer buffer) {
    }

    public void readInitialData(PacketBuffer buffer) {
        
    }
    
    /**
     * Called on serverside to detect changes and synchronize them with clients
     */
    public void detectAndSendChanges() {
    }

    /**
     * Called clientside every tick with this modular UI open
     */
    @OnlyIn(Dist.CLIENT)
    public void updateScreen() {
        if (backgroundTexture != null) {
            backgroundTexture.updateTick();
        }
        if (hoverTexture != null) {
            hoverTexture.updateTick();
        }
    }

    /**
     * Called each draw tick to draw this widget in GUI
     */
    @OnlyIn(Dist.CLIENT)
    public void drawInForeground(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (tooltipTexts != null && isMouseOverElement(mouseX, mouseY) && tooltipTexts.size() > 0 && gui != null &&  gui.getModularUIGui() != null) {
            gui.getModularUIGui().setHoverTooltip(tooltipTexts);
        }
    }

    /**
     * Called each draw tick to draw this widget in GUI
     */
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (hoverTexture != null && isMouseOverElement(mouseX, mouseY)) {
            Position pos = getPosition();
            Size size = getSize();
            hoverTexture.draw(matrixStack, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
        } else if (backgroundTexture != null) {
            Position pos = getPosition();
            Size size = getSize();
            backgroundTexture.draw(matrixStack, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
        }
    }

    /**
     * Called when mouse wheel is moved in GUI
     * For some -redacted- reason mouseX position is relative against GUI not game window as in other mouse events
     */
    @OnlyIn(Dist.CLIENT)
    public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
        return false;
    }

    /**
     * Called when mouse is clicked in GUI
     */
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * Called when mouse is pressed and hold down in GUI
     */
    @OnlyIn(Dist.CLIENT)
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public void mouseMoved(double mouseX, double mouseY) {
    }

    /**
     * Called when mouse is released in GUI
     */
    @OnlyIn(Dist.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * Called when key is typed in GUI
     */
    @OnlyIn(Dist.CLIENT)
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    /**
     * setFocus should always be called after child widgets logic
     */
    @OnlyIn(Dist.CLIENT)
    public final void setFocus(boolean focus) {
        if (isFocus != focus && gui != null) {
            ModularUIGuiContainer guiContainer = gui.getModularUIGui();
            Widget lastFocus = guiContainer.lastFocus;
            if (!focus) {
                isFocus = false;
                if (guiContainer.lastFocus == this) {
                    guiContainer.lastFocus = null;
                }
                onFocusChanged(lastFocus, guiContainer.lastFocus);
            } else {
                if (guiContainer.switchFocus(this)) {
                    isFocus = true;
                    onFocusChanged(lastFocus, guiContainer.lastFocus);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public final boolean isFocus() {
        return isFocus;
    }
    
    @OnlyIn(Dist.CLIENT)
    public void onFocusChanged(@Nullable Widget lastFocus, Widget focus) {
        
    }

    /**
     * Read data received from server's {@link #writeUpdateInfo}
     */
    @OnlyIn(Dist.CLIENT)
    public void readUpdateInfo(int id, PacketBuffer buffer) {
    }

    public void handleClientAction(int id, PacketBuffer buffer) {
    }

    public List<SlotWidget> getNativeWidgets() {
        if (this instanceof SlotWidget) {
            return Collections.singletonList((SlotWidget) this);
        }
        return Collections.emptyList();
    }

    /**
     * Writes data to be sent to client's {@link #readUpdateInfo}
     */
    protected final void writeUpdateInfo(int id, Consumer<PacketBuffer> packetBufferWriter) {
        if (uiAccess != null && gui != null) {
            uiAccess.writeUpdateInfo(this, id, packetBufferWriter);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected final void writeClientAction(int id, Consumer<PacketBuffer> packetBufferWriter) {
        if (uiAccess != null && !isClientSideWidget) {
            uiAccess.writeClientAction(this, id, packetBufferWriter);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected static void playButtonClickSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @OnlyIn(Dist.CLIENT)
    protected static boolean isShiftDown() {
        long id = Minecraft.getInstance().getWindow().getWindow();
        return InputMappings.isKeyDown(id, GLFW.GLFW_KEY_LEFT_SHIFT) || InputMappings.isKeyDown(id, GLFW.GLFW_KEY_LEFT_SHIFT);
    }

    @OnlyIn(Dist.CLIENT)
    protected static boolean isCtrlDown() {
        long id = Minecraft.getInstance().getWindow().getWindow();
        return InputMappings.isKeyDown(id, GLFW.GLFW_KEY_LEFT_CONTROL) || InputMappings.isKeyDown(id, GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public boolean isRemote() {
        return (gui != null && gui.holder != null) ? gui.holder.isRemote() : LDLMod.isRemote();
    }

    protected void setParent(WidgetGroup parent) {
        this.parent = parent;
    }

    public boolean isParent(WidgetGroup widgetGroup) {
        if (parent == null) return false;
        if (parent == widgetGroup) return true;
        return parent.isParent(widgetGroup);
    }
}
