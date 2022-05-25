package com.lowdragmc.lowdraglib.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: KilaBash
 * Date: 2022/04/26
 * Description:
 */
public class BlockInfo {
    public static final BlockInfo EMPTY = new BlockInfo(Blocks.AIR);

    private BlockState blockState;
    private boolean hasBlockEntity;
    private final ItemStack itemStack;
    private BlockEntity lastEntity;

    public BlockInfo(Block block) {
        this(block.defaultBlockState());
    }

    public BlockInfo(BlockState blockState) {
        this(blockState, false);
    }

    public BlockInfo(BlockState blockState, boolean hasBlockEntity) {
        this(blockState, hasBlockEntity, null);
    }

    public BlockInfo(BlockState blockState, boolean hasBlockEntity, ItemStack itemStack) {
        this.blockState = blockState;
        this.hasBlockEntity = hasBlockEntity;
        this.itemStack = itemStack;
    }

    public static BlockInfo fromBlockState(BlockState state) {
        try {
            if (state.getBlock() instanceof EntityBlock) {
                BlockEntity blockEntity = ((EntityBlock) state.getBlock()).newBlockEntity(BlockPos.ZERO, state);
                if (blockEntity != null) {
                    return new BlockInfo(state, true);
                }
            }
        } catch (Exception ignored){ }
        return new BlockInfo(state);
    }

    public static BlockInfo fromBlock(Block block) {
        return BlockInfo.fromBlockState(block.defaultBlockState());
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public boolean hasBlockEntity() {
        return hasBlockEntity;
    }

    public BlockEntity getBlockEntity(BlockPos pos) {
        if (hasBlockEntity && blockState.getBlock() instanceof EntityBlock entityBlock) {
            if (lastEntity != null && lastEntity.getBlockPos().equals(pos)) {
                return lastEntity;
            }
            return lastEntity = entityBlock.newBlockEntity(pos, blockState);
        }
        return null;
    }

    public BlockEntity getBlockEntity(Level level, BlockPos pos) {
        BlockEntity entity = getBlockEntity(pos);
        if (entity != null) {
            entity.setLevel(level);
        }
        return entity;
    }

    public ItemStack getItemStackForm() {
        return itemStack == null ? new ItemStack(blockState.getBlock()) : itemStack;
    }

    public void apply(Level world, BlockPos pos) {
        world.setBlockAndUpdate(pos, blockState);
        BlockEntity blockEntity = getBlockEntity(pos);
        if (blockEntity != null) {
            world.setBlockEntity(blockEntity);
        }
    }

    public void setHasBlockEntity(boolean hasBlockEntity) {
        this.hasBlockEntity = hasBlockEntity;
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }
}
