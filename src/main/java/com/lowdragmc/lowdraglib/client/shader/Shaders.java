package com.lowdragmc.lowdraglib.client.shader;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.client.shader.management.Shader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class Shaders {

	public static Shader IMAGE_F;
	public static Shader IMAGE_V;
	public static Shader GUI_IMAGE_V;

	public static void init() {
		IMAGE_F = load(Shader.ShaderType.FRAGMENT, new ResourceLocation(LDLMod.MODID, "image"));
		IMAGE_V = load(Shader.ShaderType.VERTEX, new ResourceLocation(LDLMod.MODID, "image"));
		GUI_IMAGE_V = load(Shader.ShaderType.VERTEX, new ResourceLocation(LDLMod.MODID, "gui_image"));
	}

	public static Map<ResourceLocation, Shader> CACHE = new HashMap<>();

	public static void reload() {
		for (Shader shader : CACHE.values()) {
			if (shader != null) {
				shader.deleteShader();
			}
		}
		CACHE.clear();
		init();
	}

	public static Shader load(Shader.ShaderType shaderType, ResourceLocation resourceLocation) {
		return CACHE.computeIfAbsent(new ResourceLocation(resourceLocation.getNamespace(), "shaders/" + resourceLocation.getPath() + shaderType.shaderExtension), key -> {
			try {
				return Shader.loadShader(shaderType, key);
			} catch (IOException e) {
				LDLMod.LOGGER.error("load shader {} resource {} failed", shaderType, resourceLocation);
				return null;
			}
		});
	}

	// *** vanilla **//

	private static ShaderInstance particleShader;

	public static ShaderInstance getParticleShader() {
		return particleShader;
	}

	public static void registerVanillaShaders(RegisterShadersEvent event) {
		ResourceManager resourceManager = event.getResourceManager();
		try {
			event.registerShader(new ShaderInstance(resourceManager, new ResourceLocation(LDLMod.MODID, "particle"), DefaultVertexFormat.PARTICLE), shaderInstance -> particleShader = shaderInstance);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
