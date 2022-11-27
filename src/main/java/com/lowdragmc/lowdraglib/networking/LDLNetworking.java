package com.lowdragmc.lowdraglib.networking;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.networking.both.PacketRPCMethodPayload;
import com.lowdragmc.lowdraglib.networking.c2s.CPacketUIClientAction;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketManagedPayload;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketUIOpen;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketUIWidgetUpdate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Author: KilaBash
 * Date: 2022/04/27
 * Description:
 */
public class LDLNetworking {

    public static final Networking NETWORK = new Networking(new ResourceLocation(LDLMod.MODID, "networking"), "0.0.1");

    public static void init() {
        NETWORK.registerS2C(SPacketUIOpen.class);
        NETWORK.registerS2C(SPacketUIWidgetUpdate.class);
        NETWORK.registerS2C(SPacketManagedPayload.class);

        NETWORK.registerC2S(CPacketUIClientAction.class);

        NETWORK.registerBoth(PacketRPCMethodPayload.class);
    }

    public static void sendToServer(IPacket packet) {
        NETWORK.sendToServer(packet);
    }

    public static void sendToAll(IPacket packet) {
        NETWORK.sendToAll(packet);
    }

    public static void sendToPlayer(IPacket packet, ServerPlayer player) {
        NETWORK.sendToPlayer(packet, player);
    }

}
