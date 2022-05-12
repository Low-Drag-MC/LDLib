package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SelectorWidget extends WidgetGroup {
    protected ButtonWidget button;
    protected List<String> candidates;
    protected List<SelectableWidgetGroup> selects;
    protected String currentString;
    protected boolean isShow;
    private Consumer<String> onChanged;
    public final TextTexture textTexture;
    public final DraggableScrollableWidgetGroup popUp;

    public SelectorWidget(int x, int y, int width, int height, List<String> candidates, int fontColor) {
        super(new Position(x, y), new Size(width, height));
        this.button = new ButtonWidget(0,0, width, height, d -> {
            if (d.isRemote) setShow(!isShow);
        });
        this.candidates = candidates;
        this.selects = new ArrayList<>();
        this.addWidget(button);
        this.addWidget(new ImageWidget(0,0,width, height, textTexture = new TextTexture("", fontColor).setWidth(width).setType(TextTexture.TextType.ROLL)));
        this.addWidget(popUp = new DraggableScrollableWidgetGroup(0, height, width, Math.min(5, candidates.size()) * 15));
        popUp.setBackground(new ColorRectTexture(0xAA000000));
        if (candidates.size() > 5) {
            popUp.setYScrollBarWidth(4).setYBarStyle(null, new ColorRectTexture(-1));
        }
        popUp.setVisible(false);
        popUp.setActive(false);
        currentString = "";
        y = 0;
        width = candidates.size() > 5 ? width -4 : width;
        for (String candidate : candidates) {
            SelectableWidgetGroup select = new SelectableWidgetGroup(0, y, width, 15);
            select.addWidget(new ImageWidget(0, 0, width, 15, new TextTexture(candidate, fontColor).setWidth(width).setType(TextTexture.TextType.ROLL)));
            select.setSelectedTexture(-1, -1);
            select.setOnSelected(s -> {
                setValue(candidate);
                if (onChanged != null) {
                    onChanged.accept(candidate);
                }
                setValue(candidate);
                writeClientAction(2, buffer -> buffer.writeUtf(candidate));
                setShow(false);
            });
            popUp.addWidget(select);
            selects.add(select);
            y += 15;
        }

    }

    public SelectorWidget setIsUp(boolean isUp) {
        popUp.setSelfPosition(isUp ? new Position(0, - Math.min(candidates.size(), 5) * 15): new Position(0, getSize().height));
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    public void setShow(boolean isShow) {
        if (isShow) {
            setFocus(true);
        }
        this.isShow = isShow;
        popUp.setVisible(isShow);
        popUp.setActive(isShow);
    }

    public SelectorWidget setValue(String value) {
        int index = candidates.indexOf(value);
        if (index >= 0 && !value.equals(currentString)) {
            currentString = value;
            textTexture.updateText(value);
            for (int i = 0; i < selects.size(); i++) {
                selects.get(i).isSelected = index == i;
            }
        }
        return this;
    }

    public String getValue() {
        return currentString;
    }

    public SelectorWidget setOnChanged(Consumer<String> onChanged) {
        this.onChanged = onChanged;
        return this;
    }

    public SelectorWidget setButtonBackground(IGuiTexture... guiTexture) {
        button.setButtonTexture(guiTexture);
        return this;
    }

    public SelectorWidget setBackground(IGuiTexture background) {
        popUp.setBackground(background);
        return this;
    }

    @Override
    public void onFocusChanged() {
        if (!isFocus()) {
            setShow(false);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInForeground(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        boolean lastVisible = popUp.isVisible();
        popUp.setVisible(false);
        super.drawInForeground(matrixStack, mouseX, mouseY, partialTicks);
        popUp.setVisible(lastVisible);

        if(isShow) {
            matrixStack.translate(0, 0, 200);
            popUp.drawInBackground(matrixStack, mouseX, mouseY, partialTicks);
            popUp.drawInForeground(matrixStack, mouseX, mouseY, partialTicks);
            matrixStack.translate(0, 0, -200);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        boolean lastVisible = popUp.isVisible();
        popUp.setVisible(false);
        super.drawInBackground(matrixStack, mouseX, mouseY, partialTicks);
        popUp.setVisible(lastVisible);
    }


    @Override
    public void handleClientAction(int id, PacketBuffer buffer) {
        super.handleClientAction(id, buffer);
        if (id == 2) {
            setValue(buffer.readUtf());
            if (onChanged != null) {
               onChanged.accept(getValue()); 
            }
        }
    }

}