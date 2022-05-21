package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.core.mixins.DimensionTypeAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.ITickList;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraft.world.storage.MapData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Author: KilaBash
 * Date: 2022/04/26
 * Description:
 */
public class DummyWorld extends World {
    public static final DimensionType DIMENSION_TYPE;
    public static final ISpawnWorldInfo SPAWN_WORLD_INFO;
    static {
        DIMENSION_TYPE = DimensionTypeAccessor.getDEFAULT_OVERWORLD();
        SPAWN_WORLD_INFO = new ISpawnWorldInfo() {
            @Override
            public void setXSpawn(int pX) {

            }

            @Override
            public void setYSpawn(int pY) {

            }

            @Override
            public void setZSpawn(int pZ) {

            }

            @Override
            public void setSpawnAngle(float pAngle) {

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
            public void setRaining(boolean pIsRaining) {

            }

            @Override
            public boolean isHardcore() {
                return false;
            }

            @Override
            @Nonnull
            public GameRules getGameRules() {
                return new GameRules();
            }

            @Override
            @Nonnull
            public Difficulty getDifficulty() {
                return Difficulty.PEACEFUL;
            }

            @Override
            public boolean isDifficultyLocked() {
                return false;
            }
        };

    }

    private static final IProfiler dummyProfiler = new IProfiler() {
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
        public void incrementCounter(String pEntryId) {

        }

        @Override
        public void incrementCounter(Supplier<String> pEntryIdSupplier) {

        }
    };

    protected DummyChunkProvider chunkProvider = new DummyChunkProvider(this);

    public DummyWorld() {
        super(SPAWN_WORLD_INFO, null, DIMENSION_TYPE, ()-> dummyProfiler,true, false, 0);
    };

    @Override
    public boolean setBlock(BlockPos pPos, BlockState pState, int pFlags, int pRecursionLeft) {
        return false;
    }

    @Override
    public void setBlockEntity(BlockPos p_175690_1_, @Nullable TileEntity p_175690_2_) {
        super.setBlockEntity(p_175690_1_, p_175690_2_);
    }

    @Override
    public BlockState getBlockState(BlockPos pPos) {
        return Blocks.AIR.defaultBlockState();
    }

    @Nullable
    @Override
    public TileEntity getBlockEntity(BlockPos pPos) {
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
    public WorldLightManager getLightEngine() {
        return null;
    }

    @Override
    public int getBlockTint(@Nonnull BlockPos blockPos, @Nonnull
            ColorResolver colorResolver) {
        return colorResolver.getColor(BiomeRegistry.PLAINS, blockPos.getX(), blockPos.getY());
    }

    @Override
    public int getBrightness(@Nonnull LightType lightType, @Nonnull BlockPos pos) {
        return lightType == LightType.SKY ? 15 : 0;
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
    public Biome getUncachedNoiseBiome(int x, int y, int z) {
        return null;
    }


    @Override
    public Entity getEntity(int id) {
        return null;
    }

    @Override
    public MapData getMapData(String mapName) {
        return null;
    }

    @Override
    public void setMapData(MapData mapDataIn) {

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
    public ITagCollectionSupplier getTagManager() {
        return null;
    }

    @Override
    public void playSound(PlayerEntity player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {

    }

    @Override
    public void playSound(PlayerEntity playerIn, Entity entityIn, SoundEvent eventIn, SoundCategory categoryIn, float volume, float pitch) {

    }

    @Override
    public ITickList<Block> getBlockTicks() {
        return null;
    }

    @Override
    public ITickList<Fluid> getLiquidTicks() {
        return null;
    }

    @Override
    public boolean isLoaded(BlockPos p_195588_1_) {
        return true;
    }

    @Override
    public AbstractChunkProvider getChunkSource() {
        return chunkProvider;
    }

    @Override
    public void levelEvent(PlayerEntity playerEntity, int i, BlockPos blockPos, int i1) {

    }

    @Override
    public DynamicRegistries registryAccess() {
        return null;
    }

    @Override
    public List<? extends PlayerEntity> players() {
        return null;
    }

    @Override
    public FluidState getFluidState(BlockPos pPos) {
        return Fluids.EMPTY.defaultFluidState();
    }
}
