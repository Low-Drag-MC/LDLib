package com.lowdragmc.lowdraglib.client.renderer;

import com.lowdragmc.lowdraglib.client.ClientProxy;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public interface IRenderer {
    IRenderer EMPTY = new IRenderer() {};

    @OnlyIn(Dist.CLIENT)
    default void renderItem(ItemStack stack,
                    ItemCameraTransforms.TransformType transformType,
                    boolean leftHand, MatrixStack matrixStack,
                    IRenderTypeBuffer buffer, int combinedLight,
                    int combinedOverlay, IBakedModel model) {}

    @OnlyIn(Dist.CLIENT)
    default List<BakedQuad> renderModel(IBlockDisplayReader level, BlockPos pos, BlockState state, Direction side, Random rand, IModelData modelData) {
        return Collections.emptyList();
    }

    @OnlyIn(Dist.CLIENT)
    default void onTextureSwitchEvent(TextureStitchEvent.Pre event) {

    }

    @OnlyIn(Dist.CLIENT)
    default void registerTextureSwitchEvent() {
        ClientProxy.renderers.add(this);
    }

    default boolean isRaw() {
        return false;
    }

    default boolean hasTESR(TileEntity tileEntity) {
        return false;
    }

    default boolean isGlobalRenderer(TileEntity tileEntity) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    default void render(TileEntity tileEntity, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

    }

    @OnlyIn(Dist.CLIENT)
    @Nonnull
    default TextureAtlasSprite getParticleTexture() {
        return Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(MissingTextureSprite.getLocation());
    }
}
