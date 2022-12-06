package com.lowdragmc.lowdraglib;

import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import com.lowdragmc.lowdraglib.networking.LDLNetworking;
import com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries;
import com.lowdragmc.lowdraglib.test.TestBlock;
import com.lowdragmc.lowdraglib.test.TestBlockEntity;
import com.lowdragmc.lowdraglib.test.TestItem;
import com.lowdragmc.lowdraglib.test.TestNodeEditor;
import net.minecraft.commands.Commands;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class CommonProxy {
    protected static final boolean DEBUG = !FMLLoader.isProduction();

    public CommonProxy() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
        LDLNetworking.init();
        UIFactory.register(BlockEntityUIFactory.INSTANCE);
        UIFactory.register(HeldItemUIFactory.INSTANCE);
        UIFactory.register(TestNodeEditor.instance);
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent e) {
        e.enqueueWork(TypedPayloadRegistries::init);
    }

    @SubscribeEvent
    public void loadComplete(FMLLoadCompleteEvent e) {
        e.enqueueWork(TypedPayloadRegistries::postInit);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        if (DEBUG) {
            IForgeRegistry<Block> registry = event.getRegistry();
            registry.register(TestBlock.BLOCK);
        }
    }

    public static BlockEntityType<TestBlockEntity> testType;
    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
        if (DEBUG) {
            IForgeRegistry<BlockEntityType<?>> registry = event.getRegistry();
            testType = BlockEntityType.Builder.of(TestBlockEntity::new, TestBlock.BLOCK).build(null);
            testType.setRegistryName("ldlib:testblock");
            registry.register(testType);
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        if (DEBUG) {
            IForgeRegistry<Item> registry = event.getRegistry();
            registry.register(TestItem.ITEM);
        }
    }

    public void registerCommand(RegisterCommandsEvent event) {
        if (DEBUG) {
            event.getDispatcher().register(Commands.literal("editor").executes(context -> {
                TestNodeEditor.instance.openUI(TestNodeEditor.instance, context.getSource().getPlayerOrException());
                return 1;
            }));
        }
    }
}
