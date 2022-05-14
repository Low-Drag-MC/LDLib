package com.lowdragmc.lowdraglib.client.shader.management;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.client.shader.uniform.UniformCache;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class ShaderManager {

	private static final BooleanSupplier optifine$shaderPackLoaded;

	static {
		Field shaderPackLoadedField = null;
		try {
			Class<?> shadersClass = Class.forName("net.optifine.shaders.Shaders");
			shaderPackLoadedField = shadersClass.getDeclaredField("shaderPackLoaded");
		} catch (Exception ignored) {
			LDLMod.LOGGER.debug("Cannot detect Optifine, not going to do any specific compatibility patches.");
		}
		if (shaderPackLoadedField == null) {
			optifine$shaderPackLoaded = () -> false;
		} else {
			Field finalShaderPackLoadedField = shaderPackLoadedField;
			optifine$shaderPackLoaded = () -> {
				try {
					return finalShaderPackLoadedField.getBoolean(null);
				} catch (IllegalAccessException ignored) { }
				return false;
			};
		}
	}

	private static final ShaderManager INSTANCE = new ShaderManager();

	public static ShaderManager getInstance() {
		return INSTANCE;
	}

	public static boolean allowedShader() {
		return true;
	}

	public static boolean isShadersCompatible() {
		return !optifine$shaderPackLoaded.getAsBoolean();
	}

	private final Reference2ReferenceMap<Shader, ShaderProgram> programs;

	private ShaderManager() {
		this.programs = new Reference2ReferenceOpenHashMap<>();
	}

	public Framebuffer renderFullImageInFramebuffer(Framebuffer fbo, Shader frag, Consumer<UniformCache> consumeCache) {
		if (fbo == null || frag == null || !allowedShader()) {
			return fbo;
		}

//		int lastID = glGetInteger(EXTFramebufferObject.GL_FRAMEBUFFER_BINDING_EXT);
		fbo.bindWrite(true);
		ShaderProgram program = programs.get(frag);
		if (program == null) {
			programs.put(frag, program = new ShaderProgram());
			program.attach(Shaders.IMAGE_V).attach(frag);
		}
		program.use(cache -> {
			cache.glUniform2F("iResolution", fbo.width, fbo.height);
			if (consumeCache != null) {
				consumeCache.accept(cache);
			}
		});
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.vertex(-1, 1, 0).uv(0, 0).endVertex();
		buffer.vertex(-1, -1, 0).uv(0, 1).endVertex();
		buffer.vertex(1, -1, 0).uv(1, 1).endVertex();
		buffer.vertex(1, 1, 0).uv(1, 0).endVertex();
		tessellator.end();
		program.release();
//		GlStateManager.viewport(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
//		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, lastID);
		return fbo;
	}

}
