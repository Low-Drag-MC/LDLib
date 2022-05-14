package com.lowdragmc.lowdraglib.client;

import com.lowdragmc.lowdraglib.CommonProxy;
import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.jei.JEIClientEventHandler;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.HashSet;
import java.util.Set;

public class ClientProxy extends CommonProxy {
    public static Set<IRenderer> renderers = new HashSet<>();

    public ClientProxy() {
        super();
        Shaders.init();
        if (LDLMod.isModLoaded(LDLMod.MODID_JEI)) {
            MinecraftForge.EVENT_BUS.register(JEIClientEventHandler.class);
        }
    }

    @SubscribeEvent
    public void registerTextures(TextureStitchEvent.Pre event) {
        if (event.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) {
            for (IRenderer renderer : renderers) {
                renderer.onTextureSwitchEvent(event);
            }
        }
    }
}
