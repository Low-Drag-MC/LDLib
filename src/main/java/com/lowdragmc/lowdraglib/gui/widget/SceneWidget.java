package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.client.scene.ISceneRenderHook;
import com.lowdragmc.lowdraglib.client.scene.ImmediateWorldSceneRenderer;
import com.lowdragmc.lowdraglib.client.scene.ParticleManager;
import com.lowdragmc.lowdraglib.client.scene.WorldSceneRenderer;
import com.lowdragmc.lowdraglib.client.utils.RenderUtils;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.utils.BlockPosFace;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import com.lowdragmc.lowdraglib.utils.Vector3;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class SceneWidget extends WidgetGroup {
    @OnlyIn(Dist.CLIENT)
    protected WorldSceneRenderer renderer;
    @OnlyIn(Dist.CLIENT)
    protected TrackedDummyWorld dummyWorld;
    protected boolean dragging;
    protected boolean renderFacing = true;
    protected boolean renderSelect = true;
    protected int currentMouseX;
    protected int currentMouseY;
    protected Vector3f center;
    protected float rotationYaw = 25;
    protected float rotationPitch = -135;
    protected float zoom = 5;
    protected BlockPosFace clickPosFace;
    protected BlockPosFace hoverPosFace;
    protected BlockPosFace selectedPosFace;
    protected BiConsumer<BlockPos, Direction> onSelected;
    protected Set<BlockPos> core;
    protected boolean useCache;
    protected boolean autoReleased;


    public SceneWidget(int x, int y, int width, int height, Level world) {
        super(x, y, width, height);
        if (isRemote()) {
            createScene(world);
        }
    }

    public SceneWidget useCacheBuffer() {
        return useCacheBuffer(true);
    }

    public SceneWidget useCacheBuffer(boolean autoReleased) {
        useCache = true;
        this.autoReleased = autoReleased;
        if (isRemote() && renderer != null) {
            renderer.useCacheBuffer(true);
        }
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    public ParticleManager getParticleManager() {
        if (renderer == null) return null;
        ParticleManager particleManager = renderer.getParticleManager();
        if (particleManager == null) {
            renderer.setParticleManager(particleManager = new ParticleManager());
        }
        return particleManager;
    }

    @Override
    public void setGui(ModularUI gui) {
        super.setGui(gui);
        if (gui != null) {
            gui.registerCloseListener(this::releaseCacheBuffer);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void updateScreen() {
        super.updateScreen();
        ParticleManager particleManager = getParticleManager();
        if (particleManager != null) {
            particleManager.tick();
        }
    }

    public void releaseCacheBuffer() {
        if (isRemote() && renderer != null && autoReleased) {
            renderer.deleteCacheBuffer();
        }
    }

    public void needCompileCache() {
        if (isRemote() && renderer != null) {
            renderer.needCompileCache();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public final void createScene(Level world) {
        if (world == null) return;
        core = new HashSet<>();
        dummyWorld = new TrackedDummyWorld(world);
        dummyWorld.setRenderFilter(pos -> renderer.renderedBlocksMap.keySet().stream().anyMatch(c -> c.contains(pos)));
        if (renderer != null) {
            renderer.deleteCacheBuffer();
        }
        renderer = new ImmediateWorldSceneRenderer(dummyWorld);
        center = new Vector3f();
        renderer.setOnLookingAt(ray -> {});
        renderer.setAfterWorldRender(this::renderBlockOverLay);
        renderer.setCameraLookAt(center, zoom, Math.toRadians(rotationPitch), Math.toRadians(rotationYaw));
        renderer.useCacheBuffer(useCache);
        clickPosFace = null;
        hoverPosFace = null;
        selectedPosFace = null;
    }

    @OnlyIn(Dist.CLIENT)
    public WorldSceneRenderer getRenderer() {
        return renderer;
    }

    @OnlyIn(Dist.CLIENT)
    public TrackedDummyWorld getDummyWorld() {
        return dummyWorld;
    }

    public SceneWidget setOnSelected(BiConsumer<BlockPos, Direction> onSelected) {
        this.onSelected = onSelected;
        return this;
    }

    public SceneWidget setClearColor(int color) {
        if (isRemote()) {
            renderer.setClearColor(color);
        }
        return this;
    }


    public SceneWidget setRenderSelect(boolean renderSelect) {
        this.renderSelect = renderSelect;
        return this;
    }

    public SceneWidget setRenderFacing(boolean renderFacing) {
        this.renderFacing = renderFacing;
        return this;
    }

    public SceneWidget setRenderedCore(Collection<BlockPos> blocks, ISceneRenderHook renderHook) {
        if (isRemote()) {
            core.clear();
            core.addAll(blocks);
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            int maxZ = Integer.MIN_VALUE;
            for (BlockPos vPos : blocks) {
                minX = Math.min(minX, vPos.getX());
                minY = Math.min(minY, vPos.getY());
                minZ = Math.min(minZ, vPos.getZ());
                maxX = Math.max(maxX, vPos.getX());
                maxY = Math.max(maxY, vPos.getY());
                maxZ = Math.max(maxZ, vPos.getZ());
            }
            center = new Vector3f((minX + maxX) / 2f + 0.5F, (minY + maxY) / 2f + 0.5F, (minZ + maxZ) / 2f + 0.5F);
            renderer.addRenderedBlocks(core, renderHook);
            this.zoom = (float) (3.5 * Math.sqrt(Math.max(Math.max(Math.max(maxX - minX + 1, maxY - minY + 1), maxZ - minZ + 1), 1)));
            renderer.setCameraLookAt(center, zoom, Math.toRadians(rotationPitch), Math.toRadians(rotationYaw));
            needCompileCache();
        }
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    public void renderBlockOverLay(WorldSceneRenderer renderer) {
        PoseStack matrixStack = new PoseStack();
        hoverPosFace = null;
        if (isMouseOverElement(currentMouseX, currentMouseY)) {
            BlockHitResult hit = renderer.getLastTraceResult();
            if (hit != null) {
                if (core.contains(hit.getBlockPos())) {
                    hoverPosFace = new BlockPosFace(hit.getBlockPos(), hit.getDirection());
                } else {
                    Vector3 hitPos = new Vector3(hit.getLocation());
                    Level world = renderer.world;
                    Vec3 eyePos = new Vec3(renderer.getEyePos());
                    hitPos.multiply(2); // Double view range to ensure pos can be seen.
                    Vec3 endPos = new Vec3((hitPos.x - eyePos.x), (hitPos.y - eyePos.y), (hitPos.z - eyePos.z));
                    double min = Float.MAX_VALUE;
                    for (BlockPos pos : core) {
                        BlockState blockState = world.getBlockState(pos);
                        if (blockState.getBlock() == Blocks.AIR) {
                            continue;
                        }
                        hit = world.clipWithInteractionOverride(eyePos, endPos, pos, blockState.getInteractionShape(world, pos), blockState);
                        if (hit != null && hit.getType() != HitResult.Type.MISS) {
                            double dist = eyePos.distanceToSqr(hit.getLocation());
                            if (dist < min) {
                                min = dist;
                                hoverPosFace = new BlockPosFace(hit.getBlockPos(), hit.getDirection());
                            }
                        }
                    }
                }
            }
        }
        BlockPosFace tmp = dragging ? clickPosFace : hoverPosFace;
        if (selectedPosFace != null || tmp != null) {
            if (selectedPosFace != null && renderFacing) {
                drawFacingBorder(matrixStack, selectedPosFace, 0xff00ff00);
            }
            if (tmp != null && !tmp.equals(selectedPosFace) && renderFacing) {
                drawFacingBorder(matrixStack, tmp, 0xffffffff);
            }
        }
        if (selectedPosFace == null) return;
        if (renderSelect) {
            RenderUtils.renderBlockOverLay(matrixStack, selectedPosFace.pos, 0.6f, 0, 0, 1.01f);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void drawFacingBorder(PoseStack matrixStack, BlockPosFace posFace, int color) {
        drawFacingBorder(matrixStack, posFace, color, 0);
    }

    @OnlyIn(Dist.CLIENT)
    public void drawFacingBorder(PoseStack matrixStack, BlockPosFace posFace, int color, int inner) {
        matrixStack.pushPose();
        RenderSystem.disableDepthTest();
        RenderUtils.moveToFace(matrixStack, posFace.pos.getX(), posFace.pos.getY(), posFace.pos.getZ(), posFace.facing);
        RenderUtils.rotateToFace(matrixStack, posFace.facing, null);
        matrixStack.scale(1f / 16, 1f / 16, 0);
        matrixStack.translate(-8, -8, 0);
        DrawerHelper.drawBorder(matrixStack, 1 + inner * 2, 1 + inner * 2, 14 - 4 * inner, 14 - 4 * inner, color, 1);
        RenderSystem.enableDepthTest();
        matrixStack.popPose();
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        if (id == -1) {
            selectedPosFace = new BlockPosFace(buffer.readBlockPos(), buffer.readEnum(Direction.class));
            if (onSelected != null) {
                onSelected.accept(selectedPosFace.pos, selectedPosFace.facing);
            }
        } else {
            super.handleClientAction(id, buffer);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (isMouseOverElement(mouseX, mouseY)) {
            dragging = true;
            clickPosFace = hoverPosFace;
            return true;
        }
        dragging = false;
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
        if (isMouseOverElement(mouseX, mouseY)) {
            zoom = (float) Mth.clamp(zoom + (wheelDelta < 0 ? 0.5 : -0.5), 0.1, 999);
            if (renderer != null) {
                renderer.setCameraLookAt(center, zoom, Math.toRadians(rotationPitch), Math.toRadians(rotationYaw));
            }
        }
        return super.mouseWheelMove(mouseX, mouseY, wheelDelta);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging) {
            rotationPitch += dragX + 360;
            rotationPitch = rotationPitch % 360;
            rotationYaw = (float) Mth.clamp(rotationYaw + dragY, -89.9, 89.9);
            if (renderer != null) {
                renderer.setCameraLookAt(center, zoom, Math.toRadians(rotationPitch), Math.toRadians(rotationYaw));
            }
            return false;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        if (hoverPosFace != null && hoverPosFace.equals(clickPosFace)) {
            selectedPosFace = hoverPosFace;
            writeClientAction(-1, buffer -> {
                buffer.writeBlockPos(selectedPosFace.pos);
                buffer.writeEnum(selectedPosFace.facing);
            });
            if (onSelected != null) {
                onSelected.accept(selectedPosFace.pos, selectedPosFace.facing);
            }
            clickPosFace = null;
            return true;
        }
        clickPosFace = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int x = getPosition().x;
        int y = getPosition().y;
        int width = getSize().width;
        int height = getSize().height;
        if (renderer != null) {
            renderer.render(x, y, width, height, mouseX, mouseY);
            if (renderer.isCompiling()) {
                double progress = renderer.getCompileProgress();
                if (progress > 0) {
                    new TextTexture("Renderer is compiling! " + String.format("%.1f", progress * 100) + "%%").setWidth(width).draw(matrixStack, mouseX, mouseY, x, y, width, height);
                }
            }
        }
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        super.drawInBackground(matrixStack, mouseX, mouseY, partialTicks);
        currentMouseX = mouseX;
        currentMouseY = mouseY;
    }

    public SceneWidget setCenter(Vector3f center) {
        this.center = center;
        if (renderer != null) {
            renderer.setCameraLookAt(this.center, zoom, Math.toRadians(rotationPitch), Math.toRadians(rotationYaw));
        }
        return this;
    }

    public SceneWidget setCameraYawAndPitch(float rotationYaw, float rotationPitch) {
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        if (renderer != null) {
            renderer.setCameraLookAt(this.center, zoom, Math.toRadians(rotationPitch), Math.toRadians(rotationYaw));
        }
        return this;
    }
}
