package com.lowdragmc.lowdraglib;


import com.lowdragmc.lowdraglib.gui.factory.TileEntityUIFactory;
import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import com.lowdragmc.lowdraglib.networking.LDLNetworking;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class CommonProxy {
    public CommonProxy() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.register(this);
        LDLNetworking.init();
        UIFactory.register(TileEntityUIFactory.INSTANCE);
    }
}
