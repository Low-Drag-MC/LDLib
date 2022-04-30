package com.lowdragmc.lowdraglib.networking.c2s;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIContainer;
import com.lowdragmc.lowdraglib.networking.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CPacketUIClientAction implements IPacket {

    public int windowId;
    public PacketBuffer updateData;

    public CPacketUIClientAction() {
    }

    public CPacketUIClientAction(int windowId, PacketBuffer updateData) {
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
    public void execute(NetworkEvent.Context handler) {
        ServerPlayerEntity player = handler.getSender();
        if (player != null) {
            Container openContainer = handler.getSender().containerMenu;
            if (openContainer instanceof ModularUIContainer) {
                ((ModularUIContainer)openContainer).handleClientAction(this);
            }
        }
    }
}
