package com.lowdragmc.lowdraglib.client.shader.management;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.client.shader.uniform.UniformCache;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
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

	public RenderTarget renderFullImageInFramebuffer(RenderTarget fbo, Shader frag, Consumer<UniformCache> consumeCache) {
		if (fbo == null || frag == null || !allowedShader()) {
			return fbo;
		}

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

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
		buffer.vertex(-1, 1, 0).endVertex();
		buffer.vertex(-1, -1, 0).endVertex();
		buffer.vertex(1, -1, 0).endVertex();
		buffer.vertex(1, 1, 0).endVertex();
		buffer.end();
		BufferUploader._endInternal(buffer);

		program.release();

		return fbo;
	}

}
