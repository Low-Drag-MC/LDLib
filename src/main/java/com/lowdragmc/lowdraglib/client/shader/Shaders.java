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
	public static Shader SCREEN_V;
	public static Shader ROUND_F;
	public static Shader PANEL_BG_F;
	public static Shader ROUND_BOX_F;
	public static Shader PROGRESS_ROUND_BOX_F;

	public static void init() {
		IMAGE_F = load(Shader.ShaderType.FRAGMENT, new ResourceLocation(LDLMod.MODID, "image"));
		IMAGE_V = load(Shader.ShaderType.VERTEX, new ResourceLocation(LDLMod.MODID, "image"));
		GUI_IMAGE_V = load(Shader.ShaderType.VERTEX, new ResourceLocation(LDLMod.MODID, "gui_image"));
		SCREEN_V = load(Shader.ShaderType.VERTEX, new ResourceLocation(LDLMod.MODID, "screen"));
		ROUND_F = load(Shader.ShaderType.FRAGMENT, new ResourceLocation(LDLMod.MODID, "round"));
		PANEL_BG_F = load(Shader.ShaderType.FRAGMENT, new ResourceLocation(LDLMod.MODID, "panel_bg"));
		ROUND_BOX_F = load(Shader.ShaderType.FRAGMENT, new ResourceLocation(LDLMod.MODID, "round_box"));
		PROGRESS_ROUND_BOX_F = load(Shader.ShaderType.FRAGMENT, new ResourceLocation(LDLMod.MODID, "progress_round_box"));
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
				Shader shader = Shader.loadShader(shaderType, key);
				LDLMod.LOGGER.debug("load shader {} resource {} success", shaderType, resourceLocation);
				return shader;
			} catch (IOException e) {
				LDLMod.LOGGER.error("load shader {} resource {} failed", shaderType, resourceLocation);
				LDLMod.LOGGER.error("caused by ", e);
				return IMAGE_F;
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
