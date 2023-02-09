package net.soko.pyrotechnics.datagen;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.soko.pyrotechnics.PyroTechnics;

@Mod.EventBusSubscriber(modid = PyroTechnics.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PyrotechnicsData {

    @SubscribeEvent
    public static void onDataGen(GatherDataEvent event){
        event.getGenerator().addProvider(event.includeServer(), new PyrotechnicsLootTableProvider(event.getGenerator().getPackOutput()));
    }
}
