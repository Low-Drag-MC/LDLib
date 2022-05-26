package com.lowdragmc.lowdraglib.client.particle;

import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.particles.IRendererParticleData;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2022/05/18
 * @implNote DiggingTextureParticle for TextureAtlasSprite particle, see {@link net.minecraft.core.particles.BlockParticleOption}
 */
@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IRendererParticle extends TextureSheetParticle {
    private BlockPos pos;
    private final float uo;
    private final float vo;

    public IRendererParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, IRenderer renderer) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.setSprite(renderer.getParticleTexture());
        this.gravity = 1.0F;
        this.rCol = 0.6F;
        this.gCol = 0.6F;
        this.bCol = 0.6F;
        this.quadSize /= 2.0F;
        this.uo = this.random.nextFloat() * 3.0F;
        this.vo = this.random.nextFloat() * 3.0F;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    public IRendererParticle init(BlockPos p_174846_1_) {
        this.pos = p_174846_1_;
        return this;
    }

    public IRendererParticle init() {
        this.pos = new BlockPos(this.x, this.y, this.z);
        return this;
    }

    protected float getU0() {
        return this.sprite.getU((this.uo + 1.0F) / 4.0F * 16.0F);
    }

    protected float getU1() {
        return this.sprite.getU(this.uo / 4.0F * 16.0F);
    }

    protected float getV0() {
        return this.sprite.getV(this.vo / 4.0F * 16.0F);
    }

    protected float getV1() {
        return this.sprite.getV((this.vo + 1.0F) / 4.0F * 16.0F);
    }

    public int getLightColor(float pPartialTick) {
        int i = super.getLightColor(pPartialTick);
        return i == 0 && this.level.hasChunkAt(this.pos) ? LevelRenderer.getLightColor(this.level, this.pos) : i;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<IRendererParticleData> {
        @Nullable
        @Override
        public Particle createParticle(IRendererParticleData pType,
                                       ClientLevel pLevel, double pX, double pY,
                                       double pZ, double pXSpeed,
                                       double pYSpeed, double pZSpeed) {
            BlockPos pos = pType.getPos();
            BlockState blockstate = pLevel.getBlockState(pos);
            if (blockstate.getBlock() instanceof IBlockRendererProvider block) {
                IRenderer renderer = block.getRenderer(blockstate, pos, pLevel);
                if (renderer != null) {
                    return new IRendererParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, renderer).init();
                }
            }
            return null;
        }
    }

}
