package com.lowdragmc.lowdraglib.client.scene;

import com.lowdragmc.lowdraglib.LDLMod;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.FramebufferConstants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: KilaBash
 * @Date: 2021/08/23
 * @Description: It looks similar to {@link ImmediateWorldSceneRenderer}, but totally different.
 * It uses FBO and is more universality and efficient(X).
 * FBO can be rendered anywhere more flexibly, not just in the GUI.
 * If you have scene rendering needs, you will love this FBO renderer.
 * TODO OP_LIST might be used in the future to further improve performance.
 */
@OnlyIn(Dist.CLIENT)
public class FBOWorldSceneRenderer extends WorldSceneRenderer {
    private int resolutionWidth = 1080;
    private int resolutionHeight = 1080;
    private Framebuffer fbo;

    public FBOWorldSceneRenderer(World world, int resolutionWidth, int resolutionHeight) {
        super(world);
        setFBOSize(resolutionWidth, resolutionHeight);
    }

    public FBOWorldSceneRenderer(World world, Framebuffer fbo) {
        super(world);
        this.fbo = fbo;
    }

    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    /***
     * This will modify the size of the FBO. You'd better know what you're doing before you call it.
     */
    public void setFBOSize(int resolutionWidth, int resolutionHeight) {
        this.resolutionWidth = resolutionWidth;
        this.resolutionHeight = resolutionHeight;
        releaseFBO();
        try {
            fbo = new Framebuffer(resolutionWidth, resolutionHeight, true, Minecraft.ON_OSX);
        } catch (Exception e) {
            LDLMod.LOGGER.error(e);
        }
    }

    public RayTraceResult screenPos2BlockPosFace(int mouseX, int mouseY) {
        int lastID = bindFBO();
        RayTraceResult looking = super.screenPos2BlockPosFace(mouseX, mouseY, 0, 0, this.resolutionWidth, this.resolutionHeight);
        unbindFBO(lastID);
        return looking;
    }

    public Vector3f blockPos2ScreenPos(BlockPos pos, boolean depth){
        int lastID = bindFBO();
        Vector3f winPos = super.blockPos2ScreenPos(pos, depth, 0, 0, this.resolutionWidth, this.resolutionHeight);
        unbindFBO(lastID);
        return winPos;
    }

    public void render(float x, float y, float width, float height, float mouseX, float mouseY) {
        // bind to FBO
        int lastID = bindFBO();
        super.render(0, 0, this.resolutionWidth, this.resolutionHeight, (int) (this.resolutionWidth * (mouseX - x) / width), (int) (this.resolutionHeight * (1 - (mouseY - y) / height)));
        // unbind FBO
        unbindFBO(lastID);

        // bind FBO as texture
        RenderSystem.enableTexture();
        RenderSystem.disableLighting();
        lastID = GL11.glGetInteger(GL11.GL_TEXTURE_2D);
        fbo.bindRead();
        RenderSystem.color4f(1,1,1,1);

        // render rect with FBO texture
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

        bufferbuilder.vertex(x + width, y + height, 0).uv(1, 0).endVertex();
        bufferbuilder.vertex(x + width, y, 0).uv(1, 1).endVertex();
        bufferbuilder.vertex(x, y, 0).uv(0, 1).endVertex();
        bufferbuilder.vertex(x, y + height, 0).uv(0, 0).endVertex();
        tessellator.end();

        RenderSystem.bindTexture(lastID);
    }

    public void render(float x, float y, float width, float height, int mouseX, int mouseY) {
        render(x, y, width, height, (float) mouseX, (float) mouseY);
    }

    private int bindFBO(){
        int lastID = GL11.glGetInteger(EXTFramebufferObject.GL_FRAMEBUFFER_BINDING_EXT);
        fbo.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        fbo.clear(Minecraft.ON_OSX);
        fbo.bindWrite(true);
        RenderSystem.pushMatrix();
        return lastID;
    }

    private void unbindFBO(int lastID){
        RenderSystem.popMatrix();
        fbo.unbindRead();
        GlStateManager._glBindFramebuffer(FramebufferConstants.GL_FRAMEBUFFER, lastID);
    }

    public void releaseFBO() {
        if (fbo != null) {
            fbo.destroyBuffers();
        }
        fbo = null;
    }
}
