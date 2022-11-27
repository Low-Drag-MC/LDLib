package com.lowdragmc.lowdraglib.events;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.async.AsyncThreadData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author KilaBash
 * @date 2022/11/27
 * @implNote CommonListeners
 */
@Mod.EventBusSubscriber(modid = LDLMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonListeners {

    @SubscribeEvent
    public static void onWorldUnLoad(WorldEvent.Unload event) {
        LevelAccessor world = event.getWorld();
        if (!world.isClientSide() && world instanceof ServerLevel serverLevel) {
            AsyncThreadData.getOrCreate(serverLevel).releaseExecutorService();
        }
    }

}
