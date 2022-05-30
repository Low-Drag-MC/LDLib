package com.lowdragmc.lowdraglib.client.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2022/05/30
 * @implNote TestTrailParticle
 */
public class TestTrailParticle extends TrailParticle{
    public TextureAtlasSprite sprite;

    public TestTrailParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        maxTail = 10;
        freq = 1;
        gravity = 0;
        this.xd = 0.2F;
        this.yd = 0.1F;
        this.zd = 0;
        this.lifetime = 2000;
        sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(new ResourceLocation("ldlib:particle/tail"));
    }

    @Override
    protected void update() {
        super.update();
    }

    @Override
    protected float getU0(int tail) {
        return sprite.getU0();
    }

    @Override
    protected float getV0(int tail) {
        return sprite.getV0();
    }

    @Override
    protected float getU1(int tail) {
        return sprite.getU1();
    }

    @Override
    protected float getV1(int tail) {
        return sprite.getV1();
    }

    @Override
    @Nonnull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    public static final ParticleRenderType renderType = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder pBuilder, TextureManager pTextureManager) {

        }

        @Override
        public void end(Tesselator pTesselator) {

        }
    };
}
