package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.ingredient.IIngredientSlot;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.util.TextFormattingUtil;
import com.lowdragmc.lowdraglib.utils.FluidUtils;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class TankWidget extends Widget implements IIngredientSlot {

    public final IFluidTank fluidTank;

    protected boolean showAmount;
    protected boolean allowClickFilling;
    protected boolean allowClickEmptying;
    protected boolean drawHoverTips;
    protected IGuiTexture overlay;

    protected FluidStack lastFluidInTank;
    protected int lastTankCapacity;
    protected BiConsumer<TankWidget, List<ITextComponent>> onAddedTooltips;

    public TankWidget(IFluidTank fluidTank, int x, int y, boolean allowClickContainerFilling, boolean allowClickContainerEmptying) {
        super(new Position(x, y), new Size(18, 18));
        this.fluidTank = fluidTank;
        this.showAmount = true;
        this.allowClickFilling = allowClickContainerFilling;
        this.allowClickEmptying = allowClickContainerEmptying;
        this.drawHoverTips = true;
    }

    public TankWidget setOnAddedTooltips(BiConsumer<TankWidget, List<ITextComponent>> onAddedTooltips) {
        this.onAddedTooltips = onAddedTooltips;
        return this;
    }

    public TankWidget setDrawHoverTips(boolean drawHoverTips) {
        this.drawHoverTips = drawHoverTips;
        return this;
    }

    @Override
    public TankWidget setClientSideWidget() {
        super.setClientSideWidget();
        if (fluidTank != null) {
            fluidTank.getFluid();
            this.lastFluidInTank = fluidTank.getFluid().copy();
        } else {
            this.lastFluidInTank = null;
        }
        this.lastTankCapacity = fluidTank != null ? fluidTank.getCapacity() : 0;
        return this;
    }

    public TankWidget setShowAmount(boolean showAmount) {
        this.showAmount = showAmount;
        return this;
    }

    public TankWidget setBackground(IGuiTexture background) {
        super.setBackground(background);
        return this;
    }

    public TankWidget setOverlay(IGuiTexture overlay) {
        this.overlay = overlay;
        return this;
    }

    public TankWidget setContainerClicking(boolean allowClickContainerFilling, boolean allowClickContainerEmptying) {
        this.allowClickFilling = allowClickContainerFilling;
        this.allowClickEmptying = allowClickContainerEmptying;
        return this;
    }

    @Override
    public Object getIngredientOverMouse(double mouseX, double mouseY) {
        if (isMouseOverElement(mouseX, mouseY) && !lastFluidInTank.isEmpty()) {
            return lastFluidInTank;
        }
        return null;
    }

    private List<ITextComponent> getToolTips(List<ITextComponent> list) {
        if (this.onAddedTooltips != null) {
            this.onAddedTooltips.accept(this, list);
        }
        return list;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(matrixStack, mouseX, mouseY, partialTicks);
        if (isClientSideWidget) {
            FluidStack fluidStack = fluidTank.getFluid();
            if (fluidTank.getCapacity() != lastTankCapacity) {
                this.lastTankCapacity = fluidTank.getCapacity();
            }
            if (!fluidStack.isFluidEqual(lastFluidInTank)) {
                this.lastFluidInTank = fluidStack.copy();
            } else if (fluidStack.getAmount() != lastFluidInTank.getAmount()) {
                this.lastFluidInTank.setAmount(fluidStack.getAmount());
            }
        }
        Position pos = getPosition();
        Size size = getSize();
        if (lastFluidInTank != null) {
            RenderSystem.disableBlend();
            if (!lastFluidInTank.isEmpty()) {
                DrawerHelper.drawFluidForGui(matrixStack, lastFluidInTank, lastFluidInTank.getAmount(), pos.x + 1, pos.y + 1, size.width - 2, size.height - 2);
            }

            if (showAmount && !lastFluidInTank.isEmpty()) {
                matrixStack.pushPose();
                matrixStack.scale(0.5F, 0.5F, 1);
                String s = TextFormattingUtil.formatLongToCompactStringBuckets(lastFluidInTank.getAmount(), 3) + "B";
                FontRenderer fontRenderer = Minecraft.getInstance().font;
                fontRenderer.drawShadow(matrixStack, s, (pos.x + (size.width / 3f)) * 2 - fontRenderer.width(s) + 21, (pos.y + (size.height / 3f) + 6) * 2, 0xFFFFFF);
                matrixStack.popPose();
            }

            RenderSystem.enableBlend();
            RenderSystem.color4f(1, 1, 1, 1);
        }
        if (overlay != null) {
            overlay.draw(matrixStack, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInForeground(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (drawHoverTips && isMouseOverElement(mouseX, mouseY)) {
            List<ITextComponent> tooltips = new ArrayList<>();
            if (lastFluidInTank != null && !lastFluidInTank.isEmpty()) {
                FluidAttributes fluid = lastFluidInTank.getFluid().getAttributes();
                tooltips.add(fluid.getDisplayName(lastFluidInTank));
                tooltips.add(new TranslationTextComponent("ldlib.fluid.amount", lastFluidInTank.getAmount(), lastTankCapacity));
                tooltips.add(new TranslationTextComponent("ldlib.fluid.temperature", fluid.getTemperature(lastFluidInTank)));
                tooltips.add(new TranslationTextComponent(fluid.isGaseous(lastFluidInTank) ? "ldlib.fluid.state_gas" : "ldlib.fluid.state_liquid"));
            } else {
                tooltips.add(new TranslationTextComponent("ldlib.fluid.empty"));
                tooltips.add(new TranslationTextComponent("ldlib.fluid.amount", 0, lastTankCapacity));
            }
            if (gui != null) {
                setHoverTooltips(getToolTips(tooltips));
                super.drawInForeground(matrixStack, mouseX, mouseY, partialTicks);
            }
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1f);
        }
    }

    @Override
    public void detectAndSendChanges() {
        FluidStack fluidStack = fluidTank.getFluid();
        if (fluidTank.getCapacity() != lastTankCapacity) {
            this.lastTankCapacity = fluidTank.getCapacity();
            writeUpdateInfo(0, buffer -> buffer.writeVarInt(lastTankCapacity));
        }
        if (!fluidStack.isFluidEqual(lastFluidInTank)) {
            this.lastFluidInTank = fluidStack.copy();
            CompoundNBT fluidStackTag = fluidStack.writeToNBT(new CompoundNBT());
            writeUpdateInfo(2, buffer -> buffer.writeNbt(fluidStackTag));
        } else if (fluidStack.getAmount() != lastFluidInTank.getAmount()) {
            this.lastFluidInTank.setAmount(fluidStack.getAmount());
            writeUpdateInfo(3, buffer -> buffer.writeVarInt(lastFluidInTank.getAmount()));
        }
    }

    @Override
    public void writeInitialData(PacketBuffer buffer) {
        this.lastTankCapacity = fluidTank.getCapacity();
        buffer.writeVarInt(lastTankCapacity);
        FluidStack fluidStack = fluidTank.getFluid();
        this.lastFluidInTank = fluidStack.copy();
        buffer.writeNbt(fluidStack.writeToNBT(new CompoundNBT()));
    }

    @Override
    public void readInitialData(PacketBuffer buffer) {
        this.lastTankCapacity = buffer.readVarInt();
        readUpdateInfo(2, buffer);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void readUpdateInfo(int id, PacketBuffer buffer) {
        if (id == 0) {
            this.lastTankCapacity = buffer.readVarInt();
        } else if (id == 1) {
            this.lastFluidInTank = null;
        } else if (id == 2) {
            this.lastFluidInTank = FluidStack.loadFluidStackFromNBT(buffer.readNbt());
        } else if (id == 3 && lastFluidInTank != null) {
            this.lastFluidInTank.setAmount(buffer.readVarInt());
        } else if (id == 4) {
            ItemStack currentStack = gui.entityPlayer.inventory.getCarried();
            int newStackSize = buffer.readVarInt();
            currentStack.setCount(newStackSize);
            gui.entityPlayer.inventory.setCarried(currentStack);
        }
    }

    @Override
    public void handleClientAction(int id, PacketBuffer buffer) {
        super.handleClientAction(id, buffer);
        if (id == 1) {
            boolean isShiftKeyDown = buffer.readBoolean();
            int clickResult = tryClickContainer(isShiftKeyDown);
            if (clickResult >= 0) {
                writeUpdateInfo(4, buf -> buf.writeVarInt(clickResult));
            }
        }
    }

    private int tryClickContainer(boolean isShiftKeyDown) {
        PlayerEntity player = gui.entityPlayer;
        ItemStack currentStack = player.inventory.getCarried();
        if (!currentStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) return -1;
        int maxAttempts = isShiftKeyDown ? currentStack.getCount() : 1;

        if (allowClickFilling && fluidTank.getFluidAmount() > 0) {
            boolean performedFill = false;
            FluidStack initialFluid = fluidTank.getFluid();
            for (int i = 0; i < maxAttempts; i++) {
                FluidActionResult result = FluidUtils.tryFillContainer(currentStack, (IFluidHandler) fluidTank, Integer.MAX_VALUE, null, false);
                if (!result.isSuccess()) break;
                currentStack = FluidUtils.tryFillContainer(currentStack, (IFluidHandler) fluidTank, Integer.MAX_VALUE, null, true).getResult();
                performedFill = true;
            }
            if (performedFill) {
                SoundEvent soundevent = initialFluid.getFluid().getAttributes().getFillSound(initialFluid);
                player.level.playSound(null, player.position().x, player.position().y + 0.5, player.position().z, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
                gui.entityPlayer.inventory.setCarried(currentStack);
                return currentStack.getCount();
            }
        }

        if (allowClickEmptying) {
            boolean performedEmptying = false;
            for (int i = 0; i < maxAttempts; i++) {
                FluidActionResult result = FluidUtils.tryEmptyContainer(currentStack, (IFluidHandler) fluidTank, Integer.MAX_VALUE, null, false);
                if (!result.isSuccess()) break;
                currentStack = FluidUtils.tryEmptyContainer(currentStack, (IFluidHandler) fluidTank, Integer.MAX_VALUE, null, true).getResult();
                performedEmptying = true;
            }
            FluidStack filledFluid = fluidTank.getFluid();
            if (performedEmptying) {
                SoundEvent soundevent = filledFluid.getFluid().getAttributes().getEmptySound(filledFluid);
                player.level.playSound(null, player.position().x, player.position().y + 0.5, player.position().z, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
                gui.entityPlayer.inventory.setCarried(currentStack);
                return currentStack.getCount();
            }
        }

        return -1;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if ((allowClickEmptying || allowClickFilling) && isMouseOverElement(mouseX, mouseY)) {
            ItemStack currentStack = gui.entityPlayer.inventory.getCarried();
            if (button == 0 && currentStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                boolean isShiftKeyDown = isShiftDown();
                writeClientAction(1, writer -> writer.writeBoolean(isShiftKeyDown));
                playButtonClickSound();
                return true;
            }
        }
        return false;
    }
}
