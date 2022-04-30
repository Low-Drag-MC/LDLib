package com.lowdragmc.lowdraglib.gui.modular;


import net.minecraft.entity.player.PlayerEntity;

public interface IUIHolder {
    IUIHolder EMPTY = new IUIHolder() {
        @Override
        public ModularUI createUI(PlayerEntity entityPlayer) {
            return null;
        }

        @Override
        public boolean isInvalid() {
            return false;
        }

        @Override
        public boolean isRemote() {
            return true;
        }

        @Override
        public void markAsDirty() {

        }
    };

    ModularUI createUI(PlayerEntity entityPlayer);

    boolean isInvalid();

    boolean isRemote();

    void markAsDirty();

}
