package com.lowdragmc.lowdraglib.client.particle.impl;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.client.particle.LParticle;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * @author KilaBash
 * @date 2022/06/15
 * @implNote TextureParticle, texture particle
 */
@OnlyIn(Dist.CLIENT)
public class TextureParticle extends LParticle {

    public ResourceLocation texture = new ResourceLocation(LDLMod.MODID, "textures/particle/kila_tail.png");

    public TextureParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    public TextureParticle(ClientLevel level, double x, double y, double z, double sX, double sY, double sZ) {
        super(level, x, y, z, sX, sY, sZ);
    }

    @Override
    protected float getU0(float pPartialTicks) {
        return 0;
    }

    @Override
    protected float getU1(float pPartialTicks) {
        return 1;
    }

    @Override
    protected float getV0(float pPartialTicks) {
        return 0;
    }

    @Override
    protected float getV1(float pPartialTicks) {
        return 1;
    }

    public TextureParticle setTexture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    protected static final Function<ResourceLocation, ParticleRenderType> TYPE = Util.memoize((texture) -> new ParticleRenderType() {
        @Override
        public void begin(@Nonnull BufferBuilder bufferBuilder, @Nonnull
        TextureManager textureManager) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(Shaders::getParticleShader);
            RenderSystem.setShaderTexture(0, texture);
            RenderSystem.enableCull();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(@Nonnull Tesselator tesselator) {
            tesselator.end();
        }
    });

    @Override
    @Nonnull
    public ParticleRenderType getRenderType() {
        return TYPE.apply(texture);
    }
}
