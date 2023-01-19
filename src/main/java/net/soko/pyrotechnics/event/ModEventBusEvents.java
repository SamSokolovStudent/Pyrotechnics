package net.soko.pyrotechnics.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;
import net.soko.pyrotechnics.PyroTechnics;
import net.soko.pyrotechnics.particle.ModParticles;
import net.soko.pyrotechnics.particle.custom.EmberParticles;

@Mod.EventBusSubscriber(modid = PyroTechnics.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void regiserParticleFactories(final RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(ModParticles.EMBER.get(), EmberParticles.Provider::new);
    }
}
