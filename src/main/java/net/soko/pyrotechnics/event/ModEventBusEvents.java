package net.soko.pyrotechnics.event;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.soko.pyrotechnics.PyroTechnics;
import net.soko.pyrotechnics.capability.fieriness.ChunkFieriness;
import net.soko.pyrotechnics.capability.fieriness.ChunkFierinessProvider;
import net.soko.pyrotechnics.particle.ModParticles;
import net.soko.pyrotechnics.particle.custom.EmberParticles;

@Mod.EventBusSubscriber(modid = PyroTechnics.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(ModParticles.EMBER.get(), EmberParticles.Provider::new);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesLevelChunk(AttachCapabilitiesEvent<LevelChunk> event) {
        if (event.getObject() instanceof LevelChunk) {
            if (!event.getObject().getCapability(ChunkFierinessProvider.FIERINESS_CAPABILITY).isPresent()) {
                event.addCapability(new ResourceLocation(PyroTechnics.MOD_ID, "fieriness"), new ChunkFierinessProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ChunkFieriness.class);
    }
}
