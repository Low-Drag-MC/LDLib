package com.lowdragmc.lowdraglib.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public interface IPacket {

    void encode(FriendlyByteBuf buf);

    void decode(FriendlyByteBuf buf);

    default void execute(NetworkEvent.Context handler) {
        
    }

}