package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class BlockSelectorWidget extends WidgetGroup {
    private Consumer<BlockState> onBlockStateUpdate;
    private Block block;
    private int meta;
    private final IItemHandlerModifiable handler;
    private final TextFieldWidget blockField;
    private final TextFieldWidget metaField;

    public BlockSelectorWidget(int x, int y, boolean isState) {
        super(x, y, 180, 20);
        setClientSideWidget();
        blockField = (TextFieldWidget) new TextFieldWidget(22, 0, isState ? 119 : 139, 20, null, s -> {
            if (s != null && !s.isEmpty()) {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
                if (this.block != block) {
                    this.block = block;
                    onUpdate();
                }
            }
        }).setHoverTooltips("multiblocked.gui.tips.block_register");
        metaField = (TextFieldWidget) new TextFieldWidget(142, 0, 20, 20, null, s -> {
            meta = Integer.parseInt(s);
            onUpdate();
        }).setNumbersOnly(0, Integer.MAX_VALUE).setHoverTooltips("multiblocked.gui.tips.block_meta");

        addWidget(new PhantomSlotWidget(handler = new ItemStackHandler(1), 0, 1, 1)
                .setClearSlotOnRightClick(true)
                .setChangeListener(() -> {
                    ItemStack stack = handler.getStackInSlot(0);
                    if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem)) {
                        if (block != null) {
                            block = null;
                            meta = 0;
                            blockField.setCurrentString("");
                            metaField.setCurrentString("0");
                            onUpdate();
                        }
                    } else {
                        BlockItem itemBlock = (BlockItem) stack.getItem();
                        block = itemBlock.getBlock();
                        meta = 0;
                        blockField.setCurrentString(block.getRegistryName() == null ? "" : block.getRegistryName().toString());
                        metaField.setCurrentString("0");
                        onUpdate();
                    }
                }).setBackgroundTexture(new ColorBorderTexture(1, -1)));
        addWidget(blockField);
        if (isState) {
            addWidget(metaField);
        }
    }

    public BlockState getBlock() {
        return block == null ? null : block.defaultBlockState();
    }

    public BlockSelectorWidget setBlock(BlockState blockState) {
        if (blockState == null) {
            block = null;
            meta = 0;
            handler.setStackInSlot(0, ItemStack.EMPTY);
            blockField.setCurrentString("");
            metaField.setCurrentString("0");
        } else {
            block = blockState.getBlock();
            new ItemStack(block);
            meta = block.getStateDefinition().getPossibleStates().indexOf(blockState);
            handler.setStackInSlot(0, new ItemStack(block));
            blockField.setCurrentString(block.getRegistryName() == null ? "" : block.getRegistryName().toString());
            metaField.setCurrentString(meta + "");
        }
        return this;
    }

    public BlockSelectorWidget setOnBlockStateUpdate(Consumer<BlockState> onBlockStateUpdate) {
        this.onBlockStateUpdate = onBlockStateUpdate;
        return this;
    }

    private void onUpdate() {
        handler.setStackInSlot(0, block == null ? ItemStack.EMPTY : new ItemStack(block));
        if (onBlockStateUpdate != null) {
            BlockState state = block == null ? null :
                    block.getStateDefinition().getPossibleStates().size() > meta ?
                            block.getStateDefinition().getPossibleStates().get(meta) : null;
            onBlockStateUpdate.accept(state);
        }
    }
}
