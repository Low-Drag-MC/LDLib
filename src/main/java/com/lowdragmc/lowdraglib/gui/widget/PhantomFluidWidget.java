package com.lowdragmc.lowdraglib.gui.widget;

import com.google.common.collect.Lists;
import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.gui.ingredient.IGhostIngredientTarget;
import com.lowdragmc.lowdraglib.gui.ingredient.Target;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PhantomFluidWidget extends TankWidget implements IGhostIngredientTarget {

    private Consumer<FluidStack> fluidStackUpdater;

    public PhantomFluidWidget(IFluidTank fluidTank, int x, int y) {
        super(fluidTank, x, y, false, false);
    }

    public PhantomFluidWidget setFluidStackUpdater(Consumer<FluidStack> fluidStackUpdater) {
        this.fluidStackUpdater = fluidStackUpdater;
        return this;
    }

    public static FluidStack drainFrom(Object ingredient) {
        if (ingredient instanceof ItemStack itemStack) {
            itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).ifPresent(handler -> handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE));
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Target> getPhantomTargets(Object ingredient) {
        if (LDLMod.isReiLoaded() && ingredient instanceof dev.architectury.fluid.FluidStack fluidStack) {
            ingredient = FluidStackHooksForge.toForge(fluidStack);
        }
        if (!(ingredient instanceof FluidStack) && drainFrom(ingredient) == null) {
            return Collections.emptyList();
        }

        Rect2i rectangle = toRectangleBox();
        return Lists.newArrayList(new Target() {
            @Nonnull
            @Override
            public Rect2i getArea() {
                return rectangle;
            }

            @Override
            public void accept(@Nonnull Object ingredient) {
                FluidStack ingredientStack;
                if (LDLMod.isReiLoaded() && ingredient instanceof dev.architectury.fluid.FluidStack fluidStack) {
                    ingredient = FluidStackHooksForge.toForge(fluidStack);
                }
                if (ingredient instanceof FluidStack)
                    ingredientStack = (FluidStack) ingredient;
                else
                    ingredientStack = drainFrom(ingredient);

                if (ingredientStack != null) {
                    CompoundTag tagCompound = ingredientStack.writeToNBT(new CompoundTag());
                    writeClientAction(2, buffer -> buffer.writeNbt(tagCompound));
                }

                if (isClientSideWidget) {
                    fluidTank.drain(fluidTank.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
                    if (ingredientStack != null) {
                        fluidTank.fill(ingredientStack.copy(), IFluidHandler.FluidAction.EXECUTE);
                    }
                    if (fluidStackUpdater != null) {
                        fluidStackUpdater.accept(ingredientStack);
                    }
                }
            }
        });
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        if (id == 1) {
            ItemStack itemStack = gui.getModularUIContainer().getCarried().copy();
            if (!itemStack.isEmpty()) {
                itemStack.setCount(1);
                itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
                    FluidStack resultFluid = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
                    fluidTank.drain(fluidTank.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
                    fluidTank.fill(resultFluid.copy(), IFluidHandler.FluidAction.EXECUTE);
                    if (fluidStackUpdater != null) {
                        fluidStackUpdater.accept(resultFluid);
                    }
                });
            } else {
                fluidTank.drain(fluidTank.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
                if (fluidStackUpdater != null) {
                    fluidStackUpdater.accept(null);
                }
            }
        } else if (id == 2) {
            FluidStack fluidStack;
            fluidStack = FluidStack.loadFluidStackFromNBT(buffer.readNbt());
            fluidTank.drain(fluidTank.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
            if (fluidStack != null) {
                fluidTank.fill(fluidStack.copy(), IFluidHandler.FluidAction.EXECUTE);
            }
            if (fluidStackUpdater != null) {
                fluidStackUpdater.accept(fluidStack);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOverElement(mouseX, mouseY)) {
            writeClientAction(1, buffer -> { });
            if (isClientSideWidget) {
                fluidTank.drain(fluidTank.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
                if (fluidStackUpdater != null) {
                    fluidStackUpdater.accept(null);
                }
            }
            return true;
        }
        return false;
    }

}
