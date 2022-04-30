package com.lowdragmc.lowdraglib;


import com.lowdragmc.lowdraglib.gui.factory.TileEntityUIFactory;
import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import com.lowdragmc.lowdraglib.networking.LDLNetworking;

public class CommonProxy {
    public CommonProxy() {
        LDLNetworking.init();
        UIFactory.register(TileEntityUIFactory.INSTANCE);
    }
}
