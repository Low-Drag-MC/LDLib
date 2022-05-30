package com.lowdragmc.lowdraglib;

import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import com.lowdragmc.lowdraglib.networking.LDLNetworking;
import com.lowdragmc.lowdraglib.test.TestBlock;
import com.lowdragmc.lowdraglib.test.TestBlockEntity;
import com.lowdragmc.lowdraglib.test.TestItem;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class CommonProxy {
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, LDLMod.MODID);

    public CommonProxy() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.register(this);
        LDLNetworking.init();
        UIFactory.register(BlockEntityUIFactory.INSTANCE);
        PARTICLE_TYPES.register(eventBus);
    }
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(TestBlock.BLOCK);
    }

    public static BlockEntityType<TestBlockEntity> testType;
    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
        IForgeRegistry<BlockEntityType<?>> registry = event.getRegistry();
        testType = BlockEntityType.Builder.of(TestBlockEntity::new, TestBlock.BLOCK).build(null);
        testType.setRegistryName("ldlib:testblock");
        registry.register(testType);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(TestItem.ITEM);
    }
}
