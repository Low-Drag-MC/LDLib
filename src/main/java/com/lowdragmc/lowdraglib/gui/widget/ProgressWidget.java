package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

public class ProgressWidget extends Widget {
    public final static DoubleSupplier JEIProgress = () -> Math.abs(System.currentTimeMillis() % 2000) / 2000.;

    public final DoubleSupplier progressSupplier;
    private ProgressTexture progressBar;
    private Function<Double, String> dynamicHoverTips;

    private double lastProgressValue;

    public ProgressWidget(DoubleSupplier progressSupplier, int x, int y, int width, int height, ProgressTexture progressBar) {
        super(new Position(x, y), new Size(width, height));
        this.progressSupplier = progressSupplier;
        this.progressBar = progressBar;
        this.lastProgressValue = -1;
    }

    public ProgressWidget(DoubleSupplier progressSupplier, int x, int y, int width, int height) {
        super(new Position(x, y), new Size(width, height));
        this.progressSupplier = progressSupplier;
    }

    public ProgressWidget setProgressBar(IGuiTexture emptyBarArea, IGuiTexture filledBarArea) {
        this.progressBar = new ProgressTexture(emptyBarArea, filledBarArea);
        return this;
    }

    public ProgressWidget setProgressBar(ProgressTexture progressBar) {
        this.progressBar = progressBar;
        return this;
    }

    public ProgressWidget setDynamicHoverTips(Function<Double, String> hoverTips) {
        this.dynamicHoverTips = hoverTips;
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Position pos = getPosition();
        Size size = getSize();
        if (progressSupplier == JEIProgress || isClientSideWidget) {
            lastProgressValue = progressSupplier.getAsDouble();
            if (dynamicHoverTips != null) {
                setHoverTooltips(dynamicHoverTips.apply(lastProgressValue));
            }
        }
        progressBar.setProgress(lastProgressValue);
        progressBar.draw(matrixStack, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        this.lastProgressValue = progressSupplier.getAsDouble();
    }

    @Override
    public void writeInitialData(FriendlyByteBuf buffer) {
        super.writeInitialData(buffer);
        buffer.writeDouble(lastProgressValue);
    }

    @Override
    public void readInitialData(FriendlyByteBuf buffer) {
        super.readInitialData(buffer);
        lastProgressValue = buffer.readDouble();
    }

    @Override
    public void detectAndSendChanges() {
        double actualValue = progressSupplier.getAsDouble();
        if (actualValue - lastProgressValue != 0) {
            this.lastProgressValue = actualValue;
            writeUpdateInfo(0, buffer -> buffer.writeDouble(actualValue));
        }
    }

    @Override
    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
        if (id == 0) {
            this.lastProgressValue = buffer.readDouble();
            if (dynamicHoverTips != null) {
                setHoverTooltips(dynamicHoverTips.apply(lastProgressValue));
            }
        }
    }

}
