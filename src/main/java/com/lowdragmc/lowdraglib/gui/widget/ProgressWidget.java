package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.GuiTextureConfigurator;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.editor.runtime.PersistedParser;
import com.lowdragmc.lowdraglib.gui.editor.runtime.UIDetector;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.syncdata.accessor.IGuiTextureAccessor;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

@RegisterUI(name = "progress", group = "basic")
public class ProgressWidget extends Widget implements IConfigurableWidget {
    public final static DoubleSupplier JEIProgress = () -> Math.abs(System.currentTimeMillis() % 2000) / 2000.;

    public DoubleSupplier progressSupplier;
    private ProgressTexture progressBar;
    private Function<Double, String> dynamicHoverTips;

    private double lastProgressValue;

    public ProgressWidget() {
        this(JEIProgress, 0, 0, 40, 40, new ProgressTexture());
    }

    public ProgressWidget(DoubleSupplier progressSupplier, int x, int y, int width, int height, ResourceTexture fullImage) {
        super(new Position(x, y), new Size(width, height));
        this.progressSupplier = progressSupplier;
        this.progressBar = new ProgressTexture(fullImage.getSubTexture(0.0, 0.0, 1.0, 0.5), fullImage.getSubTexture(0.0, 0.5, 1.0, 0.5));
        this.lastProgressValue = -1;
    }

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

    public ProgressWidget setFillDirection(ProgressTexture.FillDirection fillDirection) {
        this.progressBar.setFillDirection(fillDirection);
        return this;
    }

    public ProgressWidget setProgressSupplier(DoubleSupplier progressSupplier) {
        this.progressSupplier = progressSupplier;
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInForeground(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if ((tooltipTexts.size() > 0 || dynamicHoverTips != null) && isMouseOverElement(mouseX, mouseY) && getHoverElement(mouseX, mouseY) == this && gui != null && gui.getModularUIGui() != null) {
            var tips = new ArrayList<>(tooltipTexts);
            if (dynamicHoverTips != null) {
                tips.add(new TranslatableComponent(dynamicHoverTips.apply(lastProgressValue)));
            }
            gui.getModularUIGui().setHoverTooltip(tips, ItemStack.EMPTY, null, null);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Position pos = getPosition();
        Size size = getSize();
        if (progressSupplier == JEIProgress || isClientSideWidget) {
            lastProgressValue = progressSupplier.getAsDouble();
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
        }
    }

    @Override
    public void buildConfigurator(ConfiguratorGroup father) {
        IConfigurableWidget.super.buildConfigurator(father);
        var configurator = new GuiTextureConfigurator("progress bar", () -> progressBar, t -> {
            if (t instanceof ProgressTexture texture) {
                setProgressBar(texture);
            }
        }, true);
        configurator.setAvailable(t -> t instanceof ProgressTexture);
        configurator.setTips("ldlib.gui.editor.tips.progress_texture");
        father.addConfigurators(configurator);
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = IConfigurableWidget.super.serializeNBT();
        if (progressBar != null) {
            CompoundTag data = new CompoundTag();
            PersistedParser.serializeNBT(data, progressBar.getClass(), progressBar);
            tag.put("progressBar", data);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        IConfigurableWidget.super.deserializeNBT(nbt);
        if (nbt.contains("progressBar")) {
            PersistedParser.deserializeNBT(nbt.getCompound("progressBar"), new HashMap<>(), ProgressTexture.class, progressBar = new ProgressTexture());
        }
    }

    @Override
    public boolean canDragIn(Object dragging) {
        if (dragging instanceof IGuiTexture) {
            return dragging instanceof ProgressTexture;
        }
        return IConfigurableWidget.super.canDragIn(dragging);
    }

    @Override
    public boolean handleDragging(Object dragging) {
        if (dragging instanceof ProgressTexture progressTexture) {
            setProgressBar(progressTexture);
            return true;
        } else return IConfigurableWidget.super.handleDragging(dragging);
    }
}
