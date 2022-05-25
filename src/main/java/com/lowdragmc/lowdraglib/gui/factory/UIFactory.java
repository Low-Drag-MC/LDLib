package com.lowdragmc.lowdraglib.gui.factory;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIContainer;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.networking.LDLNetworking;
import com.lowdragmc.lowdraglib.networking.s2c.SPacketUIOpen;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

public abstract class UIFactory<T> {
    public final int uiFactoryId;
    public static final Int2ObjectMap<UIFactory<?>> FACTORIES = new Int2ObjectOpenHashMap<>();

    public UIFactory(){
        uiFactoryId = FACTORIES.size();
    }
    
    public static void register(UIFactory<?> factory) {
        FACTORIES.put(factory.uiFactoryId, factory);
    }

    public final boolean openUI(T holder, ServerPlayer player) {
        if (player instanceof FakePlayer) {
            return false;
        }
        ModularUI uiTemplate = createUITemplate(holder, player);
        if (uiTemplate == null) return false;
        uiTemplate.initWidgets();

        if (player.containerMenu != player.inventoryMenu) {
            player.closeContainer();
        }
        player.nextContainerCounter();
        int currentWindowId = player.containerCounter;

        FriendlyByteBuf serializedHolder = new FriendlyByteBuf(Unpooled.buffer());
        writeHolderToSyncData(serializedHolder, holder);
        ModularUIContainer container = new ModularUIContainer(uiTemplate, currentWindowId);

        //accumulate all initial updates of widgets in open packet
        uiTemplate.mainGroup.writeInitialData(serializedHolder);

        LDLNetworking.sendToPlayer(new SPacketUIOpen(uiFactoryId, serializedHolder, currentWindowId), player);

        player.initMenu(container);
        player.containerMenu = container;

        //and fire forge event only in the end
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, container));
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public final void initClientUI(FriendlyByteBuf serializedHolder, int windowId) {
        T holder = readHolderFromSyncData(serializedHolder);
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer entityPlayer = minecraft.player;

        ModularUI uiTemplate = createUITemplate(holder, entityPlayer);
        uiTemplate.initWidgets();
        ModularUIGuiContainer ModularUIGuiContainer = new ModularUIGuiContainer(uiTemplate, windowId);
        uiTemplate.mainGroup.readInitialData(serializedHolder);
        minecraft.setScreen(ModularUIGuiContainer);
        minecraft.player.containerMenu = ModularUIGuiContainer.getMenu();

    }

    protected abstract ModularUI createUITemplate(T holder, Player entityPlayer);

    @OnlyIn(Dist.CLIENT)
    protected abstract T readHolderFromSyncData(FriendlyByteBuf syncData);

    protected abstract void writeHolderToSyncData(FriendlyByteBuf syncData, T holder);

}
