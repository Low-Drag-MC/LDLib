package com.lowdragmc.lowdraglib.rei;


import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIReiHandler;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.forge.REIPluginCommon;

/**
 * @author KilaBash
 * @date 2022/11/27
 * @implNote REIPlugin
 */
@REIPluginCommon
public class REIPlugin implements REIClientPlugin {

    private static final ModularUIReiHandler modularUIGuiHandler = new ModularUIReiHandler();

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerFocusedStack(modularUIGuiHandler);
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(ModularUIGuiContainer.class, modularUIGuiHandler);
    }
}
