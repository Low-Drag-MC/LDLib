package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.core.mixins.DimensionTypeAccessor;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Author: KilaBash
 * Date: 2022/04/26
 * Description:
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DummyWorld extends Level {
    public static final DimensionType DIMENSION_TYPE;
    public static final WritableLevelData SPAWN_WORLD_INFO;
    static {
        DIMENSION_TYPE = DimensionTypeAccessor.getDEFAULT_OVERWORLD();
        SPAWN_WORLD_INFO = new WritableLevelData() {
            @Override
            public void setXSpawn(int pXSpawn) {
                
            }

            @Override
            public void setYSpawn(int pYSpawn) {

            }

            @Override
            public void setZSpawn(int pZSpawn) {

            }

            @Override
            public void setSpawnAngle(float pSpawnAngle) {

            }

            @Override
            public int getXSpawn() {
                return 0;
            }

            @Override
            public int getYSpawn() {
                return 0;
            }

            @Override
            public int getZSpawn() {
                return 0;
            }

            @Override
            public float getSpawnAngle() {
                return 0;
            }

            @Override
            public long getGameTime() {
                return 0;
            }

            @Override
            public long getDayTime() {
                return 0;
            }

            @Override
            public boolean isThundering() {
                return false;
            }

            @Override
            public boolean isRaining() {
                return false;
            }

            @Override
            public void setRaining(boolean pRaining) {

            }

            @Override
            public boolean isHardcore() {
                return false;
            }

            @Override
            public GameRules getGameRules() {
                return new GameRules();
            }

            @Override
            public Difficulty getDifficulty() {
                return Difficulty.PEACEFUL;
            }

            @Override
            public boolean isDifficultyLocked() {
                return false;
            }
        };

    }

    private static final ProfilerFiller dummyProfiler = new ProfilerFiller() {
        @Override
        public void startTick() {

        }

        @Override
        public void endTick() {

        }

        @Override
        public void push(String pName) {

        }

        @Override
        public void push(Supplier<String> pNameSupplier) {

        }

        @Override
        public void pop() {

        }

        @Override
        public void popPush(String pName) {

        }

        @Override
        public void popPush(Supplier<String> pNameSupplier) {

        }

        @Override
        public void markForCharting(MetricCategory pCategory) {
            
        }

        @Override
        public void incrementCounter(String pEntryId) {

        }

        @Override
        public void incrementCounter(String p_185258_, int p_185259_) {

        }

        @Override
        public void incrementCounter(Supplier<String> pEntryIdSupplier) {

        }

        @Override
        public void incrementCounter(Supplier<String> p_185260_,
                                     int p_185261_) {

        }
    };

    protected DummyChunkSource chunkProvider = new DummyChunkSource(this);

    public DummyWorld() {
        super(SPAWN_WORLD_INFO, null, new Holder.Direct<>(DIMENSION_TYPE), ()-> dummyProfiler,true, false, 0);
    }

    @Override
    public boolean setBlock(BlockPos pPos, BlockState pState, int pFlags, int pRecursionLeft) {
        return false;
    }

    @Override
    public void setBlockEntity(BlockEntity pBlockEntity) {
    }

    @Override
    public BlockState getBlockState(BlockPos pPos) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public void playSound(@Nullable Player pPlayer,
                          double pX, double pY, double pZ, SoundEvent pSound,
                          SoundSource pCategory, float pVolume, float pPitch) {
        
    }

    @Override
    public void playSound(@Nullable Player pPlayer,
                          Entity pEntity, SoundEvent pEvent,
                          SoundSource pCategory, float pVolume, float pPitch) {

    }

    @Override
    public String gatherChunkSourceStats() {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pPos) {
        return null;
    }

    @Override
    public float getShade(Direction direction, boolean b) {
        switch (direction) {
            case DOWN:
            case UP:
                return 0.9F;
            case NORTH:
            case SOUTH:
                return 0.8F;
            case WEST:
            case EAST:
                return 0.6F;
            default:
                return 1.0F;
        }
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return null;
    }

    @Override
    public Holder<Biome> getBiome(BlockPos pPos) {
        return Biomes.bootstrap();
    }

    @Override
    public int getBrightness(LightLayer pLightType, BlockPos pBlockPos) {
        return 15;
    }

    @Override
    public int getRawBrightness(@Nonnull BlockPos pos, int p_226659_2_) {
        return 15;
    }

    @Override
    public boolean canSeeSky(@Nonnull BlockPos pos) {
        return true;
    }

    @Override
    public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {

    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) {
        return Biomes.bootstrap();
    }


    @Override
    public Entity getEntity(int id) {
        return null;
    }

    @Override
    public MapItemSavedData getMapData(String mapName) {
        return null;
    }

    @Override
    public void setMapData(String pMapId, MapItemSavedData pData) {

    }

    @Override
    public int getFreeMapId() {
        return 0;
    }

    @Override
    public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {

    }

    @Override
    public Scoreboard getScoreboard() {
        return null;
    }

    @Override
    public RecipeManager getRecipeManager() {
        return null;
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return null;
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return null;
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return null;
    }


    @Override
    public boolean isLoaded(BlockPos p_195588_1_) {
        return true;
    }

    @Override
    public ChunkSource getChunkSource() {
        return chunkProvider;
    }

    @Override
    public void levelEvent(@Nullable Player pPlayer, int pType, BlockPos pPos, int pData) {

    }

    @Override
    public void gameEvent(@Nullable Entity pEntity, GameEvent pEvent, BlockPos pPos) {

    }

    @Override
    public RegistryAccess registryAccess() {
        return null;
    }

    @Override
    public List<? extends Player> players() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public FluidState getFluidState(BlockPos pPos) {
        return Fluids.EMPTY.defaultFluidState();
    }
}
