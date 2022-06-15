package com.lowdragmc.lowdraglib.test;

import com.lowdragmc.lowdraglib.CommonProxy;
import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.client.particle.impl.ShaderTrailParticle;
import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.SceneWidget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * @author KilaBash
 * @date 2022/05/24
 * @implNote TODO
 */
public class TestBlockEntity extends BlockEntity implements IUIHolder {

    public TestBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(CommonProxy.testType, pWorldPosition, pBlockState);
    }

    public void use(Player player) {
        if (!getLevel().isClientSide) {
            if (player.isCrouching())
            BlockEntityUIFactory.INSTANCE.openUI(this, (ServerPlayer) player);
        } else {
            if (player.isCrouching()) return;
             BlockPos pos = getBlockPos();
        }

    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        BlockPos pos = getBlockPos();
        SceneWidget sceneWidget = new SceneWidget(0, 0, 200, 200, getLevel()).useCacheBuffer();
        sceneWidget.setBackground(new ColorBorderTexture(1, -1));
        sceneWidget.setRenderedCore(List.of(pos, pos.above(), pos.below(),
                pos.relative(Direction.NORTH), pos.relative(Direction.SOUTH), pos.relative(Direction.EAST), pos.relative(Direction.WEST)), null);
        if (isRemote()) {
            double rx = pos.getX() + 0.5;
            double rz = pos.getY() + 1.5;
            double ry = pos.getZ() + 0.5;
            ShaderTrailParticle particle = new ShaderTrailParticle(null, rx, ry, rz,
                    new ShaderTrailParticle.ShaderTrailRenderType(new ResourceLocation(LDLMod.MODID, "test_trail"), shaderProgram -> {
                        shaderProgram.bindTexture("iChannel0", new ResourceLocation("ldlib:textures/particle/kila_tail.png"));
                    }));
            particle.setLifetime(-1);
            particle.setOnUpdate(p -> {
                float angle = 2 * Mth.PI * (p.getAge() % 70) / 70;
                p.setPos(rx + 2 * Mth.cos(angle), rz + 2 * Mth.sin(angle), ry + 1 * Mth.sin(angle));
            });
            sceneWidget.getParticleManager().addParticle(particle);
        }
        return new ModularUI(200,200, this, entityPlayer)
                .widget(sceneWidget);
    }

    @Override
    public boolean isInvalid() {
        return false;
    }

    @Override
    public boolean isRemote() {
        return level.isClientSide;
    }

    @Override
    public void markAsDirty() {

    }
}
