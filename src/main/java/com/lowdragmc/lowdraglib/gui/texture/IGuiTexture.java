package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurable;
import com.lowdragmc.lowdraglib.gui.editor.configurator.WrapperConfigurator;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IGuiTexture extends IConfigurable {

    default IGuiTexture setColor(int color){
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    void draw(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height);

    @OnlyIn(Dist.CLIENT)
    default void updateTick() { }
    
    IGuiTexture EMPTY = new IGuiTexture() {
        @OnlyIn(Dist.CLIENT)
        @Override
        public void draw(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {

        }
    };

    @OnlyIn(Dist.CLIENT)
    default void drawSubArea(PoseStack stack, float x, float y, int width, int height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        draw(stack, 0, 0, x, y, width, height);
    }

    // ***************** EDITOR  ***************** //

    default void createPreview(ConfiguratorGroup father) {
        father.addConfigurators(new WrapperConfigurator("ldlib.gui.editor.group.preview",
                new ImageWidget(0, 0, 100, 100, this)
                        .setBorder(2, ColorPattern.T_WHITE.color)));
    }

    @Override
    default void buildConfigurator(ConfiguratorGroup father) {
        createPreview(father);
        IConfigurable.super.buildConfigurator(father);
    }
}
