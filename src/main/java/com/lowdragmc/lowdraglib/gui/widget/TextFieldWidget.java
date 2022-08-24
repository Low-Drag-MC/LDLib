package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TextFieldWidget extends Widget {

    @OnlyIn(Dist.CLIENT)
    protected net.minecraft.client.gui.widget.TextFieldWidget textField;

    protected int maxStringLength = Integer.MAX_VALUE;
    protected Function<String, String> textValidator = (s)->s;
    protected Supplier<String> textSupplier;
    protected Consumer<String> textResponder;
    protected String currentString;
    protected boolean isBordered;
    protected int textColor = -1;
    protected float wheelDur;
    protected NumberFormat numberInstance;
    protected ITextComponent hover;

    public TextFieldWidget(int xPosition, int yPosition, int width, int height, Supplier<String> textSupplier, Consumer<String> textResponder) {
        super(new Position(xPosition, yPosition), new Size(width, height));
        if (isRemote()) {
            FontRenderer fontRenderer = Minecraft.getInstance().font;
            this.textField = new net.minecraft.client.gui.widget.TextFieldWidget(fontRenderer, xPosition, yPosition, width, height, new StringTextComponent("text field"));
            this.textField.setBordered(true);
            isBordered = true;
            this.textField.setMaxLength(this.maxStringLength);
            this.textField.setResponder(this::onTextChanged);
        }
        this.textSupplier = textSupplier;
        this.textResponder = textResponder;
    }

    public TextFieldWidget setTextSupplier(Supplier<String> textSupplier) {
        this.textSupplier = textSupplier;
        return this;
    }

    public TextFieldWidget setTextResponder(Consumer<String> textResponder) {
        this.textResponder = textResponder;
        return this;
    }

    public TextFieldWidget setBackground(IGuiTexture background) {
        super.setBackground(background);
        return this;
    }

    public TextFieldWidget setCurrentString(String currentString) {
        this.currentString = currentString;
        if (isRemote()) {
            if (!this.textField.getValue().equals(currentString)) {
                this.textField.setValue(currentString);
            }
        }
        return this;
    }

    public String getCurrentString() {
        if (isRemote()) {
            return this.textField.getValue();
        }
        return this.currentString;
    }

    @Override
    public void onFocusChanged(@Nullable Widget lastFocus, Widget focus) {
        if (!isFocus()) {
            this.textField.setFocus(false);
        }
    }

    @Override
    protected void onPositionUpdate() {
        if (isRemote() && textField != null) {
            Position position = getPosition();
            Size size = getSize();
            this.textField.x = isBordered ? position.x : position.x + 1;
            this.textField.y = isBordered ? position.y : position.y + (size.height - Minecraft.getInstance().font.lineHeight) / 2 + 1;
        }
    }

    @Override
    protected void onSizeUpdate() {
        if (isRemote() && textField != null) {
            Position position = getPosition();
            Size size = getSize();
            this.textField.setWidth(isBordered ? size.width : size.width - 2);
            this.textField.setHeight( size.height);
            this.textField.y = isBordered ? position.y : position.y + (getSize().height -  Minecraft.getInstance().font.lineHeight) / 2 + 1;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(matrixStack, mouseX, mouseY, partialTicks);
        this.textField.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.enableBlend();
        RenderSystem.color4f(1,1,1,1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        setFocus(isMouseOverElement(mouseX, mouseY));
        return this.textField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.textField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return this.textField.charTyped(codePoint, modifiers);
    }

    @Override
    public void updateScreen() {
        if (this.isVisible() && this.isActive() && textSupplier != null && isClientSideWidget&& !textSupplier.get().equals(getCurrentString())) {
            setCurrentString(textSupplier.get());
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (textSupplier != null && !textSupplier.get().equals(getCurrentString())) {
            setCurrentString(textSupplier.get());
            writeUpdateInfo(1, buffer -> buffer.writeUtf(getCurrentString()));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void readUpdateInfo(int id, PacketBuffer buffer) {
        super.readUpdateInfo(id, buffer);
        if (id == 1) {
            setCurrentString(buffer.readUtf());
        }
    }

    protected void onTextChanged(String newTextString) {
        String lastText = currentString;
        String newText = textValidator.apply(newTextString);
        if (!newText.equals(lastText)) {
            this.textField.setTextColor(textColor);
            setCurrentString(newText);
            if (isClientSideWidget && textResponder != null) {
                textResponder.accept(newText);
            }
            writeClientAction(1, buffer -> buffer.writeUtf(newText));
        } else if (!newTextString.equals(newText)){
            this.textField.setTextColor(0xffdf0000);
        } else {
            this.textField.setTextColor(textColor);
        }
    }

    @Override
    public void handleClientAction(int id, PacketBuffer buffer) {
        super.handleClientAction(id, buffer);
        if (id == 1) {
            String lastText = getCurrentString();
            String newText = textValidator.apply(buffer.readUtf());
            newText = newText.substring(0, Math.min(newText.length(), maxStringLength));
            if (!lastText.equals(newText)) {
                setCurrentString(newText);
                if (textResponder != null) {
                    this.textResponder.accept(newText);
                }
            }
        }
    }

    public TextFieldWidget setBordered(boolean bordered) {
        isBordered = bordered;
        if (isRemote()) {
            this.textField.setBordered(bordered);
        }
        return this;
    }

    public TextFieldWidget setTextColor(int textColor) {
        this.textColor = textColor;
        if (isRemote()) {
            this.textField.setTextColor(textColor);
        }
        return this;
    }

    public TextFieldWidget setMaxStringLength(int maxStringLength) {
        this.maxStringLength = maxStringLength;
        if (isRemote()) {
            this.textField.setMaxLength(maxStringLength);
        }
        return this;
    }

    public TextFieldWidget setValidator(Function<String, String> validator) {
        this.textValidator = validator;
        return this;
    }

    public TextFieldWidget setResourceLocationOnly() {
        setValidator(s -> {
            try {
                s = s.toLowerCase();
                s = s.replace(' ', '_');
                if (ResourceLocation.isValidResourceLocation(s)) {
                    return s;
                }
            } catch (NumberFormatException ignored) { }
            return this.currentString;
        });
        hover = new TranslationTextComponent("ldlib.gui.text_field.resourcelocation");
        return this;
    }

    public TextFieldWidget setNumbersOnly(long minValue, long maxValue) {
        setValidator(s -> {
            try {
                if (s == null || s.isEmpty()) return minValue + "";
                long value = Long.parseLong(s);
                if (minValue <= value && value <= maxValue) return s;
                if (value < minValue) return minValue + "";
                return maxValue + "";
            } catch (NumberFormatException ignored) { }
            return this.currentString;
        });
        if (minValue == Long.MIN_VALUE && maxValue == Long.MAX_VALUE) {
            hover = new TranslationTextComponent("ldlib.gui.text_field.number.3");
        } else if (minValue == Long.MIN_VALUE) {
            hover = new TranslationTextComponent("ldlib.gui.text_field.number.2", maxValue);
        } else if (maxValue == Long.MAX_VALUE) {
            hover = new TranslationTextComponent("ldlib.gui.text_field.number.1", minValue);
        } else {
            hover = new TranslationTextComponent("ldlib.gui.text_field.number.0", minValue, maxValue);
        }
        return setWheelDur(1);
    }

    public TextFieldWidget setNumbersOnly(int minValue, int maxValue) {
        setValidator(s -> {
            try {
                if (s == null || s.isEmpty()) return minValue + "";
                int value = Integer.parseInt(s);
                if (minValue <= value && value <= maxValue) return s;
                if (value < minValue) return minValue + "";
                return maxValue + "";
            } catch (NumberFormatException ignored) { }
            return this.currentString;
        });
        if (minValue == Integer.MIN_VALUE && maxValue == Integer.MAX_VALUE) {
            hover = new TranslationTextComponent("ldlib.gui.text_field.number.3");
        } else if (minValue == Integer.MIN_VALUE) {
            hover = new TranslationTextComponent("ldlib.gui.text_field.number.2", maxValue);
        } else if (maxValue == Integer.MAX_VALUE) {
            hover = new TranslationTextComponent("ldlib.gui.text_field.number.1", minValue);
        } else {
            hover = new TranslationTextComponent("ldlib.gui.text_field.number.0", minValue, maxValue);
        }
        return setWheelDur(1);
    }

    public TextFieldWidget setNumbersOnly(float minValue, float maxValue) {
        setValidator(s -> {
            try {
                if (s == null || s.isEmpty()) return minValue + "";
                float value = Float.parseFloat(s);
                if (minValue <= value && value <= maxValue) return s;
                if (value < minValue) return minValue + "";
                return maxValue + "";
            } catch (NumberFormatException ignored) { }
            return this.currentString;
        });
        if (minValue == Float.MIN_VALUE && maxValue == Float.MAX_VALUE) {
            hover = new TranslationTextComponent("ldlib.gui.text_field.number.3");
        } else if (minValue == Float.MIN_VALUE) {
            hover = new TranslationTextComponent("ldlib.gui.text_field.number.2", maxValue);
        } else if (maxValue == Float.MAX_VALUE) {
            hover = new TranslationTextComponent("ldlib.gui.text_field.number.1", minValue);
        } else {
            hover = new TranslationTextComponent("ldlib.gui.text_field.number.0", minValue, maxValue);
        }
        return setWheelDur(0.1f);
    }

    public TextFieldWidget setWheelDur(float wheelDur) {
        this.wheelDur = wheelDur;
        this.numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(4);
        return this;
    }

    public TextFieldWidget setWheelDur(int digits, float wheelDur) {
        this.wheelDur = wheelDur;
        this.numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(digits);
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
        if (wheelDur > 0 && numberInstance != null && isMouseOverElement(mouseX, mouseY) && isFocus()) {
            try {
                onTextChanged(numberInstance.format(Float.parseFloat(getCurrentString()) + (wheelDelta > 0 ? 1 : -1) * wheelDur));
            } catch (Exception ignored) {
            }
            return true;
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInForeground(@Nonnull MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (isMouseOverElement(mouseX, mouseY) && gui != null &&  gui.getModularUIGui() != null) {
            List<ITextComponent> tips = new ArrayList<>();
            if (tooltipTexts != null) {
                tips.addAll(tooltipTexts);
            }
            if (hover != null) {
                tips.add(hover);
            }
            if (wheelDur > 0 && numberInstance != null && isFocus()) {
                tips.add(new TranslationTextComponent("ldlib.gui.text_field.number.wheel", numberInstance.format(wheelDur)));
            }
            if (!tips.isEmpty()) {
                gui.getModularUIGui().setHoverTooltip(tips);
            }
        }
    }

}
