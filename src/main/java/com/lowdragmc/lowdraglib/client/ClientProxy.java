package com.lowdragmc.lowdraglib.client;

import com.lowdragmc.lowdraglib.CommonProxy;
import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.client.model.LDLRendererModel;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.jei.JEIClientEventHandler;
import com.lowdragmc.lowdraglib.test.TestBlock;
import com.lowdragmc.lowdraglib.utils.CustomResourcePack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
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
    }

    public ClientProxy() {
        super();
        if (LDLMod.isJeiLoaded()) {
            MinecraftForge.EVENT_BUS.register(JEIClientEventHandler.class);
        }
        if (Minecraft.getInstance() != null) {
            Minecraft.getInstance().getResourcePackRepository().addPackFinder(new CustomResourcePack(LDLMod.location, PackSource.DEFAULT, LDLMod.MODID, "LDLib Extended Resources", 6));
        }
    }

    @SubscribeEvent
    public void clientSetup(final FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            Shaders.init();
            if (DEBUG) {
                ItemBlockRenderTypes.setRenderLayer(TestBlock.BLOCK, x -> true);
            }
        });
    }

    @SubscribeEvent
    public void modelRegistry(final ModelRegistryEvent e) {
        ModelLoaderRegistry.registerLoader(new ResourceLocation(LDLMod.MODID, "renderer"), LDLRendererModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public void shaderRegistry(RegisterShadersEvent event) {
        Shaders.registerVanillaShaders(event);
    }

    @SubscribeEvent
    public void registerTextures(TextureStitchEvent.Pre event) {
        if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            for (IRenderer renderer : renderers) {
                renderer.onTextureSwitchEvent(event);
            }
        }
    }
}
