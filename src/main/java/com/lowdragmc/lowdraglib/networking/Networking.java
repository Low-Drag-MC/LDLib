package com.lowdragmc.lowdraglib.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

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

    public <MSG extends IPacket> void registerBoth(Class<MSG> clazz) {
        registerS2C(clazz);
        registerC2S(clazz);
    }

    public void sendToServer(IPacket msg) {
        network.sendToServer(msg);
    }

    public void sendToPlayer(IPacket msg, ServerPlayer player) {
        if (!(player instanceof FakePlayer))
            network.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public void sendToAllAround(IPacket msg, ServerLevel world, AABB alignedBB) {
        for (ServerPlayer player : world.getEntitiesOfClass(ServerPlayer.class, alignedBB)) {
            sendToPlayer(msg, player);
        }
    }

    public <T> void send(PacketDistributor.PacketTarget target, T packet) {
        network.send(target, packet);
    }

    public void sendToAll(IPacket msg) {
        send(PacketDistributor.ALL.noArg(), msg);
    }

    public <T> void sendToAll(T msg) {
        send(PacketDistributor.ALL.noArg(), msg);
    }

    public <T> void sendToTrackingChunk(LevelChunk levelChunk, T packet) {
        send(PacketDistributor.TRACKING_CHUNK.with(() -> levelChunk), packet);
    }

}
