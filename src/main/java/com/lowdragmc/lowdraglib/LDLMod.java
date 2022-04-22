package com.lowdragmc.lowdraglib;

import com.lowdragmc.lowdraglib.client.ClientProxy;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LDLMod.MODID)
public class LDLMod {
    public static final String MODID = "ldlib";
    public static final Logger LOGGER = LogManager.getLogger("LowDragLib");

    public LDLMod() {
        DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    }

}
