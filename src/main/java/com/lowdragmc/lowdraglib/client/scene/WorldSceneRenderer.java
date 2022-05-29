package com.lowdragmc.lowdraglib.client.scene;

import com.lowdragmc.lowdraglib.client.utils.EntityCamera;
import com.lowdragmc.lowdraglib.client.utils.glu.GLU;
import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.PositionedRect;
import com.lowdragmc.lowdraglib.utils.Size;
import com.lowdragmc.lowdraglib.utils.Vector3;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;


/**
 * @author KilaBash
 * @date 2021/08/23
 * @implNote  Abstract class, and extend a lot of features compared with the original one.
 */
@SuppressWarnings("ALL")
@OnlyIn(Dist.CLIENT)
public abstract class WorldSceneRenderer {
    protected static final FloatBuffer MODELVIEW_MATRIX_BUFFER = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    protected static final FloatBuffer PROJECTION_MATRIX_BUFFER = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    protected static final IntBuffer VIEWPORT_BUFFER = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
    protected static final FloatBuffer PIXEL_DEPTH_BUFFER = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    protected static final FloatBuffer OBJECT_POS_BUFFER = ByteBuffer.allocateDirect(3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    enum CacheState {
        UNUSED,
        NEED,
        COMPILING,
        COMPILED
    }

    public final World world;
    public final Map<Collection<BlockPos>, ISceneRenderHook> renderedBlocksMap;
    protected VertexBuffer[] vertexBuffers;
    protected Set<BlockPos> tileEntities;
    protected boolean useCache;
    protected AtomicReference<CacheState> cacheState;
    protected int maxProgress;
    protected int progress;
    protected Thread thread;
//    protected ParticleManager particleManager;
    protected EntityCamera viewEntity;

    private Consumer<WorldSceneRenderer> beforeRender;
    private Consumer<WorldSceneRenderer> afterRender;
    private Consumer<RayTraceResult> onLookingAt;
    protected int clearColor;
    private BlockRayTraceResult lastTraceResult;
    private Set<BlockPos> blocked;
    private Vector3f eyePos = new Vector3f(0, 0, 10f);
    private Vector3f lookAt = new Vector3f(0, 0, 0);
    private Vector3f worldUp = new Vector3f(0, 1, 0);

    public WorldSceneRenderer(World world) {
        this.world = world;
        renderedBlocksMap = new LinkedHashMap<>();
        cacheState = new AtomicReference<>(CacheState.UNUSED);
    }

//    public WorldSceneRenderer setParticleManager(ParticleManager particleManager) {
//        if (particleManager == null) {
//            this.particleManager = null;
//            this.viewEntity = null;
//            return this;
//        }
//        this.particleManager = particleManager;
//        this.viewEntity = new EntityCamera(world);
//        setCameraLookAt(eyePos, lookAt, worldUp);
//        return this;
//    }

    public WorldSceneRenderer useCacheBuffer(boolean useCache) {
        if (this.useCache || !Minecraft.getInstance().isSameThread() || LDLMod.isModLoaded(LDLMod.MODID_RUBIDIUM)) return this;
        deleteCacheBuffer();
        if (useCache) {
            List<RenderType> layers = RenderType.chunkBufferLayers();
            this.vertexBuffers = new VertexBuffer[layers.size()];
            for (int j = 0; j < layers.size(); ++j) {
                this.vertexBuffers[j] = new VertexBuffer(layers.get(j).format());
            }
            if (cacheState.get() == CacheState.COMPILING && thread != null) {
                thread.interrupt();
                thread = null;
            }
            cacheState.set(CacheState.NEED);
        }
        this.useCache = useCache;
        return this;
    }

    public WorldSceneRenderer deleteCacheBuffer() {
        if (useCache) {
            for (int i = 0; i < RenderType.chunkBufferLayers().size(); ++i) {
                if (this.vertexBuffers[i] != null) {
                    this.vertexBuffers[i].close();
                }
            }
            if (cacheState.get() == CacheState.COMPILING && thread != null) {
                thread.interrupt();
                thread = null;
            }
        }
        this.tileEntities = null;
        useCache = false;
        cacheState.set(CacheState.UNUSED);
        return this;
    }

    public WorldSceneRenderer needCompileCache() {
        if (cacheState.get() == CacheState.COMPILING && thread != null) {
            thread.interrupt();
            thread = null;
        }
        cacheState.set(CacheState.NEED);
        return this;
    }

    public WorldSceneRenderer setBeforeWorldRender(Consumer<WorldSceneRenderer> callback) {
        this.beforeRender = callback;
        return this;
    }

    public WorldSceneRenderer setAfterWorldRender(Consumer<WorldSceneRenderer> callback) {
        this.afterRender = callback;
        return this;
    }

    public WorldSceneRenderer addRenderedBlocks(Collection<BlockPos> blocks, ISceneRenderHook renderHook) {
        if (blocks != null) {
            this.renderedBlocksMap.put(blocks, renderHook);
        }
        return this;
    }

    public WorldSceneRenderer setBlocked(Set<BlockPos> blocked) {
        this.blocked = blocked;
        return this;
    }

    public WorldSceneRenderer setOnLookingAt(Consumer<RayTraceResult> onLookingAt) {
        this.onLookingAt = onLookingAt;
        return this;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setClearColor(int clearColor) {
        this.clearColor = clearColor;
    }

    public BlockRayTraceResult getLastTraceResult() {
        return lastTraceResult;
    }

    public void render(float x, float y, float width, float height, int mouseX, int mouseY) {
        // setupCamera
        PositionedRect positionedRect = getPositionedRect((int)x, (int)y, (int)width, (int)height);
        PositionedRect mouse = getPositionedRect(mouseX, mouseY, 0, 0);
        mouseX = mouse.position.x;
        mouseY = mouse.position.y;
        setupCamera(positionedRect);
        // render TrackedDummyWorld
        drawWorld();
        // check lookingAt
        this.lastTraceResult = null;
        if (onLookingAt != null && mouseX > positionedRect.position.x && mouseX < positionedRect.position.x + positionedRect.size.width
                && mouseY > positionedRect.position.y && mouseY < positionedRect.position.y + positionedRect.size.height) {
            Vector3f hitPos = unProject(mouseX, mouseY);
            BlockRayTraceResult result = rayTrace(hitPos);
            if (result != null) {
                this.lastTraceResult = null;
                this.lastTraceResult = result;
                onLookingAt.accept(result);
            }
        }
        // resetCamera
        resetCamera();
    }

    public Vector3f getEyePos() {
        return eyePos;
    }

    public Vector3f getLookAt() {
        return lookAt;
    }

    public Vector3f getWorldUp() {
        return worldUp;
    }

    public void setCameraLookAt(Vector3f eyePos, Vector3f lookAt, Vector3f worldUp) {
        this.eyePos = eyePos;
        this.lookAt = lookAt;
        this.worldUp = worldUp;
        if (viewEntity != null) {

            Vector3 xzProduct = new Vector3(lookAt.x() - eyePos.x(), 0, lookAt.z() - eyePos.z());
            double angleYaw = Math.toDegrees(xzProduct.angle(Vector3.Z));
            if (xzProduct.angle(Vector3.X) < Math.PI / 2) {
                angleYaw = -angleYaw;
            }
            double anglePitch = Math.toDegrees(new Vector3(lookAt).subtract(new Vector3(eyePos)).angle(Vector3.Y)) - 90;
//            viewEntity.set(eyePos.x(), eyePos.y(), eyePos.z(), (float) angleYaw, (float) anglePitch);
        }
    }

    public void setCameraLookAt(Vector3f lookAt, double radius, double rotationPitch, double rotationYaw) {
        Vector3 vecX = new Vector3(Math.cos(rotationPitch), 0, Math.sin(rotationPitch));
        Vector3 vecY = new Vector3(0, Math.tan(rotationYaw) * vecX.mag(),0);
        Vector3 pos = vecX.copy().add(vecY).normalize().multiply(radius);
        setCameraLookAt(pos.add(lookAt.x(), lookAt.y(), lookAt.z()).vector3f(), lookAt, worldUp);
    }

    protected PositionedRect getPositionedRect(int x, int y, int width, int height) {
        return new PositionedRect(new Position(x, y), new Size(width, height));
    }

    protected void setupCamera(PositionedRect positionedRect) {
        int x = positionedRect.getPosition().x;
        int y = positionedRect.getPosition().y;
        int width = positionedRect.getSize().width;
        int height = positionedRect.getSize().height;

        RenderSystem.pushLightingAttributes();
        RenderSystem.pushTextureAttributes();

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();

        //setup viewport and clear GL buffers
        RenderSystem.viewport(x, y, width, height);

        clearView(x, y, width, height);

        //setup projection matrix to perspective
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();

        float aspectRatio = width / (height * 1.0f);
        GLU.gluPerspective(60.0f, aspectRatio, 0.1f, 10000.0f);

        //setup modelview matrix
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        GLU.gluLookAt(eyePos.x(), eyePos.y(), eyePos.z(), lookAt.x(), lookAt.y(), lookAt.z(), worldUp.x(), worldUp.y(), worldUp.z());

        RenderHelper.turnOff();
        RenderSystem.disableLighting();
        RenderSystem.enableTexture();
        RenderSystem.enableAlphaTest();
        RenderSystem.depthMask(true);

        Minecraft mc = Minecraft.getInstance();
        RenderSystem.enableCull();
        RenderSystem.enableRescaleNormal();
        mc.getTextureManager().bind(AtlasTexture.LOCATION_BLOCKS);
    }

    protected void clearView(int x, int y, int width, int height) {
        int i = (clearColor & 0xFF0000) >> 16;
        int j = (clearColor & 0xFF00) >> 8;
        int k = (clearColor & 0xFF);
        RenderSystem.clearColor(i / 255.0f, j / 255.0f, k / 255.0f, (clearColor >> 24) / 255.0f);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
    }

    protected void resetCamera() {
        //reset viewport
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.viewport(0, 0, minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());

        //reset projection matrix
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.popMatrix();

        //reset modelview matrix
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.popMatrix();

        //reset attributes
        RenderSystem.popAttributes();
        RenderSystem.popAttributes();

        RenderSystem.shadeModel(7425);
        RenderSystem.enableColorMaterial();
        RenderSystem.colorMaterial(1032, 5634);
        RenderSystem.disableRescaleNormal();
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        RenderSystem.bindTexture(-1);
    }

    protected void drawWorld() {
        if (beforeRender != null) {
            beforeRender.accept(this);
        }

        Minecraft mc = Minecraft.getInstance();
        RenderType oldRenderLayer = MinecraftForgeClient.getRenderLayer();

        float particleTicks = mc.getFrameTime();
        if (useCache) {
            renderCacheBuffer(mc, particleTicks);
        } else {
            BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRenderer();
            try { // render com.lowdragmc.lowdraglib.test.block in each layer
                for (RenderType layer : RenderType.chunkBufferLayers()) {
                    ForgeHooksClient.setRenderLayer(layer);
                    MatrixStack matrixstack = new MatrixStack();
                    Random random = new Random();
                    renderedBlocksMap.forEach((renderedBlocks, hook) -> {
                        if (layer == RenderType.translucent()) { // render tesr before translucent
                            if (hook != null) {
                                hook.apply(true, layer);
                            }
                            renderTESR(renderedBlocks, matrixstack, mc.renderBuffers().bufferSource(), particleTicks);
                        }
                        if (hook != null) {
                            hook.apply(false, layer);
                        } else {
                            setDefaultRenderLayerState(layer);
                        }
                        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
                        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

                        renderBlocks(matrixstack, blockrendererdispatcher, layer, buffer, renderedBlocks);

                        Tessellator.getInstance().end();
                    });
                }
            } finally {
                ForgeHooksClient.setRenderLayer(oldRenderLayer);
            }
        }

        if (afterRender != null) {
            afterRender.accept(this);
        }
    }

    public boolean isCompiling() {
        return cacheState.get() == CacheState.COMPILING;
    }

    public double getCompileProgress() {
        if (maxProgress > 1000) {
            return progress * 1. / maxProgress;
        }
        return 0;
    }

    private void renderCacheBuffer(Minecraft mc, float particleTicks) {
        List<RenderType> layers = RenderType.chunkBufferLayers();
        if (cacheState.get() == CacheState.NEED) {
            progress = 0;
            maxProgress = renderedBlocksMap.keySet().stream().map(Collection::size).reduce(0, Integer::sum) * (layers.size() + 1);
            thread = new Thread(()->{
                cacheState.set(CacheState.COMPILING);
                BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRenderer();
                try { // render com.lowdragmc.lowdraglib.test.block in each layer
                    BlockModelRenderer.enableCaching();
                    MatrixStack matrixstack = new MatrixStack();
                    for (int i = 0; i < layers.size(); i++) {
                        if (Thread.interrupted())
                            return;
                        RenderType layer = layers.get(i);
                        ForgeHooksClient.setRenderLayer(layer);
                        BufferBuilder buffer = new BufferBuilder(layer.bufferSize());
                        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                        renderedBlocksMap.forEach((renderedBlocks, hook) -> {
                            renderBlocks(matrixstack, blockrendererdispatcher, layer, buffer, renderedBlocks);
                        });
                        net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
                        buffer.end();
                        vertexBuffers[i].uploadLater(buffer);
                    }
                    BlockModelRenderer.clearCache();
                } finally {
                    ForgeHooksClient.setRenderLayer(null);
                }
                Set<BlockPos> poses = new HashSet<>();
                renderedBlocksMap.forEach((renderedBlocks, hook) -> {
                    for (BlockPos pos : renderedBlocks) {
                        progress++;
                        if (Thread.interrupted())
                            return;
                        TileEntity tile = world.getBlockEntity(pos);
                        if (tile != null) {
                            if (TileEntityRendererDispatcher.instance.getRenderer(tile) != null) {
                                poses.add(pos);
                            }
                        }
                    }
                });
                if (Thread.interrupted())
                    return;
                tileEntities = poses;
                cacheState.set(CacheState.COMPILED);
                thread = null;
                maxProgress = -1;
            });
            thread.start();
        } else {
            MatrixStack matrixstack = new MatrixStack();
            VertexFormat format = DefaultVertexFormats.BLOCK;
            for (int i = 0; i < layers.size(); i++) {
                RenderType layer = layers.get(i);
                if (layer == RenderType.translucent() && tileEntities != null) { // render tesr before translucent
                    renderTESR(tileEntities, matrixstack, mc.renderBuffers().bufferSource(), particleTicks);
                }
                
                VertexBuffer vertexbuffer = vertexBuffers[i];
                BlockPos blockpos;
                vertexbuffer.bind();
                format.setupBufferState(0L);
                matrixstack.pushPose();


                setDefaultRenderLayerState(layer);
                
                RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

                RenderSystem.drawArrays(7, 0, vertexbuffer.vertexCount);

                matrixstack.popPose();

                VertexBuffer.unbind();
                RenderSystem.clearCurrentColor();
                format.clearBufferState();
            }
        }
    }

    private void renderBlocks(MatrixStack matrixStack, BlockRendererDispatcher blockrendererdispatcher, RenderType layer, BufferBuilder buffer, Collection<BlockPos> renderedBlocks) {
        for (BlockPos pos : renderedBlocks) {
            if (blocked != null && blocked.contains(pos)) {
                continue;
            }
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            TileEntity te = world.getBlockEntity(pos);
            IModelData modelData = net.minecraftforge.client.model.data.EmptyModelData.INSTANCE;
            if (te != null) {
                modelData = te.getModelData();
            }
            if (block == Blocks.AIR) continue;
            if (state.getRenderShape() != BlockRenderType.INVISIBLE && RenderTypeLookup.canRenderInLayer(state, layer)) {
                matrixStack.pushPose();
                matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
                blockrendererdispatcher.renderModel(state, pos, world, matrixStack, buffer, false, world.random, modelData);
                matrixStack.popPose();
            }
            if (maxProgress > 0) {
                progress++;
            }
        }
    }

    private void renderTESR(Collection<BlockPos> poses, MatrixStack matrixStack, IRenderTypeBuffer.Impl buffers, float partialTicks) {
        if (buffers == null) return;
        for (BlockPos pos : poses) {
            TileEntity tile = world.getBlockEntity(pos);
            if (tile != null) {
                matrixStack.pushPose();
                matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
                TileEntityRenderer<TileEntity> tileentityrenderer = TileEntityRendererDispatcher.instance.getRenderer(tile);
                if (tileentityrenderer != null) {
                    if (tile.hasLevel() && tile.getType().isValid(tile.getBlockState().getBlock())) {
                        try {
                            World world = tile.getLevel();
                            tileentityrenderer.render(tile, partialTicks, matrixStack, buffers, 0xF000F0, OverlayTexture.NO_OVERLAY);
                        } catch (Throwable throwable) {
                            CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering Block Entity");
                            CrashReportCategory crashreportcategory = crashreport.addCategory("Block Entity Details");
                            tile.fillCrashReportCategory(crashreportcategory);
                            throw new ReportedException(crashreport);
                        }
                    }
                }
                matrixStack.popPose();
            }
        }
        buffers.endBatch();
    }

    public static void setDefaultRenderLayerState(RenderType layer) {
        RenderSystem.color4f(1, 1, 1, 1);
        if (layer == RenderType.translucent()) { // SOLID
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(false);
        } else { // TRANSLUCENT
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
        }
    }

    public BlockRayTraceResult rayTrace(Vector3f hitPos) {
        Vector3d startPos = new Vector3d(this.eyePos.x(), this.eyePos.y(), this.eyePos.z());
        hitPos.mul(2); // Double view range to ensure pos can be seen.
        Vector3d endPos = new Vector3d((hitPos.x() - startPos.x), (hitPos.y() - startPos.y), (hitPos.z() - startPos.z));
        return this.world.clip(new RayTraceContext(startPos, endPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, null));
    }

    public Vector3f project(BlockPos pos) {
        //read current rendering parameters
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, MODELVIEW_MATRIX_BUFFER);
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, PROJECTION_MATRIX_BUFFER);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, VIEWPORT_BUFFER);

        //rewind buffers after write by OpenGL glGet calls
        MODELVIEW_MATRIX_BUFFER.rewind();
        PROJECTION_MATRIX_BUFFER.rewind();
        VIEWPORT_BUFFER.rewind();

        //call gluProject with retrieved parameters
        GLU.gluProject(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, MODELVIEW_MATRIX_BUFFER, PROJECTION_MATRIX_BUFFER, VIEWPORT_BUFFER, OBJECT_POS_BUFFER);

        //rewind buffers after read by gluProject
        VIEWPORT_BUFFER.rewind();
        PROJECTION_MATRIX_BUFFER.rewind();
        MODELVIEW_MATRIX_BUFFER.rewind();

        //rewind buffer after write by gluProject
        OBJECT_POS_BUFFER.rewind();

        //obtain position in Screen
        float winX = OBJECT_POS_BUFFER.get();
        float winY = OBJECT_POS_BUFFER.get();
        float winZ = OBJECT_POS_BUFFER.get();

        //rewind buffer after read
        OBJECT_POS_BUFFER.rewind();

        return new Vector3f(winX, winY, winZ);
    }

    public Vector3f unProject(int mouseX, int mouseY) {
        //read depth of pixel under mouse
        GL11.glReadPixels(mouseX, mouseY, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, PIXEL_DEPTH_BUFFER);

        //rewind buffer after write by glReadPixels
        PIXEL_DEPTH_BUFFER.rewind();

        //retrieve depth from buffer (0.0-1.0f)
        float pixelDepth = PIXEL_DEPTH_BUFFER.get();

        //rewind buffer after read
        PIXEL_DEPTH_BUFFER.rewind();

        //read current rendering parameters
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, MODELVIEW_MATRIX_BUFFER);
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, PROJECTION_MATRIX_BUFFER);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, VIEWPORT_BUFFER);

        //rewind buffers after write by OpenGL glGet calls
        MODELVIEW_MATRIX_BUFFER.rewind();
        PROJECTION_MATRIX_BUFFER.rewind();
        VIEWPORT_BUFFER.rewind();

        //call gluUnProject with retrieved parameters
        GLU.gluUnProject(mouseX, mouseY, pixelDepth, MODELVIEW_MATRIX_BUFFER, PROJECTION_MATRIX_BUFFER, VIEWPORT_BUFFER, OBJECT_POS_BUFFER);

        //rewind buffers after read by gluUnProject
        VIEWPORT_BUFFER.rewind();
        PROJECTION_MATRIX_BUFFER.rewind();
        MODELVIEW_MATRIX_BUFFER.rewind();

        //rewind buffer after write by gluUnProject
        OBJECT_POS_BUFFER.rewind();

        //obtain absolute position in world
        float posX = OBJECT_POS_BUFFER.get();
        float posY = OBJECT_POS_BUFFER.get();
        float posZ = OBJECT_POS_BUFFER.get();

        //rewind buffer after read
        OBJECT_POS_BUFFER.rewind();

        return new Vector3f(posX, posY, posZ);
    }

    /***
     * For better performance, You'd better handle the event {@link #setOnLookingAt(Consumer)} or {@link #getLastTraceResult()}
     * @param mouseX xPos in Texture
     * @param mouseY yPos in Texture
     * @return RayTraceResult Hit
     */
    protected RayTraceResult screenPos2BlockPosFace(int mouseX, int mouseY, int x, int y, int width, int height) {
        // render a frame
        RenderSystem.enableDepthTest();
        setupCamera(getPositionedRect(x, y, width, height));

        drawWorld();

        Vector3f hitPos = unProject(mouseX, mouseY);
        RayTraceResult result = rayTrace(hitPos);

        resetCamera();

        return result;
    }

    /***
     * For better performance, You'd better do project in {@link #setAfterWorldRender(Consumer)}
     * @param pos BlockPos
     * @param depth should pass Depth Test
     * @return x, y, z
     */
    protected Vector3f blockPos2ScreenPos(BlockPos pos, boolean depth, int x, int y, int width, int height){
        // render a frame
        RenderSystem.enableDepthTest();
        setupCamera(getPositionedRect(x, y, width, height));

        drawWorld();
        Vector3f winPos = project(pos);

        resetCamera();

        return winPos;
    }

}
