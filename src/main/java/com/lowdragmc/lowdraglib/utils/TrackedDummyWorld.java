package com.lowdragmc.lowdraglib.utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Author: KilaBash
 * Date: 2021/08/25
 * Description: TrackedDummyWorld. Used to build a Fake World.
 */
public class TrackedDummyWorld extends DummyWorld {
    

    private Predicate<BlockPos> renderFilter;
    public final World proxyWorld;
    public final Map<BlockPos, BlockInfo> renderedBlocks = new HashMap<>();

    public final Vector3f minPos = new Vector3f(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    public final Vector3f maxPos = new Vector3f(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

    public void setRenderFilter(Predicate<BlockPos> renderFilter) {
        this.renderFilter = renderFilter;
    }

    public TrackedDummyWorld(){
        this(null);
    }

    public TrackedDummyWorld(World world){
        proxyWorld = world;
    }

    public Map<BlockPos, BlockInfo> getRenderedBlocks() {
        return renderedBlocks;
    }

    public void addBlocks(Map<BlockPos, BlockInfo> renderedBlocks) {
        renderedBlocks.forEach(this::addBlock);
    }

    public void addBlock(BlockPos pos, BlockInfo blockInfo) {
        if (blockInfo.getBlockState().getBlock() == Blocks.AIR)
            return;
        if (blockInfo.getTileEntity() != null) {
            blockInfo.getTileEntity().setLevelAndPosition(this, pos);
        }
        this.renderedBlocks.put(pos, blockInfo);
        minPos.setX(Math.min(minPos.x(), pos.getX()));
        minPos.setY(Math.min(minPos.y(), pos.getY()));
        minPos.setZ(Math.min(minPos.z(), pos.getZ()));
        maxPos.setX(Math.max(maxPos.x(), pos.getX()));
        maxPos.setY(Math.max(maxPos.y(), pos.getY()));
        maxPos.setZ(Math.max(maxPos.z(), pos.getZ()));
    }

    @Override
    public void setBlockEntity(@Nonnull BlockPos pos, TileEntity tileEntity) {
        renderedBlocks.put(pos, new BlockInfo(renderedBlocks.getOrDefault(pos, BlockInfo.EMPTY).getBlockState(), tileEntity));
    }

    @Override
    public boolean setBlock(@Nonnull BlockPos pos, BlockState state, int a, int b) {
        renderedBlocks.put(pos, new BlockInfo(state, renderedBlocks.getOrDefault(pos, BlockInfo.EMPTY).getTileEntity()));
        return true;
    }

    @Override
    public TileEntity getBlockEntity(@Nonnull BlockPos pos) {
        if (renderFilter != null && !renderFilter.test(pos))
            return null;
        return proxyWorld != null ? proxyWorld.getBlockEntity(pos) : renderedBlocks.getOrDefault(pos, BlockInfo.EMPTY).getTileEntity();
    }

    @Nonnull
    @Override
    public BlockState getBlockState(@Nonnull BlockPos pos) {
        if (renderFilter != null && !renderFilter.test(pos))
            return Blocks.AIR.defaultBlockState(); //return air if not rendering this com.lowdragmc.lowdraglib.test.block
        return proxyWorld != null ? proxyWorld.getBlockState(pos) : renderedBlocks.getOrDefault(pos, BlockInfo.EMPTY).getBlockState();
    }

    public Vector3f getSize() {
        Vector3f result = new Vector3f();
        result.setX(maxPos.x() - minPos.x() + 1);
        result.setY(maxPos.y() - minPos.y() + 1);
        result.setZ(maxPos.z() - minPos.z() + 1);
        return result;
    }

    public Vector3f getMinPos() {
        return minPos;
    }

    public Vector3f getMaxPos() {
        return maxPos;
    }

}
