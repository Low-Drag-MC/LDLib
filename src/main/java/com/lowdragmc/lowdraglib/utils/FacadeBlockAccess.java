package com.lowdragmc.lowdraglib.utils;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;
import java.util.stream.Stream;


@OnlyIn(Dist.CLIENT)
public class FacadeBlockAccess implements IBlockReader {

    public final IBlockReader world;
    public final BlockPos pos;
    public final BlockState state;
    public final TileEntity tile;

    public FacadeBlockAccess(IBlockReader world, BlockPos pos, BlockState state, TileEntity tile) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.tile = tile;
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getLightEmission(BlockPos pPos) {
        return world.getLightEmission(pPos);
    }

    @Override
    public int getMaxLightLevel() {
        return world.getMaxLightLevel();
    }

    @Override
    public int getMaxBuildHeight() {
        return world.getMaxBuildHeight();
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public Stream<BlockState> getBlockStates(AxisAlignedBB pArea) {
        return world.getBlockStates(pArea);
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public BlockRayTraceResult clip(RayTraceContext pContext) {
        return world.clip(pContext);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockRayTraceResult clipWithInteractionOverride(Vector3d pStartVec,
                                                           Vector3d pEndVec,
                                                           BlockPos pPos,
                                                           VoxelShape pShape,
                                                           BlockState pState) {
        return world.clipWithInteractionOverride(pStartVec, pEndVec, pPos, pShape, pState);
    }

    @Override
    @ParametersAreNonnullByDefault
    public double getBlockFloorHeight(VoxelShape pShape, Supplier<VoxelShape> p_242402_2_) {
        return world.getBlockFloorHeight(pShape, p_242402_2_);
    }

    @Override
    @ParametersAreNonnullByDefault
    public double getBlockFloorHeight(BlockPos pPos) {
        return world.getBlockFloorHeight(pPos);
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
    @ParametersAreNonnullByDefault
    @Nonnull
    public FluidState getFluidState(BlockPos pPos) {
        return world.getFluidState(pPos);
    }
}
