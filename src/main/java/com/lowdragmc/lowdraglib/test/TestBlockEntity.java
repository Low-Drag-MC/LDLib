package com.lowdragmc.lowdraglib.test;

import com.lowdragmc.lowdraglib.CommonProxy;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.SceneWidget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

    @Override
    public ModularUI createUI(Player entityPlayer) {
        BlockPos pos = getBlockPos();
        SceneWidget sceneWidget = new SceneWidget(30, 10, 100, 100, getLevel()).useCacheBuffer();
        sceneWidget.setRenderedCore(List.of(pos, pos.above(), pos.below(),
                pos.relative(Direction.NORTH), pos.relative(Direction.SOUTH), pos.relative(Direction.EAST), pos.relative(Direction.WEST)), null);
        return new ModularUI(150,200, this, entityPlayer)
                .widget(new ImageWidget(0, 0, 150, 200, new ColorBorderTexture(1, -1)))
                .widget(sceneWidget)
                .widget(new ButtonWidget(10, 10, 40, 20, ResourceBorderTexture.BUTTON_COMMON, null));
    }

    @Override
    public boolean isInvalid() {
        return false;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public void markAsDirty() {

    }
}
