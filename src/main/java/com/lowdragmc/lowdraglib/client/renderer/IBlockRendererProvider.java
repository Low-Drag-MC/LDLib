package com.lowdragmc.lowdraglib.client.renderer;

import com.lowdragmc.lowdraglib.client.particle.IRendererParticle;
import com.lowdragmc.lowdraglib.particles.IRendererParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IBlockRenderProperties;
import net.minecraftforge.common.extensions.IForgeBlock;

import javax.annotation.Nullable;

import static com.lowdragmc.lowdraglib.CommonProxy.IRENDERER_PARTICLE;

/**
 * Author: KilaBash
 * Date: 2022/04/21
 * Description: 
 */
public interface IBlockRendererProvider extends IForgeBlock {

    @Nullable
    IRenderer getRenderer(BlockState state, BlockPos pos, BlockAndTintGetter blockReader);

    default int getLightingMap(BlockAndTintGetter world, BlockState state, BlockPos pos) {
        if (state.emissiveRendering(world, pos)) {
            return 15728880;
        } else {
            int i = world.getBrightness(LightLayer.SKY, pos);
            int j = world.getBrightness(LightLayer.BLOCK, pos);
            int k = state.getLightEmission(world, pos);
            if (j < k) {
                j = k;
            }

            return i << 20 | j << 4;
        }
    }

    @Override
    default boolean addLandingEffects(BlockState state, ServerLevel worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        if (getRenderer(state, pos, worldserver) != null) {
            worldserver.sendParticles(new IRendererParticleData(IRENDERER_PARTICLE.get(), pos), entity.getX(), entity.getY(), entity.getZ(), numberOfParticles, 0.0D, 0.0D, 0.0D, 0.15F);
        }
        return true;
    }

    @Override
    default boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (getRenderer(state, pos, world) != null) {
            Vec3 vector3d = entity.getDeltaMovement();
            world.addParticle(new IRendererParticleData(IRENDERER_PARTICLE.get(), pos), entity.getX() + (world.random.nextDouble() - 0.5D) * entity.getBbWidth(), entity.getY() + 0.1D, entity.getZ() + (world.random.nextDouble() - 0.5D) * entity.getBbWidth(), vector3d.x * -4.0D, 1.5D, vector3d.z * -4.0D);
        }
        return true;
    }

    net.minecraftforge.client.IBlockRenderProperties IRENDERER_PARTICLE_PROPERTIES = new IBlockRenderProperties() {
        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
            if (target instanceof BlockHitResult blockHitResult) {
                return IBlockRendererProvider.addHitEffects(state, level, blockHitResult.getBlockPos(), blockHitResult.getDirection(), manager);
            }
            return false;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
            return IBlockRendererProvider.addDestroyEffects(state, level, pos, manager);
        }
    };


    @OnlyIn(Dist.CLIENT)
    static boolean addHitEffects(BlockState blockstate, Level level, BlockPos pPos, Direction pSide, ParticleEngine manager) {
        if (blockstate.getBlock() instanceof IBlockRendererProvider blockRendererProvider) {
            IRenderer renderer = blockRendererProvider.getRenderer(blockstate, pPos, level);
            if (blockstate.getRenderShape() != RenderShape.INVISIBLE && renderer != null && level instanceof ClientLevel) {
                int i = pPos.getX();
                int j = pPos.getY();
                int k = pPos.getZ();
                AABB aabb = blockstate.getShape(level, pPos).bounds();
                double d0 = (double)i + level.random.nextDouble() * (aabb.maxX - aabb.minX - (double)0.2F) + (double)0.1F + aabb.minX;
                double d1 = (double)j + level.random.nextDouble() * (aabb.maxY - aabb.minY - (double)0.2F) + (double)0.1F + aabb.minY;
                double d2 = (double)k + level.random.nextDouble() * (aabb.maxZ - aabb.minZ - (double)0.2F) + (double)0.1F + aabb.minZ;
                if (pSide == Direction.DOWN) {
                    d1 = (double)j + aabb.minY - (double)0.1F;
                }

                if (pSide == Direction.UP) {
                    d1 = (double)j + aabb.maxY + (double)0.1F;
                }

                if (pSide == Direction.NORTH) {
                    d2 = (double)k + aabb.minZ - (double)0.1F;
                }

                if (pSide == Direction.SOUTH) {
                    d2 = (double)k + aabb.maxZ + (double)0.1F;
                }

                if (pSide == Direction.WEST) {
                    d0 = (double)i + aabb.minX - (double)0.1F;
                }

                if (pSide == Direction.EAST) {
                    d0 = (double)i + aabb.maxX + (double)0.1F;
                }

                manager.add((new IRendererParticle((ClientLevel) level, d0, d1, d2, 0.0D, 0.0D, 0.0D, renderer)).setPower(0.2F).scale(0.6F));
            }
            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    static boolean addDestroyEffects(BlockState state, Level level, BlockPos pPos, ParticleEngine manager) {
        if (state.getBlock() instanceof IBlockRendererProvider blockRendererProvider) {
            IRenderer renderer = blockRendererProvider.getRenderer(state, pPos, level);
            if (renderer != null && level instanceof ClientLevel) {
                VoxelShape voxelshape = state.getShape(level, pPos);
                voxelshape.forAllBoxes((p_228348_3_, p_228348_5_, p_228348_7_, p_228348_9_, p_228348_11_, p_228348_13_) -> {
                    double d1 = Math.min(1.0D, p_228348_9_ - p_228348_3_);
                    double d2 = Math.min(1.0D, p_228348_11_ - p_228348_5_);
                    double d3 = Math.min(1.0D, p_228348_13_ - p_228348_7_);
                    int i = Math.max(2, Mth.ceil(d1 / 0.25D));
                    int j = Math.max(2, Mth.ceil(d2 / 0.25D));
                    int k = Math.max(2, Mth.ceil(d3 / 0.25D));

                    for(int l = 0; l < i; ++l) {
                        for(int i1 = 0; i1 < j; ++i1) {
                            for(int j1 = 0; j1 < k; ++j1) {
                                double d4 = (l + 0.5D) / i;
                                double d5 = (i1 + 0.5D) / j;
                                double d6 = (j1 + 0.5D) / k;
                                double d7 = d4 * d1 + p_228348_3_;
                                double d8 = d5 * d2 + p_228348_5_;
                                double d9 = d6 * d3 + p_228348_7_;
                                manager.add((new IRendererParticle((ClientLevel) level, (double)pPos.getX() + d7, (double)pPos.getY() + d8, (double)pPos.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, renderer)));
                            }
                        }
                    }
                });
            }
            return true;
        }
        return false;
    }
}
