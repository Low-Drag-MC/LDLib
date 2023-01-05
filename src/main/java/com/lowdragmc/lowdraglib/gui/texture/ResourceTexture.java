package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.ui.Editor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberColor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.WrapperConfigurator;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.DialogWidget;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.File;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR;

@RegisterUI(name = "resource_texture", group = "texture")
@NoArgsConstructor
public class ResourceTexture extends TransformTexture {

    @Configurable(name = "ldlib.gui.editor.name.resource")
    public ResourceLocation imageLocation = new ResourceLocation("ldlib:textures/gui/icon.png");

    @Configurable
    @NumberRange(range = {-Float.MAX_VALUE, Float.MAX_VALUE}, wheel = 0.02)
    public float offsetX = 0;

    @Configurable
    @NumberRange(range = {-Float.MAX_VALUE, Float.MAX_VALUE}, wheel = 0.02)
    public float offsetY = 0;

    @Configurable
    @NumberRange(range = {-Float.MAX_VALUE, Float.MAX_VALUE}, wheel = 0.02)
    public float imageWidth = 1;
    @Configurable
    @NumberRange(range = {-Float.MAX_VALUE, Float.MAX_VALUE}, wheel = 0.02)
    public float imageHeight = 1;

    @Configurable
    @NumberColor
    protected int color = -1;

    public ResourceTexture(ResourceLocation imageLocation, float offsetX, float offsetY, float width, float height) {
        this.imageLocation = imageLocation;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    public ResourceTexture(String imageLocation) {
        this(new ResourceLocation(imageLocation), 0, 0, 1, 1);
    }

    public ResourceTexture getSubTexture(float offsetX, float offsetY, float width, float height) {
        return new ResourceTexture(imageLocation,
                this.offsetX + (imageWidth * offsetX),
                this.offsetY + (imageHeight * offsetY),
                this.imageWidth * width,
                this.imageHeight * height);
    }

    public ResourceTexture getSubTexture(double offsetX, double offsetY, double width, double height) {
        return new ResourceTexture(imageLocation,
                this.offsetX + (float)(imageWidth * offsetX),
                this.offsetY + (float)(imageHeight * offsetY),
                this.imageWidth * (float) width,
                this.imageHeight * (float)height);
    }

    public ResourceTexture copy() {
        return getSubTexture(0, 0, 1, 1);
    }

    public ResourceTexture setColor(int color) {
        this.color = color;
        return this;
    }

    public static ResourceTexture fromSpirit(ResourceLocation texture) {
        if (LDLMod.isClient()) {
            var sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
            return new ResourceTexture(TextureAtlas.LOCATION_BLOCKS, sprite.getU0(), sprite.getV0(), sprite.getU1() - sprite.getU0(), sprite.getV1() - sprite.getV0());
        } else {
            return new ResourceTexture("");
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void drawInternal(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        drawSubArea(stack, x, y, width, height, 0, 0, 1, 1);
    }
    
    @OnlyIn(Dist.CLIENT)
    protected void drawSubAreaInternal(PoseStack stack, float x, float y, int width, int height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        //sub area is just different width and height
        float imageU = this.offsetX + (this.imageWidth * drawnU);
        float imageV = this.offsetY + (this.imageHeight * drawnV);
        float imageWidth = this.imageWidth * drawnWidth;
        float imageHeight = this.imageHeight * drawnHeight;
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, imageLocation);
        Matrix4f matrix4f = stack.last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrix4f, x, y + height, 0).uv(imageU, imageV + imageHeight).color(color).endVertex();
        bufferbuilder.vertex(matrix4f, x + width, y + height, 0).uv(imageU + imageWidth, imageV + imageHeight).color(color).endVertex();
        bufferbuilder.vertex(matrix4f, x + width, y, 0).uv(imageU + imageWidth, imageV).color(color).endVertex();
        bufferbuilder.vertex(matrix4f, x, y, 0).uv(imageU, imageV).color(color).endVertex();
        tessellator.end();
    }

    @Override
    public void createPreview(ConfiguratorGroup father) {
        super.createPreview(father);
        WidgetGroup widgetGroup = new WidgetGroup(0, 0, 100, 100);
        ImageWidget imageWidget;
        widgetGroup.addWidget(imageWidget = new ImageWidget(0, 0, 100, 100, new GuiTextureGroup(new ResourceTexture(imageLocation.toString()), this::drawGuides)).setBorder(2, ColorPattern.T_WHITE.color));
        widgetGroup.addWidget(new ButtonWidget(0, 0, 100, 100, IGuiTexture.EMPTY, cd -> {
            if (Editor.INSTANCE == null) return;
            File path = new File(Editor.INSTANCE.getWorkSpace(), "assets/ldlib/textures");
            DialogWidget.showFileDialog(Editor.INSTANCE, "ldlib.gui.editor.tips.select_image", path, true,
                    DialogWidget.suffixFilter(".png"), r -> {
                        if (r != null && r.isFile()) {
                            imageLocation = getTextureFromFile(path, r);
                            offsetX = 0;
                            offsetY = 0;
                            imageWidth = 1;
                            imageHeight = 1;
                            imageWidget.setImage(new GuiTextureGroup(new ResourceTexture(imageLocation.toString()), this::drawGuides));
                        }
                    });
        }));
        WrapperConfigurator base = new WrapperConfigurator("ldlib.gui.editor.group.base_image", widgetGroup);
        base.setTips("ldlib.gui.editor.tips.click_select_image");
        father.addConfigurators(base);
    }

    private ResourceLocation getTextureFromFile(File path, File r){
        return new ResourceLocation("ldlib:" + r.getPath().replace(path.getPath(), "textures").replace('\\', '/'));
    }

    @OnlyIn(Dist.CLIENT)
    protected void drawGuides(PoseStack stack, int mouseX, int mouseY, float x, float y, int width, int height) {
        new ColorBorderTexture(-1, 0xffff0000).draw(stack, 0, 0,
                x + width * offsetX, y + height * offsetY,
                (int) (width * imageWidth), (int) (height * imageHeight));
    }
}
