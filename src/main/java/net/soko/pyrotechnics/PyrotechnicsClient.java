package net.soko.pyrotechnics;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.soko.pyrotechnics.event.ClientEvents;

public class PyrotechnicsClient {
    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(ClientEvents.class);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onFogAndSkyColors);
    }
}
