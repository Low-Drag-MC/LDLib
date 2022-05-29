package com.lowdragmc.lowdraglib;

import com.lowdragmc.lowdraglib.client.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
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
    public static final Random random = new Random();

    public LDLMod() {
        System.setProperty("java.awt.headless", "false");
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
}
