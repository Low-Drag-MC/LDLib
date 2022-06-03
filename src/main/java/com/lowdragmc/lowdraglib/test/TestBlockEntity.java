package com.lowdragmc.lowdraglib.test;

import com.lowdragmc.lowdraglib.CommonProxy;
import com.lowdragmc.lowdraglib.client.particle.TestTrailParticle;
import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ShaderTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.SceneWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
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
            Minecraft.getInstance().particleEngine.add(new TestTrailParticle(Minecraft.getInstance().level, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5));
        }

    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
//        BlockPos pos = getBlockPos();
//        SceneWidget sceneWidget = new SceneWidget(30, 10, 100, 100, getLevel()).useCacheBuffer();
//        sceneWidget.setRenderedCore(List.of(pos, pos.above(), pos.below(),
//                pos.relative(Direction.NORTH), pos.relative(Direction.SOUTH), pos.relative(Direction.EAST), pos.relative(Direction.WEST)), null);
        return new ModularUI(200,200, this, entityPlayer)
                .widget(new ImageWidget(0, 0, 200, 200, ShaderTexture.createRawShader("""
                        #version 150
                        // Author @patriciogv - 2015
                        // http://patriciogonzalezvivo.com
                                                        
                        uniform vec2 iResolution;
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
                                                        
                                """)))
//                .widget(sceneWidget)
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
