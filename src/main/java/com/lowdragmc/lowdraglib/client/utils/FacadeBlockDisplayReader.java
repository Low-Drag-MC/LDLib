package com.lowdragmc.lowdraglib.client.utils;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.LightType;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;

import javax.annotation.Nullable;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Author: KilaBash
 * Date: 2022/04/21
 * Description:
 */
public class FacadeBlockDisplayReader implements IBlockDisplayReader {
    public final IBlockDisplayReader parent;
    public final BlockPos pos;
    public final BlockState state;
    public final TileEntity tile;

    public FacadeBlockDisplayReader(IBlockDisplayReader parent, BlockPos pos, BlockState state, TileEntity tile) {
        this.parent = parent;
        this.pos = pos;
        this.state = state;
        this.tile = tile;
    }


    @Override
    public float getShade(Direction pDirection, boolean pIsShade) {
        return parent.getShade(pDirection, pIsShade);
    }

    @Override
    public WorldLightManager getLightEngine() {
        return parent.getLightEngine();
    }

    @Override
    public int getBlockTint(BlockPos pBlockPos, ColorResolver pColorResolver) {
        return parent.getBlockTint(pBlockPos, pColorResolver);
    }

    @Override
    public int getBrightness(LightType pLightType, BlockPos pBlockPos) {
        return parent.getBrightness(pLightType, pBlockPos);
    }

    @Override
    public int getRawBrightness(BlockPos pBlockPos, int pAmount) {
        return parent.getRawBrightness(pBlockPos, pAmount);
    }

    @Override
    public boolean canSeeSky(BlockPos pBlockPos) {
        return parent.canSeeSky(pBlockPos);
    }

    @Nullable
    @Override
    public TileEntity getBlockEntity(BlockPos pPos) {
        return pPos.equals(pos) ? tile : parent.getBlockEntity(pPos);
    }

    @Override
    public BlockState getBlockState(BlockPos pPos) {
        return pPos.equals(pos) ? state : parent.getBlockState(pPos);

    }

    @Override
    public FluidState getFluidState(BlockPos pPos) {
        return parent.getFluidState(pPos);
    }

    @Override
    public int getLightEmission(BlockPos pPos) {
        return parent.getLightEmission(pPos);
    }

    @Override
    public int getMaxLightLevel() {
        return parent.getMaxLightLevel();
    }

    @Override
    public int getMaxBuildHeight() {
        return parent.getMaxBuildHeight();
    }

    @Override
    public Stream<BlockState> getBlockStates(AxisAlignedBB pArea) {
        return parent.getBlockStates(pArea);
    }

    @Override
    public BlockRayTraceResult clip(RayTraceContext pContext) {
        return parent.clip(pContext);
    }

    @Nullable
    @Override
    public BlockRayTraceResult clipWithInteractionOverride(Vector3d pStartVec,
                                                           Vector3d pEndVec,
                                                           BlockPos pPos,
                                                           VoxelShape pShape,
                                                           BlockState pState) {
        return parent.clipWithInteractionOverride(pStartVec, pEndVec, pPos, pShape, pState);
    }

    @Override
    public double getBlockFloorHeight(VoxelShape pShape,
                                      Supplier<VoxelShape> p_242402_2_) {
        return parent.getBlockFloorHeight(pShape, p_242402_2_);
    }

    @Override
    public double getBlockFloorHeight(BlockPos pPos) {
        return parent.getBlockFloorHeight(pPos);
    }
}
