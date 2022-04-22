package com.lowdragmc.lowdraglib.test.block;

import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.impl.BlockStateRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import java.util.List;

public class LDLTestBlock extends Block implements IBlockRendererProvider {

    public LDLTestBlock() {
        super(Block.Properties
                .of(Material.METAL)
                .strength(5.0f, 6.0f)
                .sound(SoundType.STONE)
                .harvestLevel(1)
                .harvestTool(ToolType.PICKAXE));
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootContext.Builder pBuilder) {
        return super.getDrops(pState, pBuilder);
    }

    @Override
    @Nonnull
    public IRenderer getRenderer(BlockState state, BlockPos pos, IBlockDisplayReader blockReader) {

        return new BlockStateRenderer(Blocks.GLASS.defaultBlockState());
    }

}
