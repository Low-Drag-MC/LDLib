package com.lowdragmc.lowdraglib.utils;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;
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

    public final World world;
    public final BlockPos pos;
    public final BlockState state;
    public final TileEntity tile;

    public FacadeBlockWorld(World world, BlockPos pos, BlockState state, TileEntity tile) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.tile = tile;
    }

    @Nullable
    @Override
    public TileEntity getBlockEntity(BlockPos pPos) {
        return pPos.equals(pos) ? tile : world.getBlockEntity(pPos);
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public BlockState getBlockState(BlockPos pPos) {
        return pPos.equals(pos) ? state : world.getBlockState(pPos);
    }

    @Override
    public WorldLightManager getLightEngine() {
        return world.getLightEngine();
    }

    @Override
    public int getBrightness(@Nonnull LightType lightType, @Nonnull BlockPos pos) {
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
    public Biome getBiome(@Nonnull BlockPos pos) {
        return world.getBiome(pos);
    }
    
}
