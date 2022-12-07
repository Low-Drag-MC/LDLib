package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberColor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.utils.Rect;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector4f;
import lombok.Setter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author KilaBash
 * @date 2022/12/7
 * @implNote RoundBoxTexture
 */
@RegisterUI(name = "round_box_texture")
public class RoundBoxTexture extends TransformTexture{

    @Configurable
    @NumberColor
    public int color = -1;
    @Configurable
    @Setter
    @NumberRange(range = {0, Float.MAX_VALUE}, wheel = 1)
    public float radiusLT = 8;

    @Configurable
    @Setter
    @NumberRange(range = {0, Float.MAX_VALUE}, wheel = 1)
    public float radiusLB = 8;

    @Configurable
    @Setter
    @NumberRange(range = {0, Float.MAX_VALUE}, wheel = 1)
    public float radiusRT = 8;

    @Configurable
    @Setter
    @NumberRange(range = {0, Float.MAX_VALUE}, wheel = 1)
    public float radiusRB = 8;

    public RoundBoxTexture() {
        setColor(ColorPattern.T_RED.color);
    }

    public RoundBoxTexture(int radius, int color) {
        setColor(color);
        setRadius(radius);
    }

    @Override
    public RoundBoxTexture setColor(int color) {
        this.color = color;
        return this;
    }

    public void setRadius(float radius) {
        this.radiusLB = radius;
        this.radiusRT = radius;
        this.radiusRB = radius;
        this.radiusLT = radius;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void drawInternal(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        DrawerHelper.drawRoundBox(stack, Rect.ofRelative((int) x, width, (int) y, height),
                new Vector4f(radiusRT, radiusRB, radiusLT, radiusLB), color);
    }
}
