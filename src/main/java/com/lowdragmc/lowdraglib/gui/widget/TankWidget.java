package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.util.TextFormattingUtil;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.utils.FluidUtils;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

public class TankWidget extends Widget implements IRecipeIngredientSlot {

    public final IFluidTank fluidTank;

    protected boolean showAmount;
    protected boolean allowClickFilling;
    protected boolean allowClickEmptying;
    protected boolean drawHoverTips;
    protected IGuiTexture overlay;

    protected FluidStack lastFluidInTank;
    protected int lastTankCapacity;
    protected BiConsumer<TankWidget, List<Component>> onAddedTooltips;
    protected IngredientIO ingredientIO = IngredientIO.RENDER_ONLY;
    protected ProgressTexture.FillDirection fillDirection = ProgressTexture.FillDirection.ALWAYS_FULL;

    public TankWidget(IFluidTank fluidTank, int x, int y, boolean allowClickContainerFilling, boolean allowClickContainerEmptying) {
        this(fluidTank, x, y, 18, 18, allowClickContainerFilling, allowClickContainerEmptying);
    }

    public TankWidget(IFluidTank fluidTank, int x, int y, int width, int height, boolean allowClickContainerFilling, boolean allowClickContainerEmptying) {
        super(new Position(x, y), new Size(width, height));
        this.fluidTank = fluidTank;
        this.showAmount = true;
        this.allowClickFilling = allowClickContainerFilling;
        this.allowClickEmptying = allowClickContainerEmptying;
        this.drawHoverTips = true;
    }

    public TankWidget setFillDirection(ProgressTexture.FillDirection fillDirection) {
        this.fillDirection = fillDirection;
        return this;
    }

    public TankWidget setOnAddedTooltips(BiConsumer<TankWidget, List<Component>> onAddedTooltips) {
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

    public TankWidget setIngredientIO(IngredientIO ingredientIO) {
        this.ingredientIO = ingredientIO;
        return this;
    }

    @Override
    public Object getJEIIngredient() {
        return lastFluidInTank.isEmpty() ? null : lastFluidInTank;
    }

    @Override
    public IngredientIO getIngredientIo() {
        return ingredientIO;
    }

    private List<Component> getToolTips(List<Component> list) {
        if (this.onAddedTooltips != null) {
            this.onAddedTooltips.accept(this, list);
        }
        return list;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
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
                double progress = lastFluidInTank.getAmount() * 1.0 / Math.max(Math.max(lastFluidInTank.getAmount(), lastTankCapacity), 1);
                float drawnU = (float) fillDirection.getDrawnU(progress);
                float drawnV = (float) fillDirection.getDrawnV(progress);
                float drawnWidth = (float) fillDirection.getDrawnWidth(progress);
                float drawnHeight = (float) fillDirection.getDrawnHeight(progress);
                int width = size.width - 2;
                int height = size.height - 2;
                int x = pos.x + 1;
                int y = pos.y + 1;
                DrawerHelper.drawFluidForGui(matrixStack, lastFluidInTank, lastFluidInTank.getAmount(), (int) (x + drawnU * width), (int) (y + drawnV * height), ((int) (width * drawnWidth)), ((int) (height * drawnHeight)));
            }

            if (showAmount && !lastFluidInTank.isEmpty()) {
                matrixStack.pushPose();
                matrixStack.scale(0.5F, 0.5F, 1);
                String s = TextFormattingUtil.formatLongToCompactStringBuckets(lastFluidInTank.getAmount(), 3) + "B";
                Font fontRenderer = Minecraft.getInstance().font;
                fontRenderer.drawShadow(matrixStack, s, (pos.x + (size.width / 3f)) * 2 - fontRenderer.width(s) + 21, (pos.y + (size.height / 3f) + 6) * 2, 0xFFFFFF);
                matrixStack.popPose();
            }

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
        if (overlay != null) {
            overlay.draw(matrixStack, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInForeground(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (drawHoverTips && isMouseOverElement(mouseX, mouseY)) {
            List<Component> tooltips = new ArrayList<>();
            if (lastFluidInTank != null && !lastFluidInTank.isEmpty()) {
                FluidAttributes fluid = lastFluidInTank.getFluid().getAttributes();
                tooltips.add(fluid.getDisplayName(lastFluidInTank));
                tooltips.add(new TranslatableComponent("ldlib.fluid.amount", lastFluidInTank.getAmount(), lastTankCapacity));
                tooltips.add(new TranslatableComponent("ldlib.fluid.temperature", fluid.getTemperature(lastFluidInTank)));
                tooltips.add(new TranslatableComponent(fluid.isGaseous(lastFluidInTank) ? "ldlib.fluid.state_gas" : "ldlib.fluid.state_liquid"));
            } else {
                tooltips.add(new TranslatableComponent("ldlib.fluid.empty"));
                tooltips.add(new TranslatableComponent("ldlib.fluid.amount", 0, lastTankCapacity));
            }
            if (gui != null) {
                setHoverTooltips(getToolTips(tooltips));
                super.drawInForeground(matrixStack, mouseX, mouseY, partialTicks);
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f);
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
            CompoundTag fluidStackTag = fluidStack.writeToNBT(new CompoundTag());
            writeUpdateInfo(2, buffer -> buffer.writeNbt(fluidStackTag));
        } else if (fluidStack.getAmount() != lastFluidInTank.getAmount()) {
            this.lastFluidInTank.setAmount(fluidStack.getAmount());
            writeUpdateInfo(3, buffer -> buffer.writeVarInt(lastFluidInTank.getAmount()));
        }
    }

    @Override
    public void writeInitialData(FriendlyByteBuf buffer) {
        this.lastTankCapacity = fluidTank.getCapacity();
        buffer.writeVarInt(lastTankCapacity);
        FluidStack fluidStack = fluidTank.getFluid();
        this.lastFluidInTank = fluidStack.copy();
        buffer.writeNbt(fluidStack.writeToNBT(new CompoundTag()));
    }

    @Override
    public void readInitialData(FriendlyByteBuf buffer) {
        this.lastTankCapacity = buffer.readVarInt();
        readUpdateInfo(2, buffer);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
        if (id == 0) {
            this.lastTankCapacity = buffer.readVarInt();
        } else if (id == 1) {
            this.lastFluidInTank = null;
        } else if (id == 2) {
            this.lastFluidInTank = FluidStack.loadFluidStackFromNBT(buffer.readNbt());
        } else if (id == 3 && lastFluidInTank != null) {
            this.lastFluidInTank.setAmount(buffer.readVarInt());
        } else if (id == 4) {
            ItemStack currentStack = gui.getModularUIContainer().getCarried();
            int newStackSize = buffer.readVarInt();
            currentStack.setCount(newStackSize);
            gui.getModularUIContainer().setCarried(currentStack);
        }
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
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
        Player player = gui.entityPlayer;
        ItemStack currentStack = gui.getModularUIContainer().getCarried();
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
                player.level.playSound(null, player.position().x, player.position().y + 0.5, player.position().z, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                gui.getModularUIContainer().setCarried(currentStack);
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
                player.level.playSound(null, player.position().x, player.position().y + 0.5, player.position().z, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                gui.getModularUIContainer().setCarried(currentStack);
                return currentStack.getCount();
            }
        }

        return -1;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if ((allowClickEmptying || allowClickFilling) && isMouseOverElement(mouseX, mouseY)) {
            ItemStack currentStack = gui.getModularUIContainer().getCarried();
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
