package com.lowdragmc.lowdraglib.pipelike;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.Set;

public class RoutePath<T> {
    private final BlockPos destPipePos;
    private final Direction destFacing;
    private final int distance;
    private final Set<T> path;

    public RoutePath(BlockPos destPipePos, Direction destFacing, Set<T> path, int distance) {
        this.destPipePos = destPipePos;
        this.destFacing = destFacing;
        this.path = path;
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public Set<T> getPath() {
        return path;
    }

    public BlockPos getPipePos() {
        return destPipePos;
    }

    public Direction getFaceToHandler() {
        return destFacing;
    }

    public BlockPos getHandlerPos() {
        return destPipePos.relative(destFacing);
    }

}
