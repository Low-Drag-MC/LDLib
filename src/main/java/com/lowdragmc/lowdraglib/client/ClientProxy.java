package com.lowdragmc.lowdraglib.client;

import com.lowdragmc.lowdraglib.CommonProxy;
import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.client.model.LDLRendererModel;
import com.lowdragmc.lowdraglib.client.particle.IRendererParticle;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.jei.JEIClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
    public static Set<IRenderer> renderers = new HashSet<>();

    @SubscribeEvent
    public void onParticleFactoryRegister(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(IRENDERER_PARTICLE.get(), new IRendererParticle.Factory());
    }
    public ClientProxy() {
        super();
        if (LDLMod.isModLoaded(LDLMod.MODID_JEI)) {
            MinecraftForge.EVENT_BUS.register(JEIClientEventHandler.class);
        }
    }

    @SubscribeEvent
    public void clientSetup(final FMLClientSetupEvent e) {
        e.enqueueWork(Shaders::init);
    }

    @SubscribeEvent
    public void modelRegistry(final ModelRegistryEvent e) {
        ModelLoaderRegistry.registerLoader(new ResourceLocation(LDLMod.MODID, "renderer"), LDLRendererModel.Loader.INSTANCE);
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
