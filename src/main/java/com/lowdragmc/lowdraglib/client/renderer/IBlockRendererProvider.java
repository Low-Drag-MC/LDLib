package com.lowdragmc.lowdraglib.client.renderer;

import com.lowdragmc.lowdraglib.client.particle.IRendererParticle;
import com.lowdragmc.lowdraglib.particles.IRendererParticleData;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    IRenderer getRenderer(BlockState state, BlockPos pos, IBlockDisplayReader blockReader);

    default int getLightingMap(IBlockDisplayReader world, BlockState state, BlockPos pos) {
        if (state.emissiveRendering(world, pos)) {
            return 15728880;
        } else {
            int i = world.getBrightness(LightType.SKY, pos);
            int j = world.getBrightness(LightType.BLOCK, pos);
            int k = state.getLightValue(world, pos);
            if (j < k) {
                j = k;
            }

            return i << 20 | j << 4;
        }
    }

    @Override
    default boolean addLandingEffects(BlockState state, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        if (getRenderer(state, pos, worldserver) != null) {
            worldserver.sendParticles(new IRendererParticleData(IRENDERER_PARTICLE.get(), pos), entity.getX(), entity.getY(), entity.getZ(), numberOfParticles, 0.0D, 0.0D, 0.0D, 0.15F);
        }
        return true;
    }

    @Override
    default boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
        if (getRenderer(state, pos, world) != null) {
            Vector3d vector3d = entity.getDeltaMovement();
            world.addParticle(new IRendererParticleData(IRENDERER_PARTICLE.get(), pos), entity.getX() + (world.random.nextDouble() - 0.5D) * entity.getBbWidth(), entity.getY() + 0.1D, entity.getZ() + (world.random.nextDouble() - 0.5D) * entity.getBbWidth(), vector3d.x * -4.0D, 1.5D, vector3d.z * -4.0D);
        }
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    default boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (target instanceof BlockRayTraceResult && world instanceof ClientWorld) {
            BlockPos pPos = ((BlockRayTraceResult) target).getBlockPos();
            Direction pSide = ((BlockRayTraceResult) target).getDirection();
            IRenderer renderer = getRenderer(state, pPos, world);
            if (state.getRenderShape() != BlockRenderType.INVISIBLE && renderer != null) {
                int i = pPos.getX();
                int j = pPos.getY();
                int k = pPos.getZ();
                AxisAlignedBB axisalignedbb = state.getShape(world, pPos).bounds();
                double d0 = i + world.random.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.2F) + 0.1F + axisalignedbb.minX;
                double d1 = j + world.random.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.2F) + 0.1F + axisalignedbb.minY;
                double d2 = k + world.random.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.2F) + 0.1F + axisalignedbb.minZ;
                if (pSide == Direction.DOWN) {
                    d1 = j + axisalignedbb.minY - 0.1F;
                }

                if (pSide == Direction.UP) {
                    d1 = j + axisalignedbb.maxY + 0.1F;
                }

                if (pSide == Direction.NORTH) {
                    d2 = k + axisalignedbb.minZ - 0.1F;
                }

                if (pSide == Direction.SOUTH) {
                    d2 = k + axisalignedbb.maxZ + 0.1F;
                }

                if (pSide == Direction.WEST) {
                    d0 = i + axisalignedbb.minX - 0.1F;
                }

                if (pSide == Direction.EAST) {
                    d0 = i + axisalignedbb.maxX + 0.1F;
                }

                Minecraft.getInstance().particleEngine.add((new IRendererParticle((ClientWorld) world, d0, d1, d2, 0.0D, 0.0D, 0.0D, renderer)).init(pPos).setPower(0.2F).scale(0.6F));
            }
        }
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    default boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        IRenderer renderer = getRenderer(state, pos, world);
        if (renderer != null && world instanceof ClientWorld) {
            VoxelShape voxelshape = state.getShape(world, pos);
            voxelshape.forAllBoxes((p_228348_3_, p_228348_5_, p_228348_7_, p_228348_9_, p_228348_11_, p_228348_13_) -> {
                double d1 = Math.min(1.0D, p_228348_9_ - p_228348_3_);
                double d2 = Math.min(1.0D, p_228348_11_ - p_228348_5_);
                double d3 = Math.min(1.0D, p_228348_13_ - p_228348_7_);
                int i = Math.max(2, MathHelper.ceil(d1 / 0.25D));
                int j = Math.max(2, MathHelper.ceil(d2 / 0.25D));
                int k = Math.max(2, MathHelper.ceil(d3 / 0.25D));

                for(int l = 0; l < i; ++l) {
                    for(int i1 = 0; i1 < j; ++i1) {
                        for(int j1 = 0; j1 < k; ++j1) {
                            double d4 = (l + 0.5D) / i;
                            double d5 = (i1 + 0.5D) / j;
                            double d6 = (j1 + 0.5D) / k;
                            double d7 = d4 * d1 + p_228348_3_;
                            double d8 = d5 * d2 + p_228348_5_;
                            double d9 = d6 * d3 + p_228348_7_;
                            Minecraft.getInstance().particleEngine.add((new IRendererParticle((ClientWorld) world, (double)pos.getX() + d7, (double)pos.getY() + d8, (double)pos.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, renderer)).init(pos));
                        }
                    }
                }
            });
        }
        return true;
    }
}
