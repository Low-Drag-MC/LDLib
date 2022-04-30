package com.lowdragmc.lowdraglib.networking;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Optional;


public class Networking {

    protected final SimpleChannel network;
    protected int AUTO_ID = 0;
    
    public Networking(ResourceLocation location, String version) {
        network = NetworkRegistry.newSimpleChannel(location,
                () -> version,
                version::equals,
                version::equals);
    }

    public <MSG extends IPacket> void register(Class<MSG> clazz, NetworkDirection direction) {
        network.registerMessage(AUTO_ID++, clazz, IPacket::encode, buffer -> {
            try {
                MSG packet = clazz.newInstance();
                packet.decode(buffer);
                return packet;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }, (msg, ctx) -> {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> msg.execute(context));
            context.setPacketHandled(true);
        }, Optional.ofNullable(direction));
    }

    public <MSG extends IPacket> void registerC2S(Class<MSG> clazz) {
        this.register(clazz, NetworkDirection.PLAY_TO_SERVER);
    }

    public <MSG extends IPacket> void registerS2C(Class<MSG> clazz) {
        this.register(clazz, NetworkDirection.PLAY_TO_CLIENT);
    }

    public void sendToServer(IPacket msg) {
        network.sendToServer(msg);
    }

    public void sendToPlayer(IPacket msg, ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer))
            network.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public void sendToAll(IPacket msg) {
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            sendToPlayer(msg, player);
        }
    }

    public void sendToAllAround(IPacket msg, ServerWorld world, AxisAlignedBB alignedBB) {
        for (ServerPlayerEntity player : world.getEntitiesOfClass(ServerPlayerEntity.class, alignedBB)) {
            sendToPlayer(msg, player);
        }
    }

}
