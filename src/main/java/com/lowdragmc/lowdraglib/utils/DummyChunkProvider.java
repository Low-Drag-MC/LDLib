package com.lowdragmc.lowdraglib.utils;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.lighting.WorldLightManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DummyChunkProvider extends AbstractChunkProvider {

    private final World world;

    public DummyChunkProvider(World world) {
        this.world = world;
    }

    @Nullable
    @Override
    public IChunk getChunk(int x, int z, @Nonnull ChunkStatus pRequiredStatus, boolean pLoad) {
        return new EmptyChunk(world, new ChunkPos(x, z));
    }

    @Override
    @Nonnull
    public String gatherStats() {
        return "Dummy";
    }

    @Override
    @Nonnull
    public WorldLightManager getLightEngine() {
        return null;
    }

    @Override
    @Nonnull
    public IBlockReader getLevel() {
        return world;
    }
    
    
}
