package com.lowdragmc.lowdraglib.client.particle;

import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.client.shader.management.Shader;
import com.lowdragmc.lowdraglib.client.shader.management.ShaderManager;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.opengl.GL13;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2022/05/30
 * @implNote TestTrailParticle
 */
public class TestTrailParticle extends TrailParticle{
    public TextureAtlasSprite sprite;
    double rx,ry,rz;

    public TestTrailParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.rx = x;
        this.ry = y;
        this.rz = z;
        maxTail = 10;
        freq = 1;
        gravity = 0;
        this.xd = 0.2F;
//        this.yd = 0.1F;
        this.zd = 0;
        this.lifetime = 500;
        sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(new ResourceLocation("ldlib:particle/tail"));
    }

    @Override
    protected void update() {
        float angle = 2 * Mth.PI * (tickTimer % 30) / 30;
        x = rx + 3 * Mth.cos(angle);
        z = rz + 3 * Mth.sin(angle);
        y = ry + 1 * Mth.sin(angle);
//        x = xo + xd;
//        y = yo + yd;
//        z = zo + zd;
    }

    @Override
    protected float getU0(int tail) {
        return  (tail * 1f) / (maxTail);
    }

    @Override
    protected float getV0(int tail) {
        return 0;
    }

    @Override
    protected float getU1(int tail) {
        return (tail + 1f) / (maxTail);
    }

    @Override
    protected float getV1(int tail) {
        return 1;
    }

    @Override
    protected int getTexture() {
        return 0;
    }

    @Override
    @Nonnull
    public ParticleRenderType getRenderType() {
        return renderType;
    }

    public static final ParticleRenderType renderType = new ParticleRenderType() {
        private TextureTarget target;
        private Shader shader;

        TextureTarget getTarget(RenderTarget mainTarget) {
            if (target == null) {
                target = new TextureTarget(mainTarget.width, mainTarget.height, false, Minecraft.ON_OSX);
            } else if (target.width != mainTarget.width || target.height != mainTarget.height) {
                target.resize(mainTarget.width, mainTarget.height, Minecraft.ON_OSX);
            }
            return target;
        }

        Shader getShader() {
            if (shader == null) {
                shader = new Shader(Shader.ShaderType.FRAGMENT, """
                        #version 150
                        // Author @patriciogv - 2015
                        // http://patriciogonzalezvivo.com
                                                        
                        uniform float iTime;
                        
                        uniform sampler2D iChannel0;
                         
                        const vec3 b_color = vec3(.7, .1, .4);
                                                        
                        in vec2 texCoord;
                                                        
                        out vec4 fragColor;
                                                        
                        float unity_noise_randomValue (vec2 uv)
                        {
                            return fract(sin(dot(uv, vec2(12.9898, 78.233)))*43758.5453);
                        }
                                                
                        float unity_noise_interpolate (float a, float b, float t)
                        {
                            return (1.0-t)*a + (t*b);
                        }
                                                
                        float unity_valueNoise (vec2 uv)
                        {
                            vec2 i = floor(uv);
                            vec2 f = fract(uv);
                            f = f * f * (3.0 - 2.0 * f);
                                                
                            uv = abs(fract(uv) - 0.5);
                            vec2 c0 = i + vec2(0.0, 0.0);
                            vec2 c1 = i + vec2(1.0, 0.0);
                            vec2 c2 = i + vec2(0.0, 1.0);
                            vec2 c3 = i + vec2(1.0, 1.0);
                            float r0 = unity_noise_randomValue(c0);
                            float r1 = unity_noise_randomValue(c1);
                            float r2 = unity_noise_randomValue(c2);
                            float r3 = unity_noise_randomValue(c3);
                                                
                            float bottomOfGrid = unity_noise_interpolate(r0, r1, f.x);
                            float topOfGrid = unity_noise_interpolate(r2, r3, f.x);
                            float t = unity_noise_interpolate(bottomOfGrid, topOfGrid, f.y);
                            return t;
                        }
                                                
                        float Unity_SimpleNoise_float(vec2 UV, float Scale)
                        {
                            float t = 0.0;
                                                
                            float freq = pow(2.0, float(0));
                            float amp = pow(0.5, float(3-0));
                            t += unity_valueNoise(vec2(UV.x*Scale/freq, UV.y*Scale/freq))*amp;
                                                
                            freq = pow(2.0, float(1));
                            amp = pow(0.5, float(3-1));
                            t += unity_valueNoise(vec2(UV.x*Scale/freq, UV.y*Scale/freq))*amp;
                                                
                            freq = pow(2.0, float(2));
                            amp = pow(0.5, float(3-2));
                            t += unity_valueNoise(vec2(UV.x*Scale/freq, UV.y*Scale/freq))*amp;
                            return t;
                        }
                            
                        void mainImage(out vec4 fragColor, in vec2 uv) {
                            vec4 color1 = vec4(221./256.,45./256.,202./256.,1.000);
                            vec4 color2 = vec4(25./256.,195./256.,226./256.,1.000);
                            vec4 lerp = vec4(mix(color1, color2, uv.x));
                            
                            // fragColor = lerp;
                            
                            vec2 noiseOffset = vec2(-0.5, 0) * iTime;
                            float noise = Unity_SimpleNoise_float(uv + noiseOffset, 35.);
                                                
                            float mask = noise + (1. - uv.x) - uv.x;
                                                        
                            vec2 tailOffset = vec2(-0.2, 0) * iTime;

                            vec3 tail = texture(iChannel0, uv + tailOffset).rgb * mask;
                            
                            
                            fragColor = vec4(lerp.rgb * tail, length(clamp(tail, 0., 1.)));
                        }
                                                        
                        void main() {
//                            fragColor = texture(iChannel0, texCoord);
                            mainImage(fragColor, 1. - texCoord);
                        }
                        """).compileShader();
            }
            return shader;
        }

        @Override
        public void begin(BufferBuilder bufferBuilder, @Nonnull TextureManager textureManager) {
            RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
            TextureTarget target = getTarget(mainTarget);

            AbstractTexture abstracttexture = Minecraft.getInstance().getTextureManager().getTexture(new ResourceLocation("ldlib:textures/particle/kila_tail.png"));
            int id = abstracttexture.getId();
            RenderSystem.activeTexture(GL13.GL_TEXTURE0);
            RenderSystem.enableTexture();
            RenderSystem.bindTexture(id);

            ShaderManager.getInstance().renderFullImageInFramebuffer(target, getShader(), cache->{
                Minecraft mc = Minecraft.getInstance();
                float time;
                if (mc.player != null) {
                    time = (mc.player.tickCount + mc.getFrameTime()) / 20;
                } else {
                    time = System.currentTimeMillis() / 1000f;
                }
                cache.glUniform1F("iTime", time);
                cache.glUniform1I("iChannel0", 0);
            });

            RenderSystem.activeTexture(GL13.GL_TEXTURE0);
            RenderSystem.bindTexture(0);

            mainTarget.bindWrite(true);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(Shaders::getParticleShader);
            RenderSystem.setShaderTexture(0, target.getColorTextureId());
            RenderSystem.disableCull();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
            RenderSystem.enableCull();
        }
    };
}
