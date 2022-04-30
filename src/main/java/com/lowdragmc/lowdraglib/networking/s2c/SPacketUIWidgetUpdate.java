package com.lowdragmc.lowdraglib.networking.s2c;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.networking.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

public class SPacketUIWidgetUpdate implements IPacket {

    public int windowId;
    public PacketBuffer updateData;

    public SPacketUIWidgetUpdate() {
    }

    public SPacketUIWidgetUpdate(int windowId, PacketBuffer updateData) {
        this.windowId = windowId;
        this.updateData = updateData;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeVarInt(updateData.readableBytes());
        buf.writeBytes(updateData);

        buf.writeVarInt(windowId);
    }

    @Override
    public void decode(PacketBuffer buf) {
        ByteBuf directSliceBuffer = buf.readBytes(buf.readVarInt());
        ByteBuf copiedDataBuffer = Unpooled.copiedBuffer(directSliceBuffer);
        directSliceBuffer.release();
        this.updateData = new PacketBuffer(copiedDataBuffer);

        this.windowId = buf.readVarInt();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void execute(NetworkEvent.Context handler) {
        Screen currentScreen = Minecraft.getInstance().screen;
        if (currentScreen instanceof ModularUIGuiContainer) {
            ((ModularUIGuiContainer) currentScreen).handleWidgetUpdate(this);
        }
    }

}
