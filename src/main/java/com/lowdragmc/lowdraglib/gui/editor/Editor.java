package com.lowdragmc.lowdraglib.gui.editor;

import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Size;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author KilaBash
 * @date 2022/11/30
 * @implNote MainPage
 */
public class Editor extends WidgetGroup {
    @Getter
    protected WidgetGroup mainPanel;

    @Getter
    protected ConfigPanel configPanel;
    @Getter
    protected ResourcePanel resourcePanel;

    public Editor() {
        super(0, 0, 10, 10);
        setClientSideWidget();
    }

    @Override
    public void initWidget() {
        super.initWidget();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onScreenSizeUpdate(int screenWidth, int screenHeight) {
        setSize(new Size(screenWidth, screenHeight));
        super.onScreenSizeUpdate(screenWidth, screenHeight);
        this.clearAllWidgets();

        addWidget(mainPanel = new WidgetGroup(0, 0, getSize().width, getSize().height));
        addWidget(configPanel = new ConfigPanel(this));
        addWidget(resourcePanel = new ResourcePanel(this));

        mainPanel.addWidget(new FrameWidget(this, new ImageWidget(0, 0, 200, 200, ResourceBorderTexture.BORDERED_BACKGROUND)));
    }
}
