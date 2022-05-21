package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.LDLMod;
import net.minecraft.client.Minecraft;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
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
        return null;
    }

    @Override
    @Nonnull
    public String gatherStats() {
        return "Dummy";
    }

    @Override
    @Nonnull
    public WorldLightManager getLightEngine() {
        if (LDLMod.isClient()) {
            return Minecraft.getInstance().level.getLightEngine();
        }
        return null;
    }

    @Override
    @Nonnull
    public IBlockReader getLevel() {
        return world;
    }
    
    
}
