package com.lowdragmc.lowdraglib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lowdragmc.lowdraglib.client.ClientProxy;
import com.lowdragmc.lowdraglib.json.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(LDLMod.MODID)
public class LDLMod {
    public static final String MODID = "ldlib";
    public static final Logger LOGGER = LogManager.getLogger("LowDragLib");
    public static final String MODID_JEI = "jei";
    public static final String MODID_RUBIDIUM = "rubidium";
    public static final String MODID_REI = "roughlyenoughitems";
    public static final Random random = new Random();
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(IGuiTextureTypeAdapter.INSTANCE)
            .registerTypeAdapter(ItemStack.class, ItemStackTypeAdapter.INSTANCE)
            .registerTypeAdapter(FluidStack.class, FluidStackTypeAdapter.INSTANCE)
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create();


    public LDLMod() {
        DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    }

    public static boolean isClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    public static boolean isRemote() {
        if (isClient()) {
            return Minecraft.getInstance().isSameThread();
        }
        return false;
    }

    public static boolean isModLoaded(String mod) {
        return ModList.get().isLoaded(mod);
    }

    public static boolean isJeiLoaded() {
        if (isModLoaded(MODID_JEI)) return true;
        try {
            Class.forName("mezz.jei.core.config.GiveMode");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public static boolean isReiLoaded() {
        if (isModLoaded(MODID_REI)) return true;
        try {
            Class.forName("me.shedaniel.rei.api.common.entry.EntryStack");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

}
