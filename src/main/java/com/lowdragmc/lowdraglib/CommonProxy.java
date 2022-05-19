package com.lowdragmc.lowdraglib;


import com.lowdragmc.lowdraglib.gui.factory.TileEntityUIFactory;
import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import com.lowdragmc.lowdraglib.networking.LDLNetworking;
import com.lowdragmc.lowdraglib.particles.IRendererParticleData;
import com.mojang.serialization.Codec;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class CommonProxy {
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, LDLMod.MODID);

    public static RegistryObject<ParticleType<IRendererParticleData>> IRENDERER_PARTICLE = PARTICLE_TYPES.register("irenderer_particle", () -> new ParticleType<IRendererParticleData>(false, IRendererParticleData.DESERIALIZER) {
        @Nonnull
        public Codec<IRendererParticleData> codec() {
            return IRendererParticleData.codec(this);
        }
    });

    public CommonProxy() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.register(this);
        LDLNetworking.init();
        UIFactory.register(TileEntityUIFactory.INSTANCE);
        PARTICLE_TYPES.register(eventBus);
    }
}
