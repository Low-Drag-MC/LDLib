package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.client.shader.management.Shader;
import com.lowdragmc.lowdraglib.client.shader.management.ShaderManager;
import com.lowdragmc.lowdraglib.client.shader.management.ShaderProgram;
import com.lowdragmc.lowdraglib.client.shader.uniform.UniformCache;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX;

public class ShaderTexture implements IGuiTexture {
    @OnlyIn(Dist.CLIENT)
    private ShaderProgram program;
    @OnlyIn(Dist.CLIENT)
    private Shader shader;
    private float resolution = 2;
    private Consumer<UniformCache> uniformCache;
    private final boolean isRaw;

    private ShaderTexture(boolean isRaw) {
        this.isRaw = isRaw;
    }

    public void dispose() {
        if (isRaw && shader != null) {
            shader.deleteShader();
        }
        if (program != null) {
            program.delete();
        }
        shader = null;
        program = null;
    }

    public void updateRawShader(String rawShader) {
        if (LDLMod.isRemote() && ShaderManager.allowedShader()) {
            dispose();
            shader = new Shader(Shader.ShaderType.FRAGMENT, rawShader).compileShader();
            program = new ShaderProgram();
            program.attach(shader);
        }
    }

    public String getRawShader() {
        if (LDLMod.isRemote() && ShaderManager.allowedShader() && shader !=null) {
            return shader.source;
        }
        return "";
    }

    @OnlyIn(Dist.CLIENT)
    private ShaderTexture(Shader shader, boolean isRaw) {
        this.isRaw = isRaw;
        if (shader == null) return;
        this.program = new ShaderProgram();
        this.shader = shader;
        program.attach(shader);
    }

    public static ShaderTexture createShader(ResourceLocation location) {
        if (LDLMod.isRemote() && ShaderManager.allowedShader()) {
            Shader shader = Shaders.load(Shader.ShaderType.FRAGMENT, location);
            return new ShaderTexture(shader, false);
        } else {
            return new ShaderTexture(false);
        }
    }

    public static ShaderTexture createRawShader(String rawShader) {
        if (LDLMod.isRemote() && ShaderManager.allowedShader()) {
            Shader shader = new Shader(Shader.ShaderType.FRAGMENT, rawShader).compileShader();
            return new ShaderTexture(shader, true);
        } else {
            return new ShaderTexture(true);
        }
    }

    public ShaderTexture setUniformCache(Consumer<UniformCache> uniformCache) {
        this.uniformCache = uniformCache;
        return this;
    }

    public ShaderTexture setResolution(float resolution) {
        this.resolution = resolution;
        return this;
    }

    public float getResolution() {
        return resolution;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        if (program != null) {
            program.use(cache->{
                Minecraft mc = Minecraft.getInstance();
                float time;
                if (mc.player != null) {
                    time = (mc.player.tickCount + mc.getFrameTime()) / 20;
                } else {
                    time = System.currentTimeMillis() / 1000f;
                }
                float mX = Mth.clamp((mouseX - x), 0, width);
                float mY = Mth.clamp((mouseY - y), 0, height);
                cache.glUniform2F("iResolution", width * resolution, height * resolution);
                cache.glUniform2F("iMouse", mX * resolution, mY * resolution);
                cache.glUniform1F("iTime", time);
                if (uniformCache != null) {
                    uniformCache.accept(cache);
                }
            });
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder buffer = tessellator.getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            Matrix4f mat = stack.last().pose();
            buffer.begin(VertexFormat.Mode.QUADS, POSITION_TEX);
            buffer.vertex(mat, x, y + height, 0).uv(0, 0).endVertex();
            buffer.vertex(mat, x + width, y + height, 0).uv(1, 0).endVertex();
            buffer.vertex(mat, x + width, y, 0).uv(1, 1).endVertex();
            buffer.vertex(mat, x, y, 0).uv(0, 1).endVertex();
            tessellator.end();
            program.release();
        } else {
            DrawerHelper.drawText(stack, "Error compiling shader", x + 2, y + 2, 1, 0xffff0000);
        }
    }
}
