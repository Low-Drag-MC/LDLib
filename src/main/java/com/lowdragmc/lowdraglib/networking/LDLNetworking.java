package com.lowdragmc.lowdraglib.networking;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.networking.c2s.CPacketUIClientAction;
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

    private static final Networking network = new Networking(new ResourceLocation(LDLMod.MODID, "networking"), "0.0.1");

    public static void init() {
        network.registerS2C(SPacketUIOpen.class);
        network.registerS2C(SPacketUIWidgetUpdate.class);
        network.registerC2S(CPacketUIClientAction.class);
    }

    public static void sendToServer(IPacket packet) {
        network.sendToServer(packet);
    }

    public static void sendToAll(IPacket packet) {
        network.sendToAll(packet);
    }

    public static void sendToPlayer(IPacket packet, ServerPlayer player) {
        network.sendToPlayer(packet, player);
    }

}
