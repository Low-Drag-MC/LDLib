package com.lowdragmc.lowdraglib.gui.factory;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityUIFactory extends UIFactory<TileEntity>{
    public static final TileEntityUIFactory INSTANCE  = new TileEntityUIFactory();

    private TileEntityUIFactory() {
        super();
    }

    @Override
    protected ModularUI createUITemplate(TileEntity holder, PlayerEntity entityPlayer) {
        if (holder instanceof IUIHolder) {
            return ((IUIHolder) holder).createUI(entityPlayer);
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected TileEntity readHolderFromSyncData(PacketBuffer syncData) {
        World world = Minecraft.getInstance().level;
        return world == null ? null : world.getBlockEntity(syncData.readBlockPos());
    }

    @Override
    protected void writeHolderToSyncData(PacketBuffer syncData, TileEntity holder) {
        syncData.writeBlockPos(holder.getBlockPos());
    }
}
