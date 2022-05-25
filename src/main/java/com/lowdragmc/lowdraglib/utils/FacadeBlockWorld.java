package com.lowdragmc.lowdraglib.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: KilaBash
 * Date: 2022/04/26
 * Description:
 */
@OnlyIn(Dist.CLIENT)
public class FacadeBlockWorld extends DummyWorld {

    public final Level world;
    public final BlockPos pos;
    public final BlockState state;
    public final BlockEntity tile;

    public FacadeBlockWorld(Level world, BlockPos pos, BlockState state, BlockEntity tile) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.tile = tile;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pPos) {
        return pPos.equals(pos) ? tile : world.getBlockEntity(pPos);
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public BlockState getBlockState(BlockPos pPos) {
        return pPos.equals(pos) ? state : world.getBlockState(pPos);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return world.getLightEngine();
    }

    @Override
    public int getBrightness(@Nonnull LightLayer lightType, @Nonnull BlockPos pos) {
        return world.getBrightness(lightType, pos);
    }

    @Override
    public int getBlockTint(@Nonnull BlockPos blockPos, @Nonnull ColorResolver colorResolver) {
        return world.getBlockTint(blockPos, colorResolver);
    }

    @Override
    public boolean canSeeSky(@Nonnull BlockPos pos) {
        return world.canSeeSky(pos);
    }

    @Nonnull
    @Override
    public DimensionType dimensionType() {
        return world.dimensionType();
    }

    @Override
    public boolean isEmptyBlock(BlockPos pPos) {
        return !pPos.equals(pos) && world.isEmptyBlock(pPos);
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public Holder<Biome> getBiome(@Nonnull BlockPos pos) {
        return world.getBiome(pos);
    }

    @Override
    public @Nullable BlockEntity getExistingBlockEntity(BlockPos pos) {
        return pos.equals(this.pos) ? tile : world.getExistingBlockEntity(pos);
    }
}
