package com.lowdragmc.lowdraglib.networking;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
public interface IPacket {

    void encode(PacketBuffer buf);

    void decode(PacketBuffer buf);

    default void execute(NetworkEvent.Context handler) {
        
    }

}