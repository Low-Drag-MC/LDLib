package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.utils.ISearch;
import com.lowdragmc.lowdraglib.utils.SearchEngine;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author KilaBash
 * @date 2022/8/24
 * @implNote SearchComponentWidget
 */
public class SearchComponentWidget<T> extends WidgetGroup {
    public final SearchEngine<T> engine;
    public final IWidgetSearch<T> search;
    public final DraggableScrollableWidgetGroup popUp;
    public final TextFieldWidget textFieldWidget;
    private int capacity = 10;
    protected boolean isShow;

    public SearchComponentWidget(int x, int y, int width, int height, IWidgetSearch<T> search) {
        super(x, y, width, height);
        setClientSideWidget();
        this.addWidget(textFieldWidget = new TextFieldWidget(0, 0, width, height, null, null){
            @Override
            public void onFocusChanged(@Nullable Widget lastFocus, Widget focus) {
                if (lastFocus != null && focus != null && lastFocus.parent == focus.parent) {
                    return;
                }
                super.onFocusChanged(lastFocus, focus);
                setShow(isFocus());
            }
        });
        this.addWidget(popUp = new DraggableScrollableWidgetGroup(0, height, width, 0) {
            @Override
            public void onFocusChanged(@Nullable Widget lastFocus, Widget focus) {
                if (lastFocus != null && focus != null && lastFocus.parent == focus.parent) {
                    return;
                }
                super.onFocusChanged(lastFocus, focus);
                setShow(isFocus());
            }
        });
        popUp.setBackground(new ColorRectTexture(0xAA000000));
        popUp.setVisible(false);
        popUp.setActive(false);
        this.search = search;
        this.engine = new SearchEngine<>(search, (r) -> {
            int size = popUp.getAllWidgetSize();
            popUp.setSize(new Size(getSize().width, Math.min(size + 1, capacity) * 15));
            popUp.waitToAdded(new ButtonWidget(0, size * 15, width,
                    15, new TextTexture(search.resultDisplay(r)).setWidth(width).setType(TextTexture.TextType.ROLL),
                    cd -> {
                        search.selectResult(r);
                        textFieldWidget.setCurrentString(search.resultDisplay(r));
                    }).setHoverBorderTexture(-1, -1));
        });

        textFieldWidget.setTextResponder(s -> {
            popUp.clearAllWidgets();
            popUp.setSize(new Size(getSize().width, 0));
            this.engine.searchWord(s);
        });
    }

    public SearchComponentWidget<T> setCapacity(int capacity) {
        this.capacity = capacity;
        popUp.setSize(new Size(getSize().width, Math.min(popUp.getAllWidgetSize(), capacity) * 15));
        return this;
    }

    public SearchComponentWidget<T> setCurrentString(String currentString) {
        textFieldWidget.setCurrentString(currentString);
        return this;
    }

    public String getCurrentString() {
        return textFieldWidget.getCurrentString();
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
        popUp.setVisible(isShow);
        popUp.setActive(isShow);
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

    public interface IWidgetSearch<T> extends ISearch<T> {
        String resultDisplay(T value);

        void selectResult(T value);
    }
}
